package com.th.learningenglish.service;

import static com.th.learningenglish.security.HmacUtil.hmacSHA256;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.th.learningenglish.pojo.Payments;
import com.th.learningenglish.pojo.UserVips;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.pojo.VipPackages;
import com.th.learningenglish.repository.PaymentRepository;
import com.th.learningenglish.repository.UserVipRepository;
import com.th.learningenglish.repository.UserRepository;
import com.th.learningenglish.repository.VipPackageRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository repository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private VipPackageRepository vipPackageRepository;
	@Autowired
	private UserVipRepository userVipRepository;

	@Value("${momo.partnerCode:}")
	private String momoPartnerCode;

	@Value("${momo.endpoint:}")
	private String momoEndpoint;

	@Value("${momo.accessKey:}")
	private String momoAccessKey;

	@Value("${momo.secretKey:}")
	private String momoSecretKey;

	@Value("${momo.redirectUrl:}")
	private String momoRedirectUrl;

	@Value("${momo.ipnUrl:}")
	private String momoIpnUrl;

	public List<Payments> findAll() {
		return repository.findAll();
	}

	public Payments findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
	}

	public Payments create(Payments item) {
		resolveAssociations(item);
		return repository.save(item);
	}

	@Transactional
	public Map<String, Object> processPayment(Long userId, Long vipPackageId) {
		Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		VipPackages vipPackage = vipPackageRepository.findById(vipPackageId)
				.orElseThrow(() -> new RuntimeException("VIP package not found"));
		if (!vipPackage.getIsActive()) {
			throw new RuntimeException("Package not available");
		}

		Payments payment = new Payments();
		payment.setUser(user);
		payment.setVipPackage(vipPackage);
		payment.setPaymentMethod(Payments.PaymentMethod.MOMO);
		payment.setAmount(vipPackage.getPrice());
		payment.setTransactionCode(generateTransactionCode());
		payment.setCreatedAt(LocalDateTime.now());
		payment.setStatus(Payments.Status.PENDING);

		Payments savedPayment = repository.save(payment);
		String payUrl = createMoMoPaymentUrl(savedPayment);

		Map<String, Object> data = new HashMap<>();
		data.put("paymentId", savedPayment.getId());
		data.put("transactionCode", savedPayment.getTransactionCode());
		data.put("amount", normalizeAmount(savedPayment.getAmount()).setScale(0, RoundingMode.HALF_UP));
		data.put("paymentMethod", savedPayment.getPaymentMethod().name());
		data.put("status", savedPayment.getStatus().name());
		data.put("payUrl", payUrl);
		return data;
	}

	@Transactional
	public Payments confirmPayment(String transactionCode) {
		Payments payment = repository.findByTransactionCode(transactionCode)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		if (payment.getStatus() == Payments.Status.PAID) {
			createUserVipIfNeeded(payment);
			return payment;
		}

		payment.setStatus(Payments.Status.PAID);
		Payments saved = repository.save(payment);
		createUserVipIfNeeded(saved);
		return saved;
	}

	@Transactional
	public Payments confirmPaymentFromMomoIpn(Map<String, String> ipnPayload) {
		validateMomoConfig();
		String signature = safe(ipnPayload.get("signature"));
		if (signature.isBlank()) {
			throw new RuntimeException("Missing signature");
		}
		String rawSignature = buildMomoIpnRawSignature(ipnPayload);
		String calculatedSignature;
		try {
			calculatedSignature = hmacSHA256(rawSignature, momoSecretKey);
		} catch (Exception e) {
			throw new RuntimeException("Cannot verify signature", e);
		}
		if (!signature.equals(calculatedSignature)) {
			throw new RuntimeException("Invalid MoMo signature");
		}

		int resultCode = parseInt(ipnPayload.get("resultCode"));
		String transactionCode = safe(ipnPayload.get("orderId"));
		if (transactionCode.isBlank()) {
			throw new RuntimeException("Missing orderId");
		}

		Payments payment = repository.findByTransactionCode(transactionCode)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		long expectedAmount = normalizeAmount(payment.getAmount()).setScale(0, RoundingMode.HALF_UP).longValue();
		long callbackAmount = parseLong(ipnPayload.get("amount"));
		if (callbackAmount <= 0 || callbackAmount != expectedAmount) {
			throw new RuntimeException("Invalid payment amount");
		}

		if (resultCode != 0) {
			payment.setStatus(Payments.Status.FAILED);
			return repository.save(payment);
		}

		if (payment.getStatus() != Payments.Status.PAID) {
			payment.setStatus(Payments.Status.PAID);
			payment = repository.save(payment);
		}
		createUserVipIfNeeded(payment);
		return payment;
	}

	public Payments update(Long id, Payments payload) {
		Payments c = findById(id);
		if (payload.getAmount() != null) {
			c.setAmount(payload.getAmount());
		}
		if (payload.getPaymentMethod() != null) {
			c.setPaymentMethod(payload.getPaymentMethod());
		}
		if (payload.getTransactionCode() != null) {
			c.setTransactionCode(payload.getTransactionCode());
		}
		if (payload.getStatus() != null) {
			c.setStatus(payload.getStatus());
		}
		if (payload.getUser() != null && payload.getUser().getId() != null) {
			c.setUser(userRepository.findById(payload.getUser().getId())
					.orElseThrow(() -> new RuntimeException("User not found")));
		}
		if (payload.getVipPackage() != null && payload.getVipPackage().getId() != null) {
			c.setVipPackage(vipPackageRepository.findById(payload.getVipPackage().getId())
					.orElseThrow(() -> new RuntimeException("VIP package not found")));
		}
		return repository.save(c);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public String createMoMoPaymentUrl(Payments payment) {
		try {
			validateMomoConfig();
			Map<String, String> params = new HashMap<>();
			params.put("partnerCode", momoPartnerCode);
			params.put("requestId", generateTransactionCode());
			params.put("amount", normalizeAmount(payment.getAmount()).setScale(0, RoundingMode.HALF_UP).toPlainString());
			params.put("orderId", payment.getTransactionCode());
			params.put("orderInfo", "VIP package " + payment.getVipPackage().getId());
			params.put("redirectUrl", momoRedirectUrl);
			params.put("ipnUrl", momoIpnUrl);
			params.put("requestType", "payWithMethod");
			params.put("extraData", String.valueOf(payment.getId()));

			String rawSignature = "accessKey=" + momoAccessKey
					+ "&amount=" + params.get("amount")
					+ "&extraData=" + params.get("extraData")
					+ "&ipnUrl=" + params.get("ipnUrl")
					+ "&orderId=" + params.get("orderId")
					+ "&orderInfo=" + params.get("orderInfo")
					+ "&partnerCode=" + params.get("partnerCode")
					+ "&redirectUrl=" + params.get("redirectUrl")
					+ "&requestId=" + params.get("requestId")
					+ "&requestType=" + params.get("requestType");

			String signature = hmacSHA256(rawSignature, momoSecretKey);
			params.put("signature", signature);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
			ResponseEntity<Map> response = restTemplate.postForEntity(momoEndpoint, entity, Map.class);
			Map<?, ?> body = response.getBody();

			if (body == null || body.get("payUrl") == null) {
				throw new RuntimeException("MoMo response missing payUrl");
			}
			Object resultCode = body.get("resultCode");
			if (resultCode != null && !"0".equals(String.valueOf(resultCode))) {
				Object message = body.get("message");
				throw new RuntimeException("MoMo payment error: " + (message != null ? message : "Unknown error"));
			}
			return String.valueOf(body.get("payUrl"));
		} catch (Exception e) {
			throw new RuntimeException("MoMo payment error: " + e.getMessage(), e);
		}
	}

	private void resolveAssociations(Payments p) {
		if (p.getUser() == null || p.getUser().getId() == null) {
			throw new RuntimeException("user id is required");
		}
		if (p.getVipPackage() == null || p.getVipPackage().getId() == null) {
			throw new RuntimeException("vip_package id is required");
		}
		p.setUser(userRepository.findById(p.getUser().getId())
				.orElseThrow(() -> new RuntimeException("User not found")));
		p.setVipPackage(vipPackageRepository.findById(p.getVipPackage().getId())
				.orElseThrow(() -> new RuntimeException("VIP package not found")));
	}

	private String generateTransactionCode() {
		return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private BigDecimal normalizeAmount(BigDecimal amount) {
		return amount != null ? amount : BigDecimal.ZERO;
	}

	private String buildMomoIpnRawSignature(Map<String, String> payload) {
		Map<String, String> fields = new LinkedHashMap<>();
		fields.put("accessKey", momoAccessKey);
		fields.put("amount", safe(payload.get("amount")));
		fields.put("extraData", safe(payload.get("extraData")));
		fields.put("message", safe(payload.get("message")));
		fields.put("orderId", safe(payload.get("orderId")));
		fields.put("orderInfo", safe(payload.get("orderInfo")));
		fields.put("orderType", safe(payload.get("orderType")));
		fields.put("partnerCode", safe(payload.get("partnerCode")));
		fields.put("payType", safe(payload.get("payType")));
		fields.put("requestId", safe(payload.get("requestId")));
		fields.put("responseTime", safe(payload.get("responseTime")));
		fields.put("resultCode", safe(payload.get("resultCode")));
		fields.put("transId", safe(payload.get("transId")));
		StringBuilder raw = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> e : fields.entrySet()) {
			if (!first) {
				raw.append('&');
			}
			raw.append(e.getKey()).append('=').append(e.getValue());
			first = false;
		}
		return raw.toString();
	}

	private int parseInt(String val) {
		try {
			return Integer.parseInt(safe(val));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private long parseLong(String val) {
		try {
			return Long.parseLong(safe(val));
		} catch (NumberFormatException e) {
			return -1L;
		}
	}

	private String safe(String val) {
		return val == null ? "" : val.trim();
	}

	private void validateMomoConfig() {
		if (safe(momoPartnerCode).isBlank() || safe(momoAccessKey).isBlank() || safe(momoSecretKey).isBlank()
				|| safe(momoEndpoint).isBlank() || safe(momoRedirectUrl).isBlank() || safe(momoIpnUrl).isBlank()) {
			throw new RuntimeException("MoMo config is missing (partnerCode/accessKey/secretKey/endpoint/redirectUrl/ipnUrl)");
		}
	}

	private void createUserVipIfNeeded(Payments payment) {
		if (userVipRepository.existsByPaymentId(payment.getId())) {
			return;
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startAt = userVipRepository.findTopByUserIdOrderByExpireAtDesc(payment.getUser().getId())
				.map(UserVips::getExpireAt)
				.filter(expire -> expire.isAfter(now))
				.orElse(now);
		LocalDateTime expireAt = startAt.plusMonths(payment.getVipPackage().getMonths());

		UserVips userVip = new UserVips();
		userVip.setPayment(payment);
		userVip.setUser(payment.getUser());
		userVip.setVipPackage(payment.getVipPackage());
		userVip.setStartAt(startAt);
		userVip.setExpireAt(expireAt);
		userVipRepository.save(userVip);
	}
}

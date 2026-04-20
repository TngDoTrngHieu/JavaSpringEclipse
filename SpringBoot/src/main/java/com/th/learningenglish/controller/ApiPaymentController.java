package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.pojo.Payments;
import com.th.learningenglish.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class ApiPaymentController {
	@Autowired
	private PaymentService paymentService;

	@GetMapping
	public List<Payments> getAll() {
		return paymentService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Payments> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(paymentService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public Payments create(@RequestBody Payments item) {
		return paymentService.create(item);
	}

	@PostMapping("/process")
	public ResponseEntity<?> processPayment(@RequestBody ProcessPaymentRequest request) {
		try {
			return ResponseEntity.ok(paymentService.processPayment(request.getUserId(), request.getVipPackageId()));
		} catch (RuntimeException ex) {
			return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
		}
	}

	@GetMapping("/momo/return")
	public ResponseEntity<?> momoReturn(@RequestParam Map<String, String> params) {
		String orderId = params.get("orderId");
		String resultCode = params.get("resultCode");

		boolean success = "0".equals(resultCode);

		String redirectUrl = "http://localhost:3000/upgrade-vip" + "?success=" + success + "&orderId=" + orderId;

		return ResponseEntity.status(302).header("Location", redirectUrl).build();
	}

	@PostMapping("/confirm/momo-ipn")
	public ResponseEntity<?> confirmFromMomoIpn(@RequestBody Map<String, String> ipnPayload) {
		try {
			Payments payment = paymentService.confirmPaymentFromMomoIpn(ipnPayload);
			return ResponseEntity.ok(Map.of("message", "IPN processed", "transactionCode", payment.getTransactionCode(),
					"status", payment.getStatus().name()));
		} catch (RuntimeException ex) {
			return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage(), "resultCode", 1));
		}
	}

	public static class ProcessPaymentRequest {
		private Long userId;
		private Long vipPackageId;

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Long getVipPackageId() {
			return vipPackageId;
		}

		public void setVipPackageId(Long vipPackageId) {
			this.vipPackageId = vipPackageId;
		}
	}
}

package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.Payments;
import com.th.learningenglish.repository.PaymentRepository;
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
}

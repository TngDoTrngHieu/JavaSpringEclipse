package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.Payments;
import com.th.learningenglish.repository.PaymentRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository repository;

	public List<Payments> findAll() { return repository.findAll(); }
	public Payments findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found")); }
	public Payments create(Payments item) { return repository.save(item); }
	public Payments update(Long id, Payments payload) {
		Payments c = findById(id);
		c.setAmount(payload.getAmount()); c.setMonths(payload.getMonths()); c.setPaymentMethod(payload.getPaymentMethod()); c.setTransactionCode(payload.getTransactionCode()); c.setStatus(payload.getStatus()); c.setUser(payload.getUser());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
		try { return ResponseEntity.ok(paymentService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public Payments create(@RequestBody Payments item) {
		return paymentService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Payments> update(@PathVariable Long id, @RequestBody Payments payload) {
		try { return ResponseEntity.ok(paymentService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { paymentService.findById(id); paymentService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

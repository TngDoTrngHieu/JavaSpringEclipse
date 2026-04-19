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

import com.th.learningenglish.pojo.UserAnswers;
import com.th.learningenglish.service.UserAnswerService;

@RestController
@RequestMapping("/api/user-answers")
public class ApiUserAnswerController {
	@Autowired
	private UserAnswerService userAnswerService;

	@GetMapping
	public List<UserAnswers> getAll() {
		return userAnswerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserAnswers> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(userAnswerService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public UserAnswers create(@RequestBody UserAnswers item) {
		return userAnswerService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserAnswers> update(@PathVariable Long id, @RequestBody UserAnswers payload) {
		try { return ResponseEntity.ok(userAnswerService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { userAnswerService.findById(id); userAnswerService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

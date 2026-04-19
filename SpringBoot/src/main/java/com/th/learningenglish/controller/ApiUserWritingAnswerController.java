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

import com.th.learningenglish.pojo.UserWritingAnswers;
import com.th.learningenglish.service.UserWritingAnswerService;

@RestController
@RequestMapping("/api/user-writing-answers")
public class ApiUserWritingAnswerController {
	@Autowired
	private UserWritingAnswerService userWritingAnswerService;

	@GetMapping
	public List<UserWritingAnswers> getAll() {
		return userWritingAnswerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserWritingAnswers> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(userWritingAnswerService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public UserWritingAnswers create(@RequestBody UserWritingAnswers item) {
		return userWritingAnswerService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserWritingAnswers> update(@PathVariable Long id, @RequestBody UserWritingAnswers payload) {
		try { return ResponseEntity.ok(userWritingAnswerService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { userWritingAnswerService.findById(id); userWritingAnswerService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

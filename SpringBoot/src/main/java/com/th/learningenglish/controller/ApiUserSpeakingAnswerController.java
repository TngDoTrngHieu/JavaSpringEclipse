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

import com.th.learningenglish.pojo.UserSpeakingAnswers;
import com.th.learningenglish.service.UserSpeakingAnswerService;

@RestController
@RequestMapping("/api/user-speaking-answers")
public class ApiUserSpeakingAnswerController {
	@Autowired
	private UserSpeakingAnswerService userSpeakingAnswerService;

	@GetMapping
	public List<UserSpeakingAnswers> getAll() {
		return userSpeakingAnswerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserSpeakingAnswers> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(userSpeakingAnswerService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public UserSpeakingAnswers create(@RequestBody UserSpeakingAnswers item) {
		return userSpeakingAnswerService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserSpeakingAnswers> update(@PathVariable Long id, @RequestBody UserSpeakingAnswers payload) {
		try { return ResponseEntity.ok(userSpeakingAnswerService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { userSpeakingAnswerService.findById(id); userSpeakingAnswerService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

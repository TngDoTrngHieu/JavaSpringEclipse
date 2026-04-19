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

import com.th.learningenglish.pojo.PracticeSessions;
import com.th.learningenglish.service.PracticeSessionService;

@RestController
@RequestMapping("/api/practice-sessions")
public class ApiPracticeSessionController {
	@Autowired
	private PracticeSessionService practiceSessionService;

	@GetMapping
	public List<PracticeSessions> getAll() {
		return practiceSessionService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PracticeSessions> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(practiceSessionService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public PracticeSessions create(@RequestBody PracticeSessions item) {
		return practiceSessionService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PracticeSessions> update(@PathVariable Long id, @RequestBody PracticeSessions payload) {
		try { return ResponseEntity.ok(practiceSessionService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { practiceSessionService.findById(id); practiceSessionService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

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

import com.th.learningenglish.pojo.ProgressTrackers;
import com.th.learningenglish.service.ProgressTrackerService;

@RestController
@RequestMapping("/api/progress-trackers")
public class ApiProgressTrackerController {
	@Autowired
	private ProgressTrackerService progressTrackerService;

	@GetMapping
	public List<ProgressTrackers> getAll() {
		return progressTrackerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProgressTrackers> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(progressTrackerService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public ProgressTrackers create(@RequestBody ProgressTrackers item) {
		return progressTrackerService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProgressTrackers> update(@PathVariable Long id, @RequestBody ProgressTrackers payload) {
		try { return ResponseEntity.ok(progressTrackerService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { progressTrackerService.findById(id); progressTrackerService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

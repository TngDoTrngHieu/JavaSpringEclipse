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

import com.th.learningenglish.pojo.StudyPlans;
import com.th.learningenglish.service.StudyPlanService;

@RestController
@RequestMapping("/api/study-plans")
public class ApiStudyPlanController {
	@Autowired
	private StudyPlanService studyPlanService;

	@GetMapping
	public List<StudyPlans> getAll() {
		return studyPlanService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudyPlans> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(studyPlanService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public StudyPlans create(@RequestBody StudyPlans item) {
		return studyPlanService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<StudyPlans> update(@PathVariable Long id, @RequestBody StudyPlans payload) {
		try { return ResponseEntity.ok(studyPlanService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { studyPlanService.findById(id); studyPlanService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

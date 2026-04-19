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

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.service.LessonTypeService;

@RestController
@RequestMapping("/api/lesson-types")
public class ApiLessonTypeController {
	@Autowired
	private LessonTypeService lessonTypeService;

	@GetMapping
	public List<LessonTypes> getAll() {
		return lessonTypeService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<LessonTypes> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(lessonTypeService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public LessonTypes create(@RequestBody LessonTypes item) {
		return lessonTypeService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<LessonTypes> update(@PathVariable Long id, @RequestBody LessonTypes payload) {
		try {
			return ResponseEntity.ok(lessonTypeService.update(id, payload));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try {
			lessonTypeService.findById(id);
			lessonTypeService.delete(id);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}
}

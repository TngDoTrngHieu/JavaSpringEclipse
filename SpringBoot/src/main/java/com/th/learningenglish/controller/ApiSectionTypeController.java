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

import com.th.learningenglish.pojo.SectionTypes;
import com.th.learningenglish.service.SectionTypeService;

@RestController
@RequestMapping("/api/section-types")
public class ApiSectionTypeController {
	@Autowired
	private SectionTypeService sectionTypeService;

	@GetMapping
	public List<SectionTypes> getAll() {
		return sectionTypeService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<SectionTypes> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(sectionTypeService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public SectionTypes create(@RequestBody SectionTypes item) {
		return sectionTypeService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<SectionTypes> update(@PathVariable Long id, @RequestBody SectionTypes payload) {
		try {
			return ResponseEntity.ok(sectionTypeService.update(id, payload));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try {
			sectionTypeService.findById(id);
			sectionTypeService.delete(id);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}
}

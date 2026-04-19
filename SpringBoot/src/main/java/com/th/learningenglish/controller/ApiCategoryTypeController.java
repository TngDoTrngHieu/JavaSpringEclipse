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

import com.th.learningenglish.pojo.CategoryTypes;
import com.th.learningenglish.service.CategoryTypeService;

@RestController
@RequestMapping("/api/category-types")
public class ApiCategoryTypeController {
	@Autowired
	private CategoryTypeService categoryTypeService;

	@GetMapping
	public List<CategoryTypes> getAll() {
		return categoryTypeService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryTypes> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(categoryTypeService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public CategoryTypes create(@RequestBody CategoryTypes item) {
		return categoryTypeService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryTypes> update(@PathVariable Long id, @RequestBody CategoryTypes payload) {
		try {
			return ResponseEntity.ok(categoryTypeService.update(id, payload));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try {
			categoryTypeService.findById(id);
			categoryTypeService.delete(id);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}
}

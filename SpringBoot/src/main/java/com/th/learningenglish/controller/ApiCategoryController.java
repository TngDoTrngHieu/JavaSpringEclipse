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

import com.th.learningenglish.pojo.Categories;
import com.th.learningenglish.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class ApiCategoryController {
	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public List<Categories> getAll() {
		return categoryService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Categories> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(categoryService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public Categories create(@RequestBody Categories item) {
		return categoryService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Categories> update(@PathVariable Long id, @RequestBody Categories payload) {
		try {
			return ResponseEntity.ok(categoryService.update(id, payload));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try {
			categoryService.findById(id);
			categoryService.delete(id);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}
}

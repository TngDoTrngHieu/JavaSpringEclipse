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

import com.th.learningenglish.pojo.Vocabularies;
import com.th.learningenglish.service.VocabularyService;

@RestController
@RequestMapping("/api/vocabularies")
public class ApiVocabularyController {
	@Autowired
	private VocabularyService vocabularyService;

	@GetMapping
	public List<Vocabularies> getAll() {
		return vocabularyService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Vocabularies> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(vocabularyService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public Vocabularies create(@RequestBody Vocabularies item) {
		return vocabularyService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Vocabularies> update(@PathVariable Long id, @RequestBody Vocabularies payload) {
		try { return ResponseEntity.ok(vocabularyService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { vocabularyService.findById(id); vocabularyService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

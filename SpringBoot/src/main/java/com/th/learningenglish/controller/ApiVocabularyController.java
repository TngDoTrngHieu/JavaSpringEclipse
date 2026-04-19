package com.th.learningenglish.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.dto.VocabDTO;
import com.th.learningenglish.pojo.Vocabularies;
import com.th.learningenglish.service.VocabularyService;

@RestController
@RequestMapping("/api/vocabularies")
public class ApiVocabularyController {
	@Autowired
	private VocabularyService vocabularyService;

	@GetMapping
	public List<Vocabularies> list(Principal p) {
		return vocabularyService.getByUser(p.getName());
	}

	@PostMapping
	public Vocabularies add(@RequestBody VocabDTO dto, Principal p) {
		return vocabularyService.add(dto, p.getName());
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id, Principal p) {
		vocabularyService.delete(id, p.getName());
	}

	@GetMapping("/search")
	public List<Vocabularies> search(@RequestParam String keyword, Principal p) {
		return vocabularyService.search(keyword, p.getName());
	}
}

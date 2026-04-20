package com.th.learningenglish.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@GetMapping("/{id}")
	public Vocabularies getOne(@PathVariable Long id, Principal p) {
		return vocabularyService.getOne(id, p.getName());
	}

	@PostMapping("/from-ai")
	public Vocabularies saveFromAI(
			@RequestBody Map<String, String> req,
			Principal p) {
		String text = req.get("text");

		Map<String, String> parsed = parseAI(text);

		VocabDTO dto = new VocabDTO();
		dto.setWord(parsed.getOrDefault("word", ""));
		dto.setMeaning(parsed.getOrDefault("meaning", ""));
		dto.setExample(parsed.getOrDefault("example", ""));
		dto.setNote(parsed.getOrDefault("note", ""));

		return vocabularyService.add(dto, p.getName());
	}

	private Map<String, String> parseAI(String text) {
		Map<String, String> map = new HashMap<>();
		if (text == null) {
			return map;
		}

		String[] lines = text.split("\\r?\\n");

		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty())
				continue;

			int idx = trimmed.indexOf(":");
			if (idx > 0) {
				String key = trimmed.substring(0, idx).trim().toLowerCase();
				String val = trimmed.substring(idx + 1).trim();
				if (key.startsWith("word")) {
					map.put("word", val);
				} else if (key.startsWith("meaning")) {
					map.put("meaning", val);
				} else if (key.startsWith("example")) {
					map.put("example", val);
				} else if (key.startsWith("note")) {
					map.put("note", val);
				}
			}
		}

		return map;
	}
}

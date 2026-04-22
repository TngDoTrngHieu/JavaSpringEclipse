package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.service.SectionService;

@RestController
@RequestMapping("/api/sections")
public class ApiSectionController {
	@Autowired
	private SectionService sectionService;

	@GetMapping
	public List<Sections> getSections(@RequestParam(required = false) Long lessonId) {
		if (lessonId != null) {
			return sectionService.getSectionsByLesson(lessonId);
		}
		return sectionService.getAllSections();
	}

	@GetMapping("/{id}")
	public Sections getSection(@PathVariable Long id) {
		return sectionService.getSectionById(id);
	}

	@PostMapping
	public Sections createSection(@RequestBody Sections section) {
		return sectionService.createSection(section);
	}

	@PostMapping(value = "/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> uploadAudio(@RequestParam("audio") MultipartFile audio) throws Exception {
		return Map.of("audioUrl", sectionService.uploadAudio(audio));
	}

	@PutMapping("/{id}")
	public Sections updateSection(@PathVariable Long id, @RequestBody Sections section) {
		return sectionService.updateSection(id, section);
	}

	@DeleteMapping("/{id}")
	public Map<String, String> deleteSection(@PathVariable Long id) {
		sectionService.deleteSection(id);
		return Map.of("message", "Deleted");
	}

	@GetMapping("/lesson/{lessonId}")
	public List<Sections> getSectionsByLesson(@PathVariable Long lessonId) {
		return sectionService.getSectionsByLesson(lessonId);
	}
}

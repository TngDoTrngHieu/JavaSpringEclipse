package com.th.learningenglish.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.service.SectionService;

@RestController
@RequestMapping("/api/sections")
public class ApiSectionController {
	@Autowired
	private SectionService sectionService;

	@GetMapping("/lesson/{lessonId}")
	public List<Sections> getSections(@PathVariable Long lessonId) {
		return sectionService.getSectionsByLesson(lessonId);
	}
}

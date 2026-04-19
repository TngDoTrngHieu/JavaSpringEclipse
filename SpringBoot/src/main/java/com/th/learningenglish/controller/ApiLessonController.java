package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.service.LessonsService;

@RestController
@RequestMapping("/api")
public class ApiLessonController {
	@Autowired
	private LessonsService lessonService;

	@GetMapping("/lessons")
	public List<Lessons> getLessons(@RequestParam Map<String, String> params) {
		return lessonService.getLessons(params);
	}

	@GetMapping("/lessons/{id}")
	public Lessons getLesson(@PathVariable Long id) {
		return lessonService.getLessonById(id);
	}

	@PostMapping("/lessons")
	public Lessons createLesson(@RequestBody Lessons lesson) {
		return lessonService.createLesson(lesson);
	}

	@PutMapping("/lessons/{id}")
	public Lessons updateLesson(@PathVariable Long id, @RequestBody Lessons lesson) {
		return lessonService.updateLesson(id, lesson);
	}

	@DeleteMapping("/lessons/{id}")
	public String deleteLesson(@PathVariable Long id) {
		lessonService.deleteLesson(id);
		return "Deleted";
	}
}

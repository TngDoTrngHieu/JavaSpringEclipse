package com.th.learningenglish.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.pojo.Lessons;
import com.th.learningenglish.repository.LessonRepository;

@Service
public class LessonsService {
	@Autowired
	private LessonRepository lessonRepo;

	public List<Lessons> findAllLesson() {
		return lessonRepo.findAll();
	}

	public List<Lessons> getLessons(Map<String, String> params) {
		String skill = params.get("skill");
		String categoryId = params.get("categoryId");

		if (skill != null) {
			return lessonRepo.findByLessonType_Skill(LessonTypes.Skill.valueOf(skill.toUpperCase()));
		}

		if (categoryId != null) {
			return lessonRepo.findByCategory_Id(Long.parseLong(categoryId));
		}

		return lessonRepo.findAll();
	}

	public Lessons getLessonById(Long id) {
		return lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Lesson not found"));
	}

	public Lessons createLesson(Lessons lesson) {
		return lessonRepo.save(lesson);
	}

	public Lessons updateLesson(Long id, Lessons lesson) {
		Lessons l = getLessonById(id);

		l.setTitle(lesson.getTitle());
		l.setContent(lesson.getContent());
		l.setImageUrl(lesson.getImageUrl());
		l.setCategory(lesson.getCategory());
		l.setLessonType(lesson.getLessonType());

		return lessonRepo.save(l);
	}

	public void deleteLesson(Long id) {
		lessonRepo.deleteById(id);
	}
}

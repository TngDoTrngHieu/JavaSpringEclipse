package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.repository.LessonTypeRepository;

@Service
public class LessonTypeService {
	@Autowired
	private LessonTypeRepository repository;

	public List<LessonTypes> findAll() {
		return repository.findAll();
	}

	public LessonTypes findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Lesson type not found"));
	}

	public LessonTypes create(LessonTypes item) {
		return repository.save(item);
	}

	public LessonTypes update(Long id, LessonTypes payload) {
		LessonTypes current = findById(id);
		current.setName(payload.getName());
		current.setSkill(payload.getSkill());
		return repository.save(current);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
}

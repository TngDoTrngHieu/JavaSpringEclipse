package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.repository.SectionRepository;

public class SectionService {
	@Autowired
	private SectionRepository sectionRepo;

	public List<Sections> getSectionsByLesson(Long lessonId) {
		return sectionRepo.findByLesson_IdOrderByPositionAsc(lessonId);
	}
}

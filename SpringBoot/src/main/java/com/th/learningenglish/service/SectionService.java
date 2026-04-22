package com.th.learningenglish.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.th.learningenglish.pojo.Sections;
import com.th.learningenglish.repository.SectionRepository;

@Service
public class SectionService {
	@Autowired
	private SectionRepository sectionRepo;
	@Autowired
	private R2Service r2Service;

	public List<Sections> getAllSections() {
		return sectionRepo.findAll();
	}

	public List<Sections> getSectionsByLesson(Long lessonId) {
		return sectionRepo.findByLesson_IdOrderByPositionAsc(lessonId);
	}

	public Sections getSectionById(Long id) {
		return sectionRepo.findById(id).orElseThrow(() -> new RuntimeException("Section not found"));
	}

	public Sections createSection(Sections section) {
		return sectionRepo.save(section);
	}

	public Sections updateSection(Long id, Sections section) {
		Sections current = getSectionById(id);
		current.setContent(section.getContent());
		current.setQuestion(section.getQuestion());
		current.setAnswer(section.getAnswer());
		current.setCorrectAnswer(section.getCorrectAnswer());
		current.setSectionType(section.getSectionType());
		current.setLesson(section.getLesson());
		current.setPosition(section.getPosition());
		return sectionRepo.save(current);
	}

	public void deleteSection(Long id) {
		sectionRepo.deleteById(id);
	}

	public String uploadAudio(MultipartFile audio) throws IOException {
		return r2Service.upload(audio);
	}
}

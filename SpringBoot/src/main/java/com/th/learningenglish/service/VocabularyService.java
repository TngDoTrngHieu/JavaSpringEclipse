package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.Vocabularies;
import com.th.learningenglish.repository.VocabularyRepository;

@Service
public class VocabularyService {
	@Autowired
	private VocabularyRepository repository;

	public List<Vocabularies> findAll() { return repository.findAll(); }
	public Vocabularies findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Vocabulary not found")); }
	public Vocabularies create(Vocabularies item) { return repository.save(item); }
	public Vocabularies update(Long id, Vocabularies payload) {
		Vocabularies c = findById(id);
		c.setWord(payload.getWord()); c.setMeaning(payload.getMeaning()); c.setExample(payload.getExample()); c.setNote(payload.getNote()); c.setUser(payload.getUser());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

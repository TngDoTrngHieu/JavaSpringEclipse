package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.SectionTypes;
import com.th.learningenglish.repository.SectionTypeRepository;

@Service
public class SectionTypeService {
	@Autowired
	private SectionTypeRepository repository;

	public List<SectionTypes> findAll() {
		return repository.findAll();
	}

	public SectionTypes findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Section type not found"));
	}

	public SectionTypes create(SectionTypes item) {
		return repository.save(item);
	}

	public SectionTypes update(Long id, SectionTypes payload) {
		SectionTypes current = findById(id);
		current.setName(payload.getName());
		current.setSaveType(payload.getSaveType());
		return repository.save(current);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
}

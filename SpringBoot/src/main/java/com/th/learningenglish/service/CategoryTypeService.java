package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.CategoryTypes;
import com.th.learningenglish.repository.CategoryTypeRepository;

@Service
public class CategoryTypeService {
	@Autowired
	private CategoryTypeRepository repository;

	public List<CategoryTypes> findAll() {
		return repository.findAll();
	}

	public CategoryTypes findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Category type not found"));
	}

	public CategoryTypes create(CategoryTypes item) {
		return repository.save(item);
	}

	public CategoryTypes update(Long id, CategoryTypes payload) {
		CategoryTypes current = findById(id);
		current.setName(payload.getName());
		return repository.save(current);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
}

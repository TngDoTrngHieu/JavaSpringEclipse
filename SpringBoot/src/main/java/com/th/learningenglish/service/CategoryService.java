package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.Categories;
import com.th.learningenglish.repository.CategoryRepository;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository repository;

	public List<Categories> findAll() {
		return repository.findAll();
	}

	public Categories findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
	}

	public Categories create(Categories item) {
		return repository.save(item);
	}

	public Categories update(Long id, Categories payload) {
		Categories current = findById(id);
		current.setName(payload.getName());
		current.setCategoryType(payload.getCategoryType());
		return repository.save(current);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
}

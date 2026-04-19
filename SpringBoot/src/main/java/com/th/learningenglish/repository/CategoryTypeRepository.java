package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.CategoryTypes;

public interface CategoryTypeRepository extends JpaRepository<CategoryTypes, Long> {
}

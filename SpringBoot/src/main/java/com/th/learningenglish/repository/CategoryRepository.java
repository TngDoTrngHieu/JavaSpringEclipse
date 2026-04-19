package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Categories;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
}

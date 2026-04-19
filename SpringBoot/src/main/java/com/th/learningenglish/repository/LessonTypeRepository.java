package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.LessonTypes;

public interface LessonTypeRepository extends JpaRepository<LessonTypes, Long> {
}

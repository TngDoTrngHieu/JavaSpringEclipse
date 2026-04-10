package com.th.learningenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Sections;

public interface SectionRepository extends JpaRepository<Sections, Long> {
	List<Sections> findByLesson_IdOrderByPositionAsc(Long Id);
}

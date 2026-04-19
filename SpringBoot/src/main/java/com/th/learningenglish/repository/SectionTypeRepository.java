package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.SectionTypes;

public interface SectionTypeRepository extends JpaRepository<SectionTypes, Long> {
}

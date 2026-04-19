package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Vocabularies;

public interface VocabularyRepository extends JpaRepository<Vocabularies, Long> {
}

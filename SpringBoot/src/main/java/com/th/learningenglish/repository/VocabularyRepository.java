package com.th.learningenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.pojo.Vocabularies;

public interface VocabularyRepository extends JpaRepository<Vocabularies, Long> {
	boolean existsByUserAndWordIgnoreCase(Users user, String word);

	List<Vocabularies> findByUser(Users user);

	List<Vocabularies> findByUserAndWordContainingIgnoreCase(Users user, String keyword);
}

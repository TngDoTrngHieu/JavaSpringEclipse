package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.UserAnswers;

public interface UserAnswerRepository extends JpaRepository<UserAnswers, Long> {
}

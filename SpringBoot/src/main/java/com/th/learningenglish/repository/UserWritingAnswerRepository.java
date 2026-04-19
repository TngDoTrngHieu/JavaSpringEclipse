package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.UserWritingAnswers;

public interface UserWritingAnswerRepository extends JpaRepository<UserWritingAnswers, Long> {
}

package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.UserSpeakingAnswers;

public interface UserSpeakingAnswerRepository extends JpaRepository<UserSpeakingAnswers, Long> {
}

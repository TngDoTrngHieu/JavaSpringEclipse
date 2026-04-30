package com.th.learningenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.UserAnswers;

public interface UserAnswerRepository extends JpaRepository<UserAnswers, Long> {
	List<UserAnswers> findBySession_User_UsernameOrderByCreatedAtDesc(String username);
}

package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.PracticeSessions;

public interface PracticeSessionRepository extends JpaRepository<PracticeSessions, Long> {
}

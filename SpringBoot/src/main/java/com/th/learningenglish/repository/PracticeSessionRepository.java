package com.th.learningenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.LessonTypes;
import com.th.learningenglish.pojo.PracticeSessions;

public interface PracticeSessionRepository extends JpaRepository<PracticeSessions, Long> {
	List<PracticeSessions> findByUser_IdOrderByCreatedAtDesc(Long userId);

	List<PracticeSessions> findByUser_IdAndLesson_LessonType_Skill(Long userId, LessonTypes.Skill skill);
}

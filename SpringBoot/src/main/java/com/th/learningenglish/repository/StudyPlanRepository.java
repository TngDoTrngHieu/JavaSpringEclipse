package com.th.learningenglish.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.StudyPlans;

public interface StudyPlanRepository extends JpaRepository<StudyPlans, Long> {
	Optional<StudyPlans> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

}

package com.th.learningenglish.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.ProgressTrackers;

public interface ProgressTrackerRepository extends JpaRepository<ProgressTrackers, Long> {
	Optional<ProgressTrackers> findByUser_IdAndStudyPlan_IdAndSkill(Long userId, Long planId,
			ProgressTrackers.Skill skill);

	List<ProgressTrackers> findByUser_IdAndStudyPlan_Id(Long userId, Long planId);
}

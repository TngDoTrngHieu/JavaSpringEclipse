package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.StudyPlans;

public interface StudyPlanRepository extends JpaRepository<StudyPlans, Long> {
}

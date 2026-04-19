package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.ProgressTrackers;

public interface ProgressTrackerRepository extends JpaRepository<ProgressTrackers, Long> {
}

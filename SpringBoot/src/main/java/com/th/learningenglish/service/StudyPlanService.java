package com.th.learningenglish.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.ProgressTrackers;
import com.th.learningenglish.pojo.StudyPlans;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.repository.ProgressTrackerRepository;
import com.th.learningenglish.repository.StudyPlanRepository;
import com.th.learningenglish.repository.UserRepository;

@Service
public class StudyPlanService {
	@Autowired
	private StudyPlanRepository repository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProgressTrackerRepository progressTrackerRepository;

	public List<StudyPlans> findAll() {
		return repository.findAll();
	}

	public StudyPlans findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Study plan not found"));
	}

	public StudyPlans create(StudyPlans item) {
		return repository.save(item);
	}

	public StudyPlans update(Long id, StudyPlans payload) {
		StudyPlans current = findById(id);
		current.setDurationDays(payload.getDurationDays());
		current.setGoalScore(payload.getGoalScore());
		current.setUser(payload.getUser());
		return repository.save(current);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Map<String, Object> getMyPlan(String username) {
		Users user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		StudyPlans plan = repository.findTopByUser_IdOrderByCreatedAtDesc(user.getId()).orElse(null);

		if (plan == null) {
			return Map.of("hasPlan", false);
		}

		List<ProgressTrackers> trackers = progressTrackerRepository.findByUser_IdAndStudyPlan_Id(user.getId(),
				plan.getId());
		return Map.of("hasPlan", true, "plan", plan, "progress", trackers);
	}

	public StudyPlans createForUser(String username, int durationDays, BigDecimal goalScore) {
		Users user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		StudyPlans plan = new StudyPlans();
		plan.setUser(user);
		plan.setDurationDays(durationDays);
		plan.setGoalScore(goalScore);
		return repository.save(plan);
	}
}

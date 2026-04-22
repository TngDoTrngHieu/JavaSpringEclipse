package com.th.learningenglish.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.pojo.StudyPlans;
import com.th.learningenglish.service.StudyPlanService;

@RestController
@RequestMapping("/api/study-plans")
public class ApiStudyPlanController {

	@Autowired
	private StudyPlanService studyPlanService;

	@GetMapping
	public List<StudyPlans> getAll() {
		return studyPlanService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudyPlans> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(studyPlanService.findById(id));
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/my")
	public ResponseEntity<?> getMy(Principal principal) {
		return ResponseEntity.ok(studyPlanService.getMyPlan(principal.getName()));
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Map<String, Object> req, Principal principal) {
		return ResponseEntity.ok(studyPlanService.createForUser(
				principal.getName(),
				Integer.parseInt(req.get("durationDays").toString()),
				new BigDecimal(req.get("goalScore").toString())));
	}
}

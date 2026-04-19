package com.th.learningenglish.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.pojo.UserVips;
import com.th.learningenglish.service.UserVipService;

@RestController
@RequestMapping("/api/user-vips")
public class ApiUserVipController {
	@Autowired
	private UserVipService userVipService;

	@GetMapping
	public List<UserVips> getAll() {
		return userVipService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserVips> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(userVipService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public UserVips create(@RequestBody UserVips item) {
		return userVipService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserVips> update(@PathVariable Long id, @RequestBody UserVips payload) {
		try { return ResponseEntity.ok(userVipService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { userVipService.findById(id); userVipService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

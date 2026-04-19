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

import com.th.learningenglish.pojo.VipPackages;
import com.th.learningenglish.service.VipPackageService;

@RestController
@RequestMapping("/api/vip-packages")
public class ApiVipPackageController {
	@Autowired
	private VipPackageService vipPackageService;

	@GetMapping
	public List<VipPackages> getAll() {
		return vipPackageService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<VipPackages> getById(@PathVariable Long id) {
		try { return ResponseEntity.ok(vipPackageService.findById(id)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@PostMapping
	public VipPackages create(@RequestBody VipPackages item) {
		return vipPackageService.create(item);
	}

	@PutMapping("/{id}")
	public ResponseEntity<VipPackages> update(@PathVariable Long id, @RequestBody VipPackages payload) {
		try { return ResponseEntity.ok(vipPackageService.update(id, payload)); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		try { vipPackageService.findById(id); vipPackageService.delete(id); return ResponseEntity.ok(Map.of("message", "Deleted")); } catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
	}
}

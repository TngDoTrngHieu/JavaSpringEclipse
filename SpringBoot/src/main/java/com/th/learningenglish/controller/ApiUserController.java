package com.th.learningenglish.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.service.UserService;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

	@Autowired
	private UserService userService;

	@GetMapping
	public java.util.List<Users> getUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public Users getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(Authentication auth) {
		return ResponseEntity.ok(userService.getProfile(auth.getName()));
	}

	@PutMapping("/{id}")
	public Users updateUserById(@PathVariable Long id, @RequestBody Map<String, String> params) {
		return userService.updateUserById(id, params);
	}

	@PutMapping(path = "/update/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProfile(Authentication auth, @RequestParam Map<String, String> params,
			@RequestParam(value = "avatar", required = false) MultipartFile avatar) {

		try {
			Users user = userService.updateProfile(auth.getName(), params, avatar);
			return ResponseEntity.ok(user);

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public Map<String, String> deleteUserById(@PathVariable Long id) {
		userService.deleteUserById(id);
		return Map.of("message", "Deleted");
	}
}

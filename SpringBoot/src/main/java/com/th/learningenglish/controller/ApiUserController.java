package com.th.learningenglish.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.repository.UserRepository;
import com.th.learningenglish.service.UserService;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@GetMapping("/profile")
	public Users getProfile(Authentication auth) {
		return userService.getProfile(auth.getName());
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
}

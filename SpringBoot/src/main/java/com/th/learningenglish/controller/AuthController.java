package com.th.learningenglish.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.th.learningenglish.dto.LoginRequest;
import com.th.learningenglish.dto.RegisterRequest;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserService userService;

	@PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Users register(@RequestParam Map<String, String> params,
			@RequestParam(value = "avatar", required = false) MultipartFile avatar) {

		// 🔥 convert Map → DTO
		RegisterRequest req = new RegisterRequest();
		req.setFirstname(params.get("firstname"));
		req.setLastname(params.get("lastname"));
		req.setEmail(params.get("email"));
		req.setPassword(params.get("password"));
		req.setusername(params.get("username"));

		return userService.register(req, avatar);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		try {
			String token = userService.login(req);

			return ResponseEntity.ok(Collections.singletonMap("token", token));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "Server error"));
		}
	}
}
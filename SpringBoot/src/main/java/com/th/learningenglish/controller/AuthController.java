package com.th.learningenglish.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.th.learningenglish.dto.LoginRequest;
import com.th.learningenglish.dto.RegisterRequest;
import com.th.learningenglish.pojo.Users;
import com.th.learningenglish.security.JwtUtils;
import com.th.learningenglish.service.EmailService;
import com.th.learningenglish.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;

	@PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Users register(@RequestParam Map<String, String> params,
			@RequestParam(value = "avatar", required = false) MultipartFile avatar) {

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

	@PostMapping("/google")
	public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> body) {
		try {
			return ResponseEntity.ok(userService.loginWithGoogleToken(body.get("token")));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "Server error"));
		}
	}

	@GetMapping("/google-client-id")
	public ResponseEntity<?> getGoogleClientId() {
		Map<String, String> res = new HashMap<>();
		res.put("client_id", userService.getGoogleClientId());
		return ResponseEntity.ok(res);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		if (email == null || email.isBlank()) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email is required"));
		}

		try {
			Users u = userService.getUserByEmail(email);
			System.out.println("EMAIL NHAN: [" + email + "]");
			String token = JwtUtils.generateResetToken(email, 5 * 60 * 1000);
			String link = "https://java-spring-eclipse.vercel.app/reset-password?token=" + token;
			emailService.sendResetLink(email, link);

			return ResponseEntity.ok(Collections.singletonMap("message", "Email đã được gửi tới gmail"));
		} catch (RuntimeException re) {

			return ResponseEntity.ok(Collections.singletonMap("message", "Gmail chưa được đăng ký vào trang web."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "Server error"));
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
		String token = body.get("token");
		String newPassword = body.get("newPassword");
		if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
			return ResponseEntity.badRequest()
					.body(Collections.singletonMap("error", "token and newPassword are required"));
		}
		if (newPassword.length() < 6) {
			return ResponseEntity.badRequest()
					.body(Collections.singletonMap("error", "newPassword must be at least 6 characters"));
		}
		try {
			String email = JwtUtils.validateResetTokenAndGetEmail(token);
			if (email == null)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Collections.singletonMap("error", "Invalid or expired token"));
			Users u = userService.getUserByEmail(email);
			userService.updateUserById(u.getId(), Collections.singletonMap("password", newPassword));
			return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successful"));
		} catch (RuntimeException re) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Collections.singletonMap("error", "User not found"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", "Server error"));
		}
	}
}
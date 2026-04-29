package com.th.learningenglish.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.service.ChatService;
import com.th.learningenglish.service.UserService;

@RestController
@RequestMapping("/api/chat")
public class ApiChatController {

	@Autowired
	private ChatService chatService;

	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<?> chat(@RequestBody Map<String, String> req, Principal principal) {

		// 1. Kiểm tra đăng nhập
		if (principal == null) {
			return ResponseEntity.status(401).body(Map.of("error", "Bạn cần đăng nhập để sử dụng AI Chat."));
		}

		// 2. Kiểm tra VIP
		if (!userService.isUserVip(principal.getName())) {
			return ResponseEntity.status(403).body(Map.of("error", "Tính năng AI Chat hiện chỉ dành cho thành viên VIP."));
		}

		String question = req.get("question");

		String answer = chatService.chat(principal.getName(), question);

		return ResponseEntity.ok(Map.of("answer", answer));
	}
}
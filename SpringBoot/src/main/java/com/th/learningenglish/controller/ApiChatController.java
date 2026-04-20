package com.th.learningenglish.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.th.learningenglish.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ApiChatController {

	@Autowired
	private ChatService chatService;

	@PostMapping
	public Map<String, String> chat(@RequestBody Map<String, String> req, Principal principal) {

		String question = req.get("question");

		String answer = chatService.chat(principal.getName(), question);

		return Map.of("answer", answer);
	}
}
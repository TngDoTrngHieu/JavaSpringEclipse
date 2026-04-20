package com.th.learningenglish.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

	@Autowired
	private GeminiService geminiService;

	@Autowired
	private VocabularyService vocabularyService;

	public String chat(String username, String message) {

		String context = "";
		try {
			context = vocabularyService.buildRagContext(username, message);
		} catch (Exception e) {
			// ignore context building errors and proceed without RAG
			context = "";
		}

		String prompt;
		if (context == null || context.isBlank()) {
			prompt = """
					You are an English learning assistant.
					Answer clearly and simply.

					User question:
					""" + message;
		} else {
			prompt = """
					You are an English learning assistant.
					Use the provided context first. If the context is relevant, prioritize it.
					If the context is insufficient, you may still answer generally.

					Context:
					""" + context + "\n" + "User question:\n" + message;
		}
		System.out.println("RAG CONTEXT:\n" + context);
		try {
			return geminiService.ask(prompt);
		} catch (Exception e) {
			return "Lỗi khi gọi AI: " + e.getMessage();
		}

	}
}
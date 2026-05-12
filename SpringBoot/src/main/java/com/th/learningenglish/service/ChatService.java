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
			context = vocabularyService.buildRagContext(message);
		} catch (Exception e) {
			context = "";
		}

		String systemInstruction = """
				You are an English learning assistant.

				IMPORTANT: If the user asks for the meaning of an English word
				(whether they type a single word, or ask a question like "từ meat có nghĩa là gì"),
				you MUST ALWAYS respond in EXACTLY this format, no exceptions:

				Word: <the word>
				Meaning: <the most common Vietnamese meaning>
				Example: <one simple example sentence>
				Note: <brief grammar tip, other meanings, or usage note>

				Do NOT use Markdown formatting like ** for bold text. Just use plain text.
				Do NOT use bullet points or numbered lists.
				Put additional meanings inside the Note field only.
				If the user asks a general question NOT about a specific word's meaning, answer naturally.
				""";

		String prompt;
		if (context == null || context.isBlank()) {
			prompt = systemInstruction + "\nUser question:\n" + message;
		} else {
			prompt = systemInstruction + "\nContext from user's vocabulary:\n" + context + "\nUser question:\n"
					+ message;
		}
		System.out.println("RAG CONTEXT:\n" + context);
		try {
			return geminiService.ask(prompt);
		} catch (Exception e) {
			return "Lỗi khi gọi AI: " + e.getMessage();
		}

	}
}
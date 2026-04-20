package com.th.learningenglish.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GeminiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final OkHttpClient client = new OkHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	public String ask(String prompt) throws Exception {

		String json = """
				{
				  "contents": [
				    {
				      "parts": [
				        { "text": "%s" }
				      ]
				    }
				  ]
				}
				""".formatted(prompt.replace("\"", "\\\""));

		Request request = new Request.Builder()
				.url("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent")
				.addHeader("X-goog-api-key", apiKey).addHeader("Content-Type", "application/json")
				.post(RequestBody.create(json, MediaType.parse("application/json"))).build();

		Response response = client.newCall(request).execute();
		String body = response.body().string();

		JsonNode root = mapper.readTree(body);

		JsonNode candidates = root.path("candidates");

		if (!candidates.isArray() || candidates.size() == 0) {
			return "Gemini không trả dữ liệu: " + root.toString();
		}

		JsonNode textNode = candidates.get(0).path("content").path("parts");

		if (!textNode.isArray() || textNode.size() == 0) {
			return "Gemini trả dữ liệu không hợp lệ: " + root.toString();
		}
		System.out.println("GEMINI RESPONSE: " + body);

		return textNode.get(0).path("text").asText();

	}
}
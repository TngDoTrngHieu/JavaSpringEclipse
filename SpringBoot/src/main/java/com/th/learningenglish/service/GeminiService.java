package com.th.learningenglish.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GeminiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private final OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
			.readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
			.writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS).build();
	private final ObjectMapper mapper = new ObjectMapper();

	private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";

	private static final String GEMINI_LITE_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent";

	// 1. ask vocab
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

		Request request = new Request.Builder().url(GEMINI_LITE_URL).addHeader("X-goog-api-key", apiKey)
				.addHeader("Content-Type", "application/json")
				.post(RequestBody.create(json, MediaType.parse("application/json"))).build();

		return executeAndParseResponse(request);
	}

	// 2. CHẤM ĐIỂM WRITING TASK 2

	public String evaluateTask2Essay(String essayContent) throws Exception {
		String prompt = "You are an IELTS examiner.\n"
				+ "Please evaluate the following essay using IELTS Writing Task 2 criteria:\n" + "- Task Response\n"
				+ "- Coherence & Cohesion\n" + "- Lexical Resource\n" + "- Grammar Range & Accuracy\n\n"
				+ "Return a valid JSON with fields:\n"
				+ "content, feedback, task_score, coherence_score, lexical_score, grammar_score, overall_score.\n"
				+ "Band scores should be float numbers from 0.0 to 9.0.\n\n"
				+ "**The 'feedback' field must be a single string (not an object), containing all the evaluation content, clearly separated by headings and newlines.**\n"
				+ "**All feedback content should be written in Vietnamese. However, keep section headings (e.g., 'Task Response', 'Lexical Resource', etc.) and any example vocabulary suggestions in English.**\n"
				+ "Do NOT return markdown or code formatting inside the 'feedback' string.";

		ObjectNode root = mapper.createObjectNode();
		ArrayNode contents = mapper.createArrayNode();
		root.set("contents", contents);

		contents.add(createTurn("user", arr(obj("text", prompt))));
		contents.add(createTurn("user", arr(obj("text", "Essay:\n" + essayContent))));

		return sendRequestToGemini(root, GEMINI_URL);
	}

	// 3. CHẤM ĐIỂM WRITING TASK 1 (CÓ HÌNH ẢNH)

	public String evaluateTask1Essay(String essayContent, String imageUrl) throws Exception {
		String prompt = "You are a strict IELTS examiner.\n" + "DO NOT ignore the image." + "Read context clearly "
				+ "Please evaluate the following Writing Task 1 report using IELTS criteria:\n" + "- Task Achievement\n"
				+ "- Coherence & Cohesion\n" + "- Lexical Resource\n" + "- Grammatical Range & Accuracy\n\n"
				+ "You will be provided with the prompt image and the student's response.\n\n"
				+ "Return a valid JSON with fields:\n"
				+ "content, feedback, task_score, coherence_score, lexical_score, grammar_score, overall_score.\n"
				+ "Band scores should be float numbers from 0.0 to 9.0.\n\n"
				+ "**The 'feedback' field must be a single string (not an object), containing all the evaluation content, clearly separated by headings and newlines.**\n"
				+ "**All feedback content should be written in Vietnamese. However, keep section headings (e.g., 'Task Achievement', 'Coherence & Cohesion', etc.) and any example vocabulary suggestions in English.**\n"
				+ "Do NOT return markdown or code formatting inside the 'feedback' string.";

		// Download và mã hóa hình ảnh sang Base64
		HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		String mimeType = conn.getContentType() != null ? conn.getContentType() : "image/jpeg";
		InputStream in = conn.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		in.transferTo(bos);
		String base64Image = Base64.getEncoder().encodeToString(bos.toByteArray());

		ObjectNode root = mapper.createObjectNode();
		ArrayNode contents = mapper.createArrayNode();
		root.set("contents", contents);

		// Text Prompt + Image
		ArrayNode parts1 = mapper.createArrayNode();
		parts1.add(obj("text", prompt));

		ObjectNode inlineData = mapper.createObjectNode();
		inlineData.put("mime_type", mimeType);
		inlineData.put("data", base64Image);
		ObjectNode imagePart = mapper.createObjectNode();
		imagePart.set("inline_data", inlineData);
		parts1.add(imagePart);

		contents.add(createTurn("user", parts1));

		// Turn 2: Bài viết của sinh viên
		contents.add(createTurn("user", arr(obj("text", "Essay:\n" + essayContent))));

		return sendRequestToGemini(root, GEMINI_URL);
	}

	// 4. CHẤM ĐIỂM SPEAKING

	public String evaluateSpeakingTranscript(String title, List<String> questions, String transcript) throws Exception {
		String prompt = """
				You are an IELTS Speaking examiner.
				Evaluate the candidate's spoken response based on these criteria:
				- Pronunciation
				- Fluency & Coherence
				- Lexical Resource
				- Grammatical Range & Accuracy

				Return a valid JSON with fields:
				transcript, pronunciation_score, fluency_score, coherence_score,
				lexical_resource_score, grammar_score, overall_score, feedback.
				Band scores should be float numbers from 0.0 to 9.0.

				The 'feedback' field must be a single string in Vietnamese,
				with section headings in English.
				Do NOT include markdown or code formatting.
				""";
		StringBuilder questionPrompt = new StringBuilder();
		questionPrompt.append("Prompt Title: ").append(title).append("\nQuestions:\n");
		for (int i = 0; i < questions.size(); i++) {
			questionPrompt.append((i + 1)).append(". ").append(questions.get(i)).append("\n");
		}

		ObjectNode root = mapper.createObjectNode();
		ArrayNode contents = mapper.createArrayNode();
		root.set("contents", contents);

		contents.add(createTurn("user", arr(obj("text", prompt))));
		contents.add(createTurn("user", arr(obj("text", questionPrompt.toString()))));
		contents.add(createTurn("user", arr(obj("text", "Transcript:\n" + transcript))));

		return sendRequestToGemini(root, GEMINI_URL);
	}

	// 5. TẠO CÂU HỎI TRẮC NGHIỆM TỪ ĐOẠN VĂN
	public String generateQuizFromPassage(String passage) throws Exception {
		String prompt = """
				You are an IELTS reading question generator.

				Based ONLY on the following passage, generate 5 multiple-choice questions.

				PASSAGE:
				%s

				REQUIREMENTS:
				- All questions must be in English
				- Each question must have exactly 4 options
				- Only ONE correct answer
				- The correctAnswer must EXACTLY match one option
				- Do NOT create information outside the passage
				- Avoid duplicate or similar questions

				OUTPUT FORMAT (STRICT JSON ONLY):
				[
				  {
				    "question": "string",
				    "options": ["A", "B", "C", "D"],
				    "correctAnswer": "one of options"
				  }
				]

				Return ONLY JSON. No explanation.
				""".formatted(passage);

		ObjectNode root = mapper.createObjectNode();
		ArrayNode contents = mapper.createArrayNode();
		root.set("contents", contents);

		contents.add(createTurn("user", arr(obj("text", prompt))));

		return sendRequestToGemini(root, GEMINI_URL);
	}

	// Gửi request từ Json Node xây dựng sẵn
	private String sendRequestToGemini(ObjectNode rootNode, String targetUrl) throws Exception {
		Request request = new Request.Builder().url(targetUrl).addHeader("X-goog-api-key", apiKey)
				.addHeader("Content-Type", "application/json")
				.post(RequestBody.create(mapper.writeValueAsString(rootNode), MediaType.parse("application/json")))
				.build();
		return executeAndParseResponse(request);
	}

	// Thực thi HTTP Request và bóc tách lấy text
	private String executeAndParseResponse(Request request) throws Exception {
		System.out.println("🚀 START CALL GEMINI");
		try (Response response = client.newCall(request).execute()) {
			System.out.println("✅ END CALL GEMINI");

			if (!response.isSuccessful()) {
				throw new Exception("Gemini API error: " + response.code() + " - "
						+ (response.body() != null ? response.body().string() : ""));
			}

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

			String result = textNode.get(0).path("text").asText();

			result = result.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();

			return result;
		}
	}

	private ObjectNode createTurn(String role, ArrayNode parts) {
		ObjectNode turn = mapper.createObjectNode();
		turn.put("role", role);
		turn.set("parts", parts);
		return turn;
	}

	private ObjectNode obj(String key, String value) {
		ObjectNode o = mapper.createObjectNode();
		o.put(key, value);
		return o;
	}

	private ArrayNode arr(JsonNode... nodes) {
		ArrayNode a = mapper.createArrayNode();
		for (JsonNode n : nodes) {
			a.add(n);
		}
		return a;
	}
}
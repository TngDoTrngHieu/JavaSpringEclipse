package com.th.learningenglish.dto;

import java.util.Map;

public class ListeningSubmitDTO {
	private Long lessonId;
	/**
	 * Keys must be section id as string (JSON object keys are always strings), e.g. "17" -> "B".
	 */
	private Map<String, String> answers;

	public Long getLessonId() {
		return lessonId;
	}

	public void setLessonId(Long lessonId) {
		this.lessonId = lessonId;
	}

	public Map<String, String> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<String, String> answers) {
		this.answers = answers;
	}
}

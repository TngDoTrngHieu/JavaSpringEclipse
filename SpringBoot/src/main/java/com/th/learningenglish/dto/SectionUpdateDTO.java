package com.th.learningenglish.dto;

import java.util.List;

public class SectionUpdateDTO {

	private Long id;
	private Integer position;
	private Long sectionTypeId;
	private String content;
	private String question;
	private List<String> options;
	private String correctAnswer;
	/** JSON string — gap-fill / structured answer nếu cần */
	private String answer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Long getSectionTypeId() {
		return sectionTypeId;
	}

	public void setSectionTypeId(Long sectionTypeId) {
		this.sectionTypeId = sectionTypeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}

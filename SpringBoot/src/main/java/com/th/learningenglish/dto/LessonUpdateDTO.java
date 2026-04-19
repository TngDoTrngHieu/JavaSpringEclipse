package com.th.learningenglish.dto;

import java.util.List;

/**
 * Full replace: lesson fields + toàn bộ sections (sync create / update / delete).
 */
public class LessonUpdateDTO {

	private String title;
	private String imageUrl;
	private String content;
	private Long categoryId;
	private Long lessonTypeId;
	private List<SectionUpdateDTO> sections;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getLessonTypeId() {
		return lessonTypeId;
	}

	public void setLessonTypeId(Long lessonTypeId) {
		this.lessonTypeId = lessonTypeId;
	}

	public List<SectionUpdateDTO> getSections() {
		return sections;
	}

	public void setSections(List<SectionUpdateDTO> sections) {
		this.sections = sections;
	}
}

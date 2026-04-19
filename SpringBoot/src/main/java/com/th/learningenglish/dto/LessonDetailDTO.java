package com.th.learningenglish.dto;

import java.util.List;

/**
 * GET /api/lessons/{id} — lesson + sections đã map (skill, type section, options[]).
 */
public class LessonDetailDTO {

	private Long id;
	private String title;
	private String skill;
	private String imageUrl;
	private String content;
	private Long categoryId;
	private String categoryName;
	private Long lessonTypeId;
	private String lessonTypeName;
	private List<SectionDTO> sections;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
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

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Long getLessonTypeId() {
		return lessonTypeId;
	}

	public void setLessonTypeId(Long lessonTypeId) {
		this.lessonTypeId = lessonTypeId;
	}

	public String getLessonTypeName() {
		return lessonTypeName;
	}

	public void setLessonTypeName(String lessonTypeName) {
		this.lessonTypeName = lessonTypeName;
	}

	public List<SectionDTO> getSections() {
		return sections;
	}

	public void setSections(List<SectionDTO> sections) {
		this.sections = sections;
	}
}

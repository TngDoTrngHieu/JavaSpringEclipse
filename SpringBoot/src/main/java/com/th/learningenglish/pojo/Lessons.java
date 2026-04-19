package com.th.learningenglish.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lessons")
public class Lessons implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "title", nullable = false, length = 255)
	private String title;

	@Size(max = 1024)
	@Column(name = "image_url", length = 1024)
	private String imageUrl;

	@Lob
	@Column(name = "content", columnDefinition = "LONGTEXT")
	private String content;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Categories category;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "lesson_type_id", nullable = false)
	private LessonTypes lessonType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "lesson")
	@JsonIgnore
	private Set<Sections> sectionsSet;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "lesson")
	@JsonIgnore
	private Set<PracticeSessions> practiceSessionsSet;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "lesson")
	@JsonIgnore
	private Set<UserWritingAnswers> userWritingAnswersSet;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "lesson")
	@JsonIgnore
	private Set<UserSpeakingAnswers> userSpeakingAnswersSet;

	@PrePersist
	protected void onCreate() {
		createdAt = updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Lessons() {
	}

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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Categories getCategory() {
		return category;
	}

	public void setCategory(Categories category) {
		this.category = category;
	}

	public LessonTypes getLessonType() {
		return lessonType;
	}

	public void setLessonType(LessonTypes lessonType) {
		this.lessonType = lessonType;
	}

	public Set<Sections> getSectionsSet() {
		return sectionsSet;
	}

	public void setSectionsSet(Set<Sections> sectionsSet) {
		this.sectionsSet = sectionsSet;
	}

	public Set<PracticeSessions> getPracticeSessionsSet() {
		return practiceSessionsSet;
	}

	public void setPracticeSessionsSet(Set<PracticeSessions> practiceSessionsSet) {
		this.practiceSessionsSet = practiceSessionsSet;
	}

	public Set<UserWritingAnswers> getUserWritingAnswersSet() {
		return userWritingAnswersSet;
	}

	public void setUserWritingAnswersSet(Set<UserWritingAnswers> userWritingAnswersSet) {
		this.userWritingAnswersSet = userWritingAnswersSet;
	}

	public Set<UserSpeakingAnswers> getUserSpeakingAnswersSet() {
		return userSpeakingAnswersSet;
	}

	public void setUserSpeakingAnswersSet(Set<UserSpeakingAnswers> userSpeakingAnswersSet) {
		this.userSpeakingAnswersSet = userSpeakingAnswersSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Lessons))
			return false;
		Lessons other = (Lessons) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.Lessons[ id=" + id + " ]";
	}
}

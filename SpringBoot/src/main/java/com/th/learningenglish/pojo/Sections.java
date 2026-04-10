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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "sections", uniqueConstraints = @UniqueConstraint(columnNames = { "lesson_id", "position" }))
public class Sections implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Column(name = "position", nullable = false)
	private int position;

	@Column(name = "content", columnDefinition = "JSON")
	private String content;

	@Column(name = "question", columnDefinition = "JSON")
	private String question;

	@Column(name = "options", columnDefinition = "JSON")
	private String options;

	@Column(name = "answer", columnDefinition = "JSON")
	private String answer;

	@Column(name = "correct_answer", columnDefinition = "JSON")
	private String correctAnswer;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "lesson_id", nullable = false)
	private Lessons lesson;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "section_type_id", nullable = false)
	private SectionTypes sectionType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "section")
	@JsonIgnore
	private Set<UserAnswers> userAnswersSet;

	@PrePersist
	protected void onCreate() {
		createdAt = updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Sections() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
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

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
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

	public Lessons getLesson() {
		return lesson;
	}

	public void setLesson(Lessons lesson) {
		this.lesson = lesson;
	}

	public SectionTypes getSectionType() {
		return sectionType;
	}

	public void setSectionType(SectionTypes sectionType) {
		this.sectionType = sectionType;
	}

	public Set<UserAnswers> getUserAnswersSet() {
		return userAnswersSet;
	}

	public void setUserAnswersSet(Set<UserAnswers> userAnswersSet) {
		this.userAnswersSet = userAnswersSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Sections))
			return false;
		Sections other = (Sections) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.Sections[ id=" + id + " ]";
	}
}

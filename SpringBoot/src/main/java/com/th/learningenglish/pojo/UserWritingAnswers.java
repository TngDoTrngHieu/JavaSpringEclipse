package com.th.learningenglish.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_writing_answers")
public class UserWritingAnswers implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Lob
	@Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
	private String content;

	@Lob
	@Column(name = "feedback", columnDefinition = "LONGTEXT")
	private String feedback;

	@Column(name = "task_score", precision = 5, scale = 2)
	private BigDecimal taskScore;

	@Column(name = "coherence_score", precision = 5, scale = 2)
	private BigDecimal coherenceScore;

	@Column(name = "lexical_score", precision = 5, scale = 2)
	private BigDecimal lexicalScore;

	@Column(name = "grammar_score", precision = 5, scale = 2)
	private BigDecimal grammarScore;

	@Column(name = "overall_score", precision = 5, scale = 2)
	private BigDecimal overallScore;

	@NotNull
	@Column(name = "duration_seconds", nullable = false)
	private int durationSeconds = 0;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "lesson_id", nullable = false)
	private Lessons lesson;

	@PrePersist
	protected void onCreate() {
		createdAt = updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public UserWritingAnswers() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public BigDecimal getTaskScore() {
		return taskScore;
	}

	public void setTaskScore(BigDecimal taskScore) {
		this.taskScore = taskScore;
	}

	public BigDecimal getCoherenceScore() {
		return coherenceScore;
	}

	public void setCoherenceScore(BigDecimal coherenceScore) {
		this.coherenceScore = coherenceScore;
	}

	public BigDecimal getLexicalScore() {
		return lexicalScore;
	}

	public void setLexicalScore(BigDecimal lexicalScore) {
		this.lexicalScore = lexicalScore;
	}

	public BigDecimal getGrammarScore() {
		return grammarScore;
	}

	public void setGrammarScore(BigDecimal grammarScore) {
		this.grammarScore = grammarScore;
	}

	public BigDecimal getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(BigDecimal overallScore) {
		this.overallScore = overallScore;
	}

	public int getDurationSeconds() {
		return durationSeconds;
	}

	public void setDurationSeconds(int durationSeconds) {
		this.durationSeconds = durationSeconds;
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

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Lessons getLesson() {
		return lesson;
	}

	public void setLesson(Lessons lesson) {
		this.lesson = lesson;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof UserWritingAnswers))
			return false;
		UserWritingAnswers other = (UserWritingAnswers) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.UserWritingAnswers[ id=" + id + " ]";
	}
}

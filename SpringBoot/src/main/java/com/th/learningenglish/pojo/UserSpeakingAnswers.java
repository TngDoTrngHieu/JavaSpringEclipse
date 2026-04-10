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
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "user_speaking_answers")
public class UserSpeakingAnswers implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 1, max = 2048)
	@Column(name = "audio_url", nullable = false, length = 2048)
	private String audioUrl;

	@Lob
	@Column(name = "transcript", columnDefinition = "LONGTEXT")
	private String transcript;

	@Lob
	@Column(name = "feedback", columnDefinition = "LONGTEXT")
	private String feedback;

	@Column(name = "pronunciation_score", precision = 5, scale = 2)
	private BigDecimal pronunciationScore;

	@Column(name = "fluency_score", precision = 5, scale = 2)
	private BigDecimal fluencyScore;

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

	public UserSpeakingAnswers() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public BigDecimal getPronunciationScore() {
		return pronunciationScore;
	}

	public void setPronunciationScore(BigDecimal pronunciationScore) {
		this.pronunciationScore = pronunciationScore;
	}

	public BigDecimal getFluencyScore() {
		return fluencyScore;
	}

	public void setFluencyScore(BigDecimal fluencyScore) {
		this.fluencyScore = fluencyScore;
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
		if (!(object instanceof UserSpeakingAnswers))
			return false;
		UserSpeakingAnswers other = (UserSpeakingAnswers) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.UserSpeakingAnswers[ id=" + id + " ]";
	}
}

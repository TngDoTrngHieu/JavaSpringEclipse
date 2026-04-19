package com.th.learningenglish.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class Users implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Size(max = 100)
	@Column(name = "firstname", length = 100)
	private String firstname;

	@Size(max = 100)
	@Column(name = "lastname", length = 100)
	private String lastname;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "email", nullable = false, unique = true, length = 255)
	private String email;

	@NotNull
	@Size(min = 1, max = 255)
	@JsonIgnore
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role = Role.USER;

	@Size(max = 1024)
	@Column(name = "avatar_url", length = 1024)
	private String avatarUrl;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(unique = true, nullable = false)
	private String username;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<ProgressTrackers> progressTrackersSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<Vocabularies> vocabulariesSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<Payments> paymentsSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<UserSpeakingAnswers> userSpeakingAnswersSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<UserVips> userVipsSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<PracticeSessions> practiceSessionsSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<UserWritingAnswers> userWritingAnswersSet;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<StudyPlans> studyPlansSet;

	public enum Role {
		USER, ADMIN
	}

	@PrePersist
	protected void onCreate() {
		createdAt = updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Users() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
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

	public Set<ProgressTrackers> getProgressTrackersSet() {
		return progressTrackersSet;
	}

	public void setProgressTrackersSet(Set<ProgressTrackers> progressTrackersSet) {
		this.progressTrackersSet = progressTrackersSet;
	}

	public Set<Vocabularies> getVocabulariesSet() {
		return vocabulariesSet;
	}

	public void setVocabulariesSet(Set<Vocabularies> vocabulariesSet) {
		this.vocabulariesSet = vocabulariesSet;
	}

	public Set<Payments> getPaymentsSet() {
		return paymentsSet;
	}

	public void setPaymentsSet(Set<Payments> paymentsSet) {
		this.paymentsSet = paymentsSet;
	}

	public Set<UserSpeakingAnswers> getUserSpeakingAnswersSet() {
		return userSpeakingAnswersSet;
	}

	public void setUserSpeakingAnswersSet(Set<UserSpeakingAnswers> userSpeakingAnswersSet) {
		this.userSpeakingAnswersSet = userSpeakingAnswersSet;
	}

	public Set<UserVips> getUserVipsSet() {
		return userVipsSet;
	}

	public void setUserVipsSet(Set<UserVips> userVipsSet) {
		this.userVipsSet = userVipsSet;
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

	public Set<StudyPlans> getStudyPlansSet() {
		return studyPlansSet;
	}

	public void setStudyPlansSet(Set<StudyPlans> studyPlansSet) {
		this.studyPlansSet = studyPlansSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Users))
			return false;
		Users other = (Users) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.Users[ id=" + id + " ]";
	}
}

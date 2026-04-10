package com.th.learningenglish.pojo;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lesson_types", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "skill" }))
public class LessonTypes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "skill", nullable = false)
	private Skill skill;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "lessonType")
	private Set<Lessons> lessonsSet;

	public enum Skill {
		READING, LISTENING, WRITING, SPEAKING
	}

	public LessonTypes() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public Set<Lessons> getLessonsSet() {
		return lessonsSet;
	}

	public void setLessonsSet(Set<Lessons> lessonsSet) {
		this.lessonsSet = lessonsSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LessonTypes))
			return false;
		LessonTypes other = (LessonTypes) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.LessonTypes[ id=" + id + " ]";
	}
}

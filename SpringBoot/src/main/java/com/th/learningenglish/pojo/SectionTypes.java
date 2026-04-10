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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "section_types")
public class SectionTypes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "name", nullable = false, unique = true, length = 100)
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "save_type", nullable = false)
	private SaveType saveType = SaveType.AUTO;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "sectionType")
	private Set<Sections> sectionsSet;

	public enum SaveType {
		AUTO, MANUAL
	}

	public SectionTypes() {
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

	public SaveType getSaveType() {
		return saveType;
	}

	public void setSaveType(SaveType saveType) {
		this.saveType = saveType;
	}

	public Set<Sections> getSectionsSet() {
		return sectionsSet;
	}

	public void setSectionsSet(Set<Sections> sectionsSet) {
		this.sectionsSet = sectionsSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SectionTypes))
			return false;
		SectionTypes other = (SectionTypes) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.SectionTypes[ id=" + id + " ]";
	}
}

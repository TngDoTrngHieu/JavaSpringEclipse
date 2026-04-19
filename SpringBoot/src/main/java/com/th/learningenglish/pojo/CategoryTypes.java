package com.th.learningenglish.pojo;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "category_types")
public class CategoryTypes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "name", nullable = false, unique = true, length = 100)
	private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryType")
	@JsonIgnore
	private Set<Categories> categoriesSet;

	public CategoryTypes() {
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

	public Set<Categories> getCategoriesSet() {
		return categoriesSet;
	}

	public void setCategoriesSet(Set<Categories> categoriesSet) {
		this.categoriesSet = categoriesSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CategoryTypes))
			return false;
		CategoryTypes other = (CategoryTypes) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.CategoryTypes[ id=" + id + " ]";
	}
}

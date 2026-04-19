package com.th.learningenglish.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vip_packages")
public class VipPackages implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Size(min = 1, max = 150)
	@Column(name = "name", nullable = false, unique = true, length = 150)
	private String name;

	@Lob
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@NotNull
	@Column(name = "months", nullable = false)
	private int months;

	@NotNull
	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@NotNull
	@Column(name = "is_active", nullable = false)
	private boolean isActive = true;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vipPackage")
	@JsonIgnore
	private Set<UserVips> userVipsSet;

	public VipPackages() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Set<UserVips> getUserVipsSet() {
		return userVipsSet;
	}

	public void setUserVipsSet(Set<UserVips> userVipsSet) {
		this.userVipsSet = userVipsSet;
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof VipPackages))
			return false;
		VipPackages other = (VipPackages) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.th.pojo.VipPackages[ id=" + id + " ]";
	}
}

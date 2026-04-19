package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.VipPackages;
import com.th.learningenglish.repository.VipPackageRepository;

@Service
public class VipPackageService {
	@Autowired
	private VipPackageRepository repository;

	public List<VipPackages> findAll() { return repository.findAll(); }
	public VipPackages findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Vip package not found")); }
	public VipPackages create(VipPackages item) { return repository.save(item); }
	public VipPackages update(Long id, VipPackages payload) {
		VipPackages c = findById(id);
		c.setName(payload.getName()); c.setDescription(payload.getDescription()); c.setMonths(payload.getMonths()); c.setPrice(payload.getPrice()); c.setIsActive(payload.getIsActive());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

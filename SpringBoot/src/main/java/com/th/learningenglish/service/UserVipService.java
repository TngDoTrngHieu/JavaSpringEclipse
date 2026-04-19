package com.th.learningenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.th.learningenglish.pojo.UserVips;
import com.th.learningenglish.repository.UserVipRepository;

@Service
public class UserVipService {
	@Autowired
	private UserVipRepository repository;

	public List<UserVips> findAll() { return repository.findAll(); }
	public UserVips findById(Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("User vip not found")); }
	public UserVips create(UserVips item) { return repository.save(item); }
	public UserVips update(Long id, UserVips payload) {
		UserVips c = findById(id);
		c.setStartAt(payload.getStartAt()); c.setExpireAt(payload.getExpireAt()); c.setPayment(payload.getPayment()); c.setUser(payload.getUser()); c.setVipPackage(payload.getVipPackage());
		return repository.save(c);
	}
	public void delete(Long id) { repository.deleteById(id); }
}

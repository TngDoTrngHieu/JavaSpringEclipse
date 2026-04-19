package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.UserVips;

public interface UserVipRepository extends JpaRepository<UserVips, Long> {
}

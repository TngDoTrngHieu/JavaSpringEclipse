package com.th.learningenglish.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.UserVips;

public interface UserVipRepository extends JpaRepository<UserVips, Long> {
	boolean existsByPaymentId(Long paymentId);

	Optional<UserVips> findTopByUserIdOrderByExpireAtDesc(Long userId);
}

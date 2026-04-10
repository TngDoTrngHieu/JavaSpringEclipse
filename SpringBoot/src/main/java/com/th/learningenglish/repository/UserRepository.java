package com.th.learningenglish.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String email);

	Optional<Users> findByUsername(String username);

}

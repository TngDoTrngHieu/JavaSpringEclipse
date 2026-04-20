package com.th.learningenglish.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Payments;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
	Optional<Payments> findByTransactionCode(String transactionCode);
}

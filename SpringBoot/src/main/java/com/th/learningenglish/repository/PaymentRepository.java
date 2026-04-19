package com.th.learningenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.th.learningenglish.pojo.Payments;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
}

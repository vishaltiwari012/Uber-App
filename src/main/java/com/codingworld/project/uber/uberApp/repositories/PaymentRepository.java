package com.codingworld.project.uber.uberApp.repositories;

import com.codingworld.project.uber.uberApp.entities.Payment;
import com.codingworld.project.uber.uberApp.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRide(Ride ride);
}

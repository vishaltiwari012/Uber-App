package com.codingworld.project.uber.uberApp.repositories;

import com.codingworld.project.uber.uberApp.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
}

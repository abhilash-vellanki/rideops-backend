package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactions, Long> {
}

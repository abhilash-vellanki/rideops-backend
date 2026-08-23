package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactions,Long> {
}
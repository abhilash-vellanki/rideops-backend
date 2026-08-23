package com.abhilash.rideops.repositories;
import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Optional<Wallet> findByUser(User user);
}
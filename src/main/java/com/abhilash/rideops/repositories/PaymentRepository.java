package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByRide(Ride ride);
}
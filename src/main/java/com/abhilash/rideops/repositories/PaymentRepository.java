package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.Ride;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByRide(Ride ride);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select payment from Payment payment where payment.ride = :ride")
    Optional<Payment> findByRideForUpdate(@Param("ride") Ride ride);
}

package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.PlatformCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface PlatformCommissionRepository extends JpaRepository<PlatformCommission, Long> {

    boolean existsByPaymentId(Long paymentId);

    @Query("select sum(commission.amount) from PlatformCommission commission")
    Optional<BigDecimal> sumAmount();
}

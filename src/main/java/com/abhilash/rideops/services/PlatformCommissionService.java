package com.abhilash.rideops.services;

import com.abhilash.rideops.dto.PlatformCommissionSummaryDTO;
import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.PlatformCommission;

import java.math.BigDecimal;

public interface PlatformCommissionService {

    PlatformCommission recordCommission(Payment payment, BigDecimal amount);

    PlatformCommissionSummaryDTO getSummary();
}

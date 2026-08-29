package com.abhilash.rideops.strategies;

import com.abhilash.rideops.entities.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface PaymentStrategy {
    BigDecimal PLATFORM_COMMISSION_RATE = new BigDecimal("0.30");

    void processPayment(Payment payment);

    default BigDecimal calculatePlatformCommission(Double paymentAmount) {
        if (paymentAmount == null || paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero to calculate commission");
        }
        return BigDecimal.valueOf(paymentAmount)
                .multiply(PLATFORM_COMMISSION_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}

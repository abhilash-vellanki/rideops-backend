package com.abhilash.rideops.strategies.impl;

import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.strategies.PaymentStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStrategyCommissionTest {

    private final PaymentStrategy paymentStrategy = new PaymentStrategy() {
        @Override
        public void processPayment(Payment payment) {
        }
    };

    @Test
    void calculatesThirtyPercentCommissionRoundedToCents() {
        assertEquals(new BigDecimal("30.00"),
                paymentStrategy.calculatePlatformCommission(new BigDecimal("100.00")));
        assertEquals(new BigDecimal("3.02"),
                paymentStrategy.calculatePlatformCommission(new BigDecimal("10.05")));
    }

    @Test
    void rejectsMissingOrNonPositivePaymentAmounts() {
        assertThrows(IllegalArgumentException.class,
                () -> paymentStrategy.calculatePlatformCommission(null));
        assertThrows(IllegalArgumentException.class,
                () -> paymentStrategy.calculatePlatformCommission(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> paymentStrategy.calculatePlatformCommission(new BigDecimal("-1.00")));
    }
}

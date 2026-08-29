package com.abhilash.rideops.strategies;


import com.abhilash.rideops.entities.RideRequest;

import java.math.BigDecimal;

public interface RideFareCalculationStrategy {
    BigDecimal RIDE_FARE_MULTIPLIER = new BigDecimal("10.00");
    BigDecimal calculateFare(RideRequest rideRequest);
}

package com.abhilash.rideops.strategies.impl;

import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.services.impl.DistanceServiceOSRMImpl;
import com.abhilash.rideops.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {
    private final DistanceServiceOSRMImpl distanceServiceOSRM;
    private static final BigDecimal SURGE_FACTOR = new BigDecimal("1.20");
    @Override
    public BigDecimal calculateFare(RideRequest rideRequest) {

        double distance=distanceServiceOSRM.calculateDistance(rideRequest.getPickupLocation(),rideRequest.getDropOffLocation());
        log.debug("Surge fare calculated: rideRequestId={}, distanceKm={}, multiplier={}, surgeFactor={}",
                rideRequest.getId(), distance, RIDE_FARE_MULTIPLIER, SURGE_FACTOR);
        return BigDecimal.valueOf(distance)
                .multiply(RIDE_FARE_MULTIPLIER)
                .multiply(SURGE_FACTOR)
                .setScale(2, RoundingMode.HALF_UP);
    }
}

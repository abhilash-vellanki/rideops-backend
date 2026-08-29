package com.abhilash.rideops.strategies.impl;

import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.services.impl.DistanceServiceOSRMImpl;
import com.abhilash.rideops.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {
    private final DistanceServiceOSRMImpl distanceServiceOSRM;
    private static final double SURGE_FACTOR=1.2;
    @Override
    public double calculateFare(RideRequest rideRequest) {

        double distance=distanceServiceOSRM.calculateDistance(rideRequest.getPickupLocation(),rideRequest.getDropOffLocation());
        log.debug("Surge fare calculated: rideRequestId={}, distanceKm={}, multiplier={}, surgeFactor={}",
                rideRequest.getId(), distance, RIDE_FARE_MULTIPLIER, SURGE_FACTOR);
        return distance*RIDE_FARE_MULTIPLIER*SURGE_FACTOR;
    }
}

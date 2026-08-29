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
public class RideFareDefaultFareCalculationStrategy  implements RideFareCalculationStrategy {

    private final DistanceServiceOSRMImpl distanceServiceOSRM;
    @Override
    public BigDecimal calculateFare(RideRequest rideRequest) {
        double distance=distanceServiceOSRM.calculateDistance(rideRequest.getPickupLocation(),rideRequest.getDropOffLocation());
        log.debug("Default fare calculated: rideRequestId={}, distanceKm={}, multiplier={}",
                rideRequest.getId(), distance, RIDE_FARE_MULTIPLIER);
        return BigDecimal.valueOf(distance)
                .multiply(RIDE_FARE_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }
}

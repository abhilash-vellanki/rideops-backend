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
public class RideFareDefaultFareCalculationStrategy  implements RideFareCalculationStrategy {

    private final DistanceServiceOSRMImpl distanceServiceOSRM;
    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance=distanceServiceOSRM.calculateDistance(rideRequest.getPickupLocation(),rideRequest.getDropOffLocation());
        log.debug("Default fare calculated: rideRequestId={}, distanceKm={}, multiplier={}",
                rideRequest.getId(), distance, RIDE_FARE_MULTIPLIER);
        return distance*RIDE_FARE_MULTIPLIER;
    }
}

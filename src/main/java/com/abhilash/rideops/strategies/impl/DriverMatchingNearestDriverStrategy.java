package com.abhilash.rideops.strategies.impl;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.repositories.DriverRepository;
import com.abhilash.rideops.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;
    @Override
    public List<Driver> findMatchingDrivers(RideRequest rideRequest) {
        List<Driver> drivers = driverRepository.findTenNearestDrivers(rideRequest.getPickupLocation());
        log.debug("Nearest-driver matching completed: rideRequestId={}, matches={}",
                rideRequest.getId(), drivers.size());
        return drivers;
    }
}

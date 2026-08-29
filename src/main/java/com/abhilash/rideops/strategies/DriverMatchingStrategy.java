package com.abhilash.rideops.strategies;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.RideRequest;
import java.util.List;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDrivers(RideRequest rideRequest);

}

package com.abhilash.rideops.services;

import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.entities.Ride;

public interface RatingService {
    DriverDTO rateDriver(Ride ride, Integer rating);
    RiderDTO rateRider(Ride ride, Integer rating);
    void createNewRating(Ride ride);
}
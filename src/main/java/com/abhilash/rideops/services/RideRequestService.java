package com.abhilash.rideops.services;

import com.abhilash.rideops.entities.RideRequest;

public interface RideRequestService {
    RideRequest findRideRequestById(Long rideRequestId);
    void update(RideRequest rideRequest);

}

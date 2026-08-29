package com.abhilash.rideops.services;

import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.RideRequestDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.dto.RiderRideDTO;
import com.abhilash.rideops.entities.Rider;
import com.abhilash.rideops.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RiderService {

    RideRequestDTO requestRide(RideRequestDTO rideRequestDTO); //driver id passed from Spring security context holder
    RiderRideDTO cancelRide(Long rideId);
    DriverDTO rateDriver(Long rideId, Integer rating);
    RiderDTO getMyProfile();
    Page<RiderRideDTO> getAllMyRides(PageRequest pageRequest);
    Rider createNewRider(User user);
    Rider getCurrrentRider();
}

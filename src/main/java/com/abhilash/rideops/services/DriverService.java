package com.abhilash.rideops.services;


import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.DriverRideDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DriverService {
    DriverRideDTO cancelRide(Long rideId); //driver id passed from Spring security context holder
    DriverRideDTO startRide(Long rideId, String otp);
    DriverRideDTO endRide(Long rideId);
    DriverRideDTO acceptRide(Long rideRequestId);
    RiderDTO rateRider(Long rideId, Integer rating);
    DriverDTO getMyProfile();
    Page<DriverRideDTO> getAllMyRides(Pageable pageable);
    Driver getCurrentDriver();
    Driver updateDriverStatus(Driver driver, com.abhilash.rideops.entities.enums.DriverStatus status);
    DriverDTO updateMyStatus(com.abhilash.rideops.entities.enums.DriverStatus status);
    DriverDTO updateMyLocation(com.abhilash.rideops.dto.PointDTO location);
    DriverDTO createNewDriver(Driver driver);
    Optional<Driver> findByVehicleId(String vehicleId);
}

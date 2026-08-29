package com.abhilash.rideops.services;


import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.DriverRideDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.dto.RiderRideDTO;
import com.abhilash.rideops.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DriverService {
    DriverRideDTO cancelRide(Long rideId); //driver id passed from Spring security context holder
    DriverRideDTO startRide(Long rideId, String otp);
    DriverRideDTO endRide(Long rideId);
    RiderRideDTO acceptRide(Long rideRequestId);
    RiderDTO rateRider(Long rideId, Integer rating);
    DriverDTO getMyProfile();
    Page<DriverRideDTO> getAllMyRides(PageRequest pageRequest);
    Driver getCurrentDriver();
    Driver updateDriverAvailability(Driver driver);
    DriverDTO createNewDriver(Driver driver);
    Driver findByVehicleId(String vehicleId);
}
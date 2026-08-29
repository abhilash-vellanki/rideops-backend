package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.entities.Rider;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import com.abhilash.rideops.entities.enums.RideStatus;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.repositories.RideRepository;
import com.abhilash.rideops.services.RideRequestService;
import com.abhilash.rideops.services.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper mapper;
    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);
        Ride ride=mapper.map(rideRequest,Ride.class);
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setOtp(generateRandomOTP());
        ride.setId(null);
        rideRequestService.update(rideRequest);
        Ride savedRide = rideRepository.save(ride);
        log.info("Ride created: rideId={}, rideRequestId={}, driverId={}, riderId={}",
                savedRide.getId(), rideRequest.getId(), driver.getId(), savedRide.getRider().getId());
        return savedRide;
    }

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow(
                ()->new ResourceNotFoundException("Ride not found: rideId="+rideId));
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        RideStatus previousStatus = ride.getRideStatus();
        ride.setRideStatus(rideStatus);
        Ride savedRide = rideRepository.save(ride);
        log.info("Ride status updated: rideId={}, previousStatus={}, status={}",
                savedRide.getId(), previousStatus, rideStatus);
        return savedRide;

    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {
        return rideRepository.findByRiderOrderByCreatedTimeDescIdDesc(rider,pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return rideRepository.findByDriverOrderByCreatedTimeDescIdDesc(driver,pageRequest);
    }

    private String generateRandomOTP(){
        Random random=new Random();
        int otp=random.nextInt(10000); // 0 to 9999
        return String.format("%04d",otp);
    }
}

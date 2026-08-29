package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.entities.Rider;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import com.abhilash.rideops.entities.enums.RideStatus;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.RideRepository;
import com.abhilash.rideops.services.RideRequestService;
import com.abhilash.rideops.services.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);
        Ride ride = new Ride();
        ride.setPickupLocation(rideRequest.getPickupLocation());
        ride.setDropOffLocation(rideRequest.getDropOffLocation());
        ride.setRider(rideRequest.getRider());
        ride.setPaymentMethod(rideRequest.getPaymentMethod());
        ride.setFare(rideRequest.getFare());
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setOtp(generateRandomOTP());
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
        if (!isAllowedTransition(previousStatus, rideStatus)) {
            throw new RuntimeConflictException("Ride " + ride.getId() + " cannot transition from "
                    + previousStatus + " to " + rideStatus);
        }
        ride.setRideStatus(rideStatus);
        Ride savedRide = rideRepository.save(ride);
        log.info("Ride status updated: rideId={}, previousStatus={}, status={}",
                savedRide.getId(), previousStatus, rideStatus);
        return savedRide;

    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, Pageable pageable) {
        return rideRepository.findByRiderOrderByCreatedTimeDescIdDesc(rider,pageable);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, Pageable pageable) {
        return rideRepository.findByDriverOrderByCreatedTimeDescIdDesc(driver,pageable);
    }

    private String generateRandomOTP(){
        int otp = SECURE_RANDOM.nextInt(10000);
        return String.format("%04d",otp);
    }

    private boolean isAllowedTransition(RideStatus currentStatus, RideStatus targetStatus) {
        return switch (currentStatus) {
            case CONFIRMED -> targetStatus == RideStatus.ONGOING || targetStatus == RideStatus.CANCELLED;
            case ONGOING -> targetStatus == RideStatus.ENDED;
            case CANCELLED, ENDED -> false;
        };
    }
}

package com.abhilash.rideops.services.impl;


import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.RideRequestDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.dto.RiderRideDTO;
import com.abhilash.rideops.entities.*;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import com.abhilash.rideops.entities.enums.RideStatus;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.RideRequestRepository;
import com.abhilash.rideops.repositories.RiderRepository;
import com.abhilash.rideops.services.DriverService;
import com.abhilash.rideops.services.RatingService;
import com.abhilash.rideops.services.RideService;
import com.abhilash.rideops.services.RiderService;
import com.abhilash.rideops.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideStrategyManager rideStrategyManager;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;
    @Override
    @Transactional
    public RideRequestDTO requestRide(RideRequestDTO rideRequestDTO) {
        Rider rider=getCurrrentRider();
        RideRequest rideRequest=modelMapper.map(rideRequestDTO,RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);

        Double fare=rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest=rideRequestRepository.save(rideRequest);

        List<Driver> drivers=rideStrategyManager
                .driverMatchingStrategy(rider.getRating()).findMatchingDrivers(rideRequest);

        log.info("Ride requested: rideRequestId={}, riderId={}, matchedDrivers={}",
                savedRideRequest.getId(), rider.getId(), drivers.size());
        return modelMapper.map(savedRideRequest,RideRequestDTO.class);
    }

    @Override
    public RiderRideDTO cancelRide(Long rideId) {
        Rider rider=getCurrrentRider();
        Ride ride=rideService.getRideById(rideId);
        if(!rider.equals(ride.getRider())){
            log.warn("Ride cancellation rejected because rider does not own ride: rideId={}, riderId={}",
                    rideId, rider.getId());
            throw new AccessDeniedException("Rider "+rider.getId()+" cannot cancel ride "+rideId
                    +" because it belongs to another rider");
        }
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            log.warn("Ride cancellation rejected because status is invalid: rideId={}, status={}",
                    rideId, ride.getRideStatus());
            throw new RuntimeConflictException("Ride "+rideId+" cannot be cancelled while status is "
                    +ride.getRideStatus()+"; expected CONFIRMED");
        }
        Ride savedRide=rideService.updateRideStatus(ride,RideStatus.CANCELLED);
        driverService.updateDriverAvailability(ride.getDriver());
        log.info("Ride cancelled by rider: rideId={}, riderId={}", rideId, rider.getId());
        return modelMapper.map(savedRide,RiderRideDTO.class);
    }

    @Override
    public DriverDTO rateDriver(Long rideId, Integer rating) {
        Ride ride=rideService.getRideById(rideId);
        Rider rider=getCurrrentRider();
        if(!rider.equals(ride.getRider())){
            log.warn("Driver rating rejected because rider does not own ride: rideId={}, riderId={}",
                    rideId, rider.getId());
            throw new AccessDeniedException("Rider "+rider.getId()+" cannot rate the driver for ride "+rideId
                    +" because it belongs to another rider");
        }
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            log.warn("Driver rating rejected because ride has not ended: rideId={}, status={}",
                    rideId, ride.getRideStatus());
            throw new RuntimeConflictException("The driver cannot be rated for ride "+rideId+" while status is "
                    +ride.getRideStatus()+"; expected ENDED");
        }
        DriverDTO driverDTO = ratingService.rateDriver(ride,rating);
        log.info("Driver rating submitted: rideId={}, riderId={}", rideId, rider.getId());
        return driverDTO;
    }

    @Override
    public RiderDTO getMyProfile() {
        Rider rider=getCurrrentRider();
        return modelMapper.map(rider,RiderDTO.class);

    }

    @Override
    public Page<RiderRideDTO> getAllMyRides(PageRequest pageRequest) {
        Rider rider=getCurrrentRider();
        return rideService.getAllRidesOfRider(rider,pageRequest)
                .map(ride -> modelMapper.map(ride, RiderRideDTO.class));

    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider=Rider.builder().user(user).rating(0.0).build();
        Rider savedRider = riderRepository.save(rider);
        log.info("Rider created: riderId={}, userId={}", savedRider.getId(), user.getId());
        return savedRider;
    }

    @Override
    public Rider getCurrrentRider() {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return riderRepository.findByUser(user).orElseThrow(
                ()->new ResourceNotFoundException("No rider profile is associated with userId="+user.getId()));
    }
}

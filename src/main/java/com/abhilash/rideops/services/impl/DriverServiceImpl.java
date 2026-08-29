package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.DriverRideDTO;
import com.abhilash.rideops.dto.PointDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.enums.DriverStatus;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import com.abhilash.rideops.entities.enums.RideStatus;
import com.abhilash.rideops.exceptions.InvalidRideOtpException;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.DriverRepository;
import com.abhilash.rideops.services.*;
import com.abhilash.rideops.utils.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper mapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Override
    public DriverDTO createNewDriver(Driver driver) {
        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver created: driverId={}, userId={}", savedDriver.getId(), savedDriver.getUser().getId());
        return mapper.map(savedDriver,DriverDTO.class);
    }

    @Override
    public Optional<Driver> findByVehicleId(String vehicleId) {
        return driverRepository.findByVehicleId(vehicleId);
    }


    @Override
    @Transactional
    public DriverRideDTO acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            log.warn("Ride acceptance rejected because request is not pending: rideRequestId={}, status={}",
                    rideRequestId, rideRequest.getRideRequestStatus());
            throw new RuntimeConflictException("Ride request "+rideRequestId+" cannot be accepted while status is "
                    +rideRequest.getRideRequestStatus()+"; expected PENDING");
        }
        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.getStatus().equals(DriverStatus.AVAILABLE)){
            log.warn("Ride acceptance rejected because driver is unavailable: driverId={}, status={}",
                    currentDriver.getId(), currentDriver.getStatus());
            throw new RuntimeConflictException("Driver "+currentDriver.getId()+" cannot accept a ride while status is "
                    +currentDriver.getStatus()+"; expected AVAILABLE");
        }
        Driver savedDriver = updateDriverStatus(currentDriver, DriverStatus.ON_TRIP);
        Ride ride=rideService.createNewRide(rideRequest,savedDriver);
        log.info("Ride accepted: rideId={}, rideRequestId={}, driverId={}",
                ride.getId(), rideRequestId, savedDriver.getId());
        return mapper.map(ride, DriverRideDTO.class);
    }

    @Override
    @Transactional
    public DriverRideDTO cancelRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();
        if (!Objects.equals(driver.getId(), ride.getDriver().getId())) {
            log.warn("Ride cancellation rejected because driver does not own ride: rideId={}, driverId={}",
                    rideId, driver.getId());
            throw new AccessDeniedException("Driver "+driver.getId()+" cannot cancel ride "+rideId
                    +" because it is assigned to another driver");
        }
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            log.warn("Ride cancellation rejected because status is invalid: rideId={}, status={}",
                    rideId, ride.getRideStatus());
            throw new RuntimeConflictException("Ride "+rideId+" cannot be cancelled while status is "
                    +ride.getRideStatus()+"; expected CONFIRMED");
        }
        rideService.updateRideStatus(ride,RideStatus.CANCELLED);
        updateDriverStatus(driver, DriverStatus.AVAILABLE);

        log.info("Ride cancelled by driver: rideId={}, driverId={}", rideId, driver.getId());
        return mapper.map(ride, DriverRideDTO.class);
    }

    @Override
    @Transactional
    public DriverRideDTO startRide(Long rideId, String otp) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver=getCurrentDriver();
        if (!Objects.equals(driver.getId(), ride.getDriver().getId())) {
            log.warn("Ride start rejected because driver does not own ride: rideId={}, driverId={}",
                    rideId, driver.getId());
            throw new AccessDeniedException("Driver "+driver.getId()+" cannot start ride "+rideId
                    +" because it is assigned to another driver");
        }
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            log.warn("Ride start rejected because status is invalid: rideId={}, status={}",
                    rideId, ride.getRideStatus());
            throw new RuntimeConflictException("Ride "+rideId+" cannot be started while status is "
                    +ride.getRideStatus()+"; expected CONFIRMED");
        }
        if(!Objects.equals(otp, ride.getOtp())){
            log.warn("Ride start rejected because OTP validation failed: rideId={}, driverId={}",
                    rideId, driver.getId());
            throw new InvalidRideOtpException("The supplied OTP is invalid for ride "+rideId);
        }
        ride.setOtp(null);
        ride.setStartedAt(LocalDateTime.now());
        Ride savedRide=rideService.updateRideStatus(ride, RideStatus.ONGOING);
        paymentService.createNewPayment(savedRide);
        ratingService.createNewRating(savedRide);
        log.info("Ride started: rideId={}, driverId={}", rideId, driver.getId());
        return mapper.map(savedRide, DriverRideDTO.class);
    }

    @Override
    @Transactional
    public DriverRideDTO endRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();
        if (!Objects.equals(driver.getId(), ride.getDriver().getId())) {
            log.warn("Ride completion rejected because driver does not own ride: rideId={}, driverId={}",
                    rideId, driver.getId());
            throw new AccessDeniedException("Driver "+driver.getId()+" cannot complete ride "+rideId
                    +" because it is assigned to another driver");
        }
        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            log.warn("Ride completion rejected because status is invalid: rideId={}, status={}",
                    rideId, ride.getRideStatus());
            throw new RuntimeConflictException("Ride "+rideId+" cannot be completed while status is "
                    +ride.getRideStatus()+"; expected ONGOING");
        }
        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide=rideService.updateRideStatus(ride, RideStatus.ENDED);
        updateDriverStatus(driver, DriverStatus.AVAILABLE);
        paymentService.processPayment(savedRide);
        log.info("Ride completed: rideId={}, driverId={}", rideId, driver.getId());
        return mapper.map(savedRide,DriverRideDTO.class);

    }



    @Override
    @Transactional
    public RiderDTO rateRider(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver=getCurrentDriver();
        if (!Objects.equals(driver.getId(), ride.getDriver().getId())) {
            log.warn("Rider rating rejected because driver does not own ride: rideId={}, driverId={}",
                    rideId, driver.getId());
            throw new AccessDeniedException("Driver "+driver.getId()+" cannot rate the rider for ride "+rideId
                    +" because it is assigned to another driver");
        }
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            log.warn("Rider rating rejected because ride has not ended: rideId={}, status={}",
                    rideId, ride.getRideStatus());
            throw new RuntimeConflictException("The rider cannot be rated for ride "+rideId+" while status is "
                    +ride.getRideStatus()+"; expected ENDED");
        }
        RiderDTO riderDTO = ratingService.rateRider(ride,rating);
        log.info("Rider rating submitted: rideId={}, driverId={}", rideId, driver.getId());
        return riderDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public DriverDTO getMyProfile() {
        Driver currentDriver=getCurrentDriver();
        return mapper.map(currentDriver,DriverDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverRideDTO> getAllMyRides(org.springframework.data.domain.Pageable pageRequest) {
        Driver currentDriver=getCurrentDriver();
        return rideService.getAllRidesOfDriver(currentDriver,pageRequest)
                .map(ride -> mapper.map(ride,DriverRideDTO.class));
    }

    @Override
    public Driver getCurrentDriver() {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findByUser(user).orElseThrow(
                ()->new ResourceNotFoundException("No driver profile is associated with userId="+user.getId()));
    }

    @Override
    public Driver updateDriverStatus(Driver driver, DriverStatus status) {
        driver.setStatus(status);
        Driver savedDriver = driverRepository.save(driver);
        log.debug("Driver status updated: driverId={}, status={}", savedDriver.getId(), savedDriver.getStatus());
        return savedDriver;
    }

    @Override
    @Transactional
    public DriverDTO updateMyStatus(DriverStatus status) {
        if (status != DriverStatus.AVAILABLE && status != DriverStatus.OFFLINE) {
            throw new IllegalArgumentException("Drivers may only set their status to AVAILABLE or OFFLINE");
        }
        Driver driver = getCurrentDriver();
        if (driver.getStatus() == DriverStatus.ON_TRIP) {
            throw new RuntimeConflictException("Driver status cannot be changed while a ride is in progress");
        }
        if (driver.getStatus() == DriverStatus.SUSPENDED) {
            throw new RuntimeConflictException("A suspended driver cannot change availability");
        }
        if (status == DriverStatus.AVAILABLE && driver.getCurrentLocation() == null) {
            throw new RuntimeConflictException("A current location is required before becoming available");
        }
        return mapper.map(updateDriverStatus(driver, status), DriverDTO.class);
    }

    @Override
    @Transactional
    public DriverDTO updateMyLocation(PointDTO location) {
        Driver driver = getCurrentDriver();
        if (driver.getStatus() == DriverStatus.SUSPENDED) {
            throw new RuntimeConflictException("A suspended driver cannot update location");
        }
        driver.setCurrentLocation(GeometryUtil.createPoint(location));
        return mapper.map(driverRepository.save(driver), DriverDTO.class);
    }

}

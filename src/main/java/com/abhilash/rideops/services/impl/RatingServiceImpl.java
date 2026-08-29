package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.RiderDTO;
import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Rating;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.Rider;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.DriverRepository;
import com.abhilash.rideops.repositories.RatingRepository;
import com.abhilash.rideops.repositories.RiderRepository;
import com.abhilash.rideops.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverDTO rateDriver(Ride ride, Integer rating) {
        Driver driver=ride.getDriver();
        Rating ratingObj=ratingRepository.findByRide(ride)
                .orElseThrow(()->new ResourceNotFoundException("Rating record not found: rideId="+ride.getId()));
        if(ratingObj.getDriverRating()!=null) {
            throw new RuntimeConflictException("A driver rating has already been submitted for rideId="+ride.getId());
        }
        ratingObj.setDriverRating(rating);
        ratingRepository.save(ratingObj);
        Double avgRating= ratingRepository.findByDriver(driver).stream()
                .mapToDouble(Rating::getDriverRating)
                .average().orElse(0.0);

        driver.setRating(avgRating);
        Driver savedDriver=driverRepository.save(driver);
        log.info("Driver rating updated: rideId={}, driverId={}, rating={}, averageRating={}",
                ride.getId(), driver.getId(), rating, avgRating);
        return modelMapper.map(savedDriver, DriverDTO.class);
    }

    @Override
    public RiderDTO rateRider(Ride ride, Integer rating) {
        Rider rider=ride.getRider();
        Rating ratingObj=ratingRepository.findByRide(ride)
                .orElseThrow(()->new ResourceNotFoundException("Rating record not found: rideId="+ride.getId()));
        if(ratingObj.getRiderRating()!=null) {
            throw new RuntimeConflictException("A rider rating has already been submitted for rideId="+ride.getId());
        }
        ratingObj.setRiderRating(rating);
        ratingRepository.save(ratingObj);
        Double avgRating= ratingRepository.findByRider(rider).stream()
                .mapToDouble(Rating::getDriverRating)
                .average().orElse(0.0);

        rider.setRating(avgRating);
        Rider savedRider=riderRepository.save(rider);
        log.info("Rider rating updated: rideId={}, riderId={}, rating={}, averageRating={}",
                ride.getId(), rider.getId(), rating, avgRating);
        return modelMapper.map(savedRider, RiderDTO.class);

    }

    @Override
    public void createNewRating(Ride ride) {
        Rating rating=Rating.builder()
                .driver(ride.getDriver())
                .ride(ride)
                .rider(ride.getRider())
                .build();
        Rating savedRating = ratingRepository.save(rating);
        log.debug("Rating record created: ratingId={}, rideId={}", savedRating.getId(), ride.getId());

    }
}

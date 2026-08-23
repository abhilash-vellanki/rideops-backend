package com.abhilash.rideops.repositories;


import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Rating;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating,Long> {
    List<Rating> findByRider(Rider rider);
    List<Rating> findByDriver(Driver driver);
    Optional<Rating> findByRide(Ride ride);
}
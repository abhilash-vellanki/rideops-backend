package com.abhilash.rideops.repositories;


import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Rating;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating,Long> {
    Optional<Rating> findByRide(Ride ride);

    @Query("select avg(rating.driverRating) from Rating rating where rating.driver = :driver and rating.driverRating is not null")
    Optional<Double> averageDriverRating(@Param("driver") Driver driver);

    @Query("select avg(rating.riderRating) from Rating rating where rating.rider = :rider and rating.riderRating is not null")
    Optional<Double> averageRiderRating(@Param("rider") Rider rider);
}

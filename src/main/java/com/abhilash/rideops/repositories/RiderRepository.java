package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Rider;
import com.abhilash.rideops.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider,Long> {
    Optional<Rider> findByUser(User user);
}

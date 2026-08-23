package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRequestRepository extends JpaRepository<RideRequest,Long> {
}

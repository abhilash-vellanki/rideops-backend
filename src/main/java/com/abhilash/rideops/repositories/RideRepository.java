package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.Rider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride,Long> {
    Page<Ride> findByRiderOrderByCreatedTimeDescIdDesc(Rider rider, Pageable pageable);
    Page<Ride> findByDriverOrderByCreatedTimeDescIdDesc(Driver driver, Pageable pageable);
}

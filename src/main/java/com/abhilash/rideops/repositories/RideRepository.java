package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.Rider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride,Long> {
    @EntityGraph(attributePaths = {"rider", "rider.user", "driver", "driver.user"})
    Page<Ride> findByRiderOrderByCreatedTimeDescIdDesc(Rider rider, Pageable pageable);
    @EntityGraph(attributePaths = {"rider", "rider.user", "driver", "driver.user"})
    Page<Ride> findByDriverOrderByCreatedTimeDescIdDesc(Driver driver, Pageable pageable);
}

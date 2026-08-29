package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.locationtech.jts.geom.Point;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;


import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    @Query(value = """
        SELECT d.*
        FROM driver d
        WHERE d.status = 'AVAILABLE'
          AND ST_DWithin(
                  CAST(d.current_location AS geography),
                  CAST(:pickupLocation AS geography),
                  10000
              )
        ORDER BY ST_Distance(
                     CAST(d.current_location AS geography),
                     CAST(:pickupLocation AS geography)
                 ), d.id
        LIMIT 10
        """, nativeQuery = true)
    List<Driver> findTenNearestDrivers(
            @Param("pickupLocation") Point pickupLocation
    );

    @Query(value = """
        SELECT d.*
        FROM driver d
        WHERE d.status = 'AVAILABLE'
          AND ST_DWithin(
                  CAST(d.current_location AS geography),
                  CAST(:pickupLocation AS geography),
                  15000
              )
        ORDER BY d.rating DESC NULLS LAST,
                 ST_Distance(
                     CAST(d.current_location AS geography),
                     CAST(:pickupLocation AS geography)
                 ), d.id
        LIMIT 10
        """, nativeQuery = true)
    List<Driver> findTopRatedDrivers(
            @Param("pickupLocation") Point pickupLocation
    );
    Optional<Driver> findByVehicleId(String vehicleId);
    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<Driver> findByUser(User user);
}

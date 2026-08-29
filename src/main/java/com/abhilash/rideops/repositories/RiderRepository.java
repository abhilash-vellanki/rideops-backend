package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.Rider;
import com.abhilash.rideops.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider,Long> {
    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<Rider> findByUser(User user);
}

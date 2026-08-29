package com.abhilash.rideops.repositories;

import com.abhilash.rideops.entities.RefreshTokenSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSession, Long> {
    Optional<RefreshTokenSession> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            update RefreshTokenSession session
               set session.revokedAt = :revokedAt,
                   session.version = session.version + 1
             where session.user.id = :userId
               and session.revokedAt is null
            """)
    int revokeAllActiveForUser(@Param("userId") Long userId, @Param("revokedAt") Instant revokedAt);
}

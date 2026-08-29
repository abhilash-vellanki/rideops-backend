package com.abhilash.rideops.entities;

import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OptimisticLockingConfigurationTest {
    @Test
    void mutableAggregatesDeclareVersionColumns() throws NoSuchFieldException {
        List<Class<?>> versionedEntities = List.of(
                User.class,
                Rider.class,
                Driver.class,
                RideRequest.class,
                Ride.class,
                Payment.class,
                Rating.class,
                Wallet.class,
                RefreshTokenSession.class
        );

        for (Class<?> entityType : versionedEntities) {
            assertNotNull(entityType.getDeclaredField("version").getAnnotation(Version.class),
                    () -> entityType.getSimpleName() + " must use optimistic locking");
        }
    }
}

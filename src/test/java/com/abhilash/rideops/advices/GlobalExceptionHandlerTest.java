package com.abhilash.rideops.advices;

import com.abhilash.rideops.exceptions.ExternalServiceException;
import com.abhilash.rideops.exceptions.InvalidRideOtpException;
import com.abhilash.rideops.exceptions.MissingRefreshTokenException;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import jakarta.persistence.OptimisticLockException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/rides/42");
    }

    @Test
    void mapsBusinessExceptionsToExpectedStatuses() {
        ResponseEntity<ApiErrors> notFound = handler.handleResourceNotFound(
                new ResourceNotFoundException("Ride not found: rideId=42"), request
        );
        ResponseEntity<ApiErrors> conflict = handler.handleConflict(
                new RuntimeConflictException("Ride cannot be cancelled"), request
        );
        ResponseEntity<ApiErrors> invalidOtp = handler.handleInvalidRideOtp(
                new InvalidRideOtpException("The supplied OTP is invalid for ride 42"), request
        );
        ResponseEntity<ApiErrors> missingRefreshToken = handler.handleMissingRefreshToken(
                new MissingRefreshTokenException("Refresh token cookie is missing"), request
        );

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, notFound.getStatusCode()),
                () -> assertEquals(HttpStatus.CONFLICT, conflict.getStatusCode()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, invalidOtp.getStatusCode()),
                () -> assertEquals(HttpStatus.UNAUTHORIZED, missingRefreshToken.getStatusCode()),
                () -> assertEquals("/rides/42", notFound.getBody().getPath())
        );
    }

    @Test
    void mapsExternalServiceFailuresToServiceUnavailable() {
        ResponseEntity<ApiErrors> response = handler.handleExternalServiceFailure(
                new ExternalServiceException("Routing service is unavailable", new RuntimeException()),
                request
        );

        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()),
                () -> assertEquals(503, response.getBody().getStatus()),
                () -> assertEquals("Service Unavailable", response.getBody().getError())
        );
    }

    @Test
    void mapsOptimisticLockConflictsWithoutExposingPersistenceDetails() {
        ResponseEntity<ApiErrors> response = handler.handlePersistenceConflict(
                new OptimisticLockException("stale version"), request);

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, response.getStatusCode()),
                () -> assertEquals("The request conflicts with the current stored data",
                        response.getBody().getMessage())
        );
    }

    @Test
    void unexpectedFailuresDoNotExposeInternalExceptionMessages() {
        ResponseEntity<ApiErrors> response = handler.handleUnexpectedException(
                new RuntimeException("database password was rejected"),
                request
        );

        assertNotNull(response.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals("An unexpected error occurred", response.getBody().getMessage()),
                () -> assertEquals("/rides/42", response.getBody().getPath()),
                () -> assertNotNull(response.getBody().getTimestamp())
        );
    }
}

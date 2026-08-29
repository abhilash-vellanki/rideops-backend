package com.abhilash.rideops.dto;

import com.abhilash.rideops.entities.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateRideRequestDTO(
        @Valid @NotNull PointDTO pickupLocation,
        @Valid @NotNull PointDTO dropOffLocation,
        @NotNull PaymentMethod paymentMethod
) {
}

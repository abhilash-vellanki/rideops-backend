package com.abhilash.rideops.dto;

import com.abhilash.rideops.entities.enums.DriverStatus;
import jakarta.validation.constraints.NotNull;

public record DriverStatusUpdateDTO(@NotNull DriverStatus status) {
}

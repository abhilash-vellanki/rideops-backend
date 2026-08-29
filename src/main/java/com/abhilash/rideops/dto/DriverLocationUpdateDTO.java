package com.abhilash.rideops.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DriverLocationUpdateDTO(@Valid @NotNull PointDTO location) {
}

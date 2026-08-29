package com.abhilash.rideops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DriverOnboardingDTO(
        @NotBlank @Size(max = 50) String vehicleId
) {
}

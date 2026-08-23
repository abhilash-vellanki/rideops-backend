package com.abhilash.rideops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RideStartDTO {
    @NotBlank
    @Pattern(regexp = "\\d{4,6}")
    private String otp;
}

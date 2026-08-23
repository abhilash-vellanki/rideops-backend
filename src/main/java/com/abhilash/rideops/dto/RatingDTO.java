package com.abhilash.rideops.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    @NotNull
    @Positive
    Long rideId;

    @NotNull
    @Min(1)
    @Max(5)
    Integer rating;
}

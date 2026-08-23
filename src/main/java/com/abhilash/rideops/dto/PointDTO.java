package com.abhilash.rideops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointDTO {
    @NotNull
    @Size(min = 2, max = 2)
    private Double[] coordinates;

    @NotBlank
    @Pattern(regexp = "Point")
    private String type = "Point";

    public PointDTO(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}

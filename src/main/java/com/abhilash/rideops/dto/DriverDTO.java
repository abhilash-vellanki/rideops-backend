package com.abhilash.rideops.dto;


import com.abhilash.rideops.entities.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private Long id;
    private UserDTO user;
    private Double rating;
    private DriverStatus status;
    private PointDTO currentLocation;
    private String vehicleId;
}

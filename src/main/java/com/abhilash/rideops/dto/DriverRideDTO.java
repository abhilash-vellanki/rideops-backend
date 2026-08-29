package com.abhilash.rideops.dto;

import com.abhilash.rideops.entities.enums.PaymentMethod;
import com.abhilash.rideops.entities.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRideDTO {
    private Long id;
    private PointDTO pickupLocation;
    private PointDTO dropOffLocation;
    private LocalDateTime createdTime;
    private RiderDTO rider;
    private DriverDTO driver;
    private PaymentMethod paymentMethod;
    private RideStatus rideStatus;
    private BigDecimal fare;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}

package com.abhilash.rideops.dto;
import com.abhilash.rideops.entities.enums.PaymentMethod;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDTO {
    private Long id;
    @Valid
    @NotNull
    private PointDTO pickupLocation;
    @Valid
    @NotNull
    private PointDTO dropOffLocation;
    @NotNull
    private PaymentMethod paymentMethod;
    private LocalDateTime createdTime;
    private RiderDTO rider;
    private Double fare;
    private RideRequestStatus rideStatus;

}

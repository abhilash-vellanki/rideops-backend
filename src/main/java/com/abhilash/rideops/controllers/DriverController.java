package com.abhilash.rideops.controllers;

import com.abhilash.rideops.dto.*;
import com.abhilash.rideops.services.DriverService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Secured("ROLE_DRIVER")
@Validated
public class DriverController {

    private final DriverService driverService;

    @PatchMapping("/status")
    public ResponseEntity<DriverDTO> updateStatus(@Valid @RequestBody DriverStatusUpdateDTO request) {
        return ResponseEntity.ok(driverService.updateMyStatus(request.status()));
    }

    @PatchMapping("/location")
    public ResponseEntity<DriverDTO> updateLocation(@Valid @RequestBody DriverLocationUpdateDTO request) {
        return ResponseEntity.ok(driverService.updateMyLocation(request.location()));
    }
    @PostMapping("/acceptRide/{rideRequestId}")
    public ResponseEntity<DriverRideDTO> acceptRide(@PathVariable Long rideRequestId){
        return ResponseEntity.ok(driverService.acceptRide(rideRequestId));
    }

    @PostMapping("/startRide/{rideId}")
    public ResponseEntity<DriverRideDTO> startRide(
            @PathVariable Long rideId,
            @Valid @RequestBody RideStartDTO rideStartDTO
    ) {
        return ResponseEntity.ok(driverService.startRide(rideId,rideStartDTO.getOtp()));
    }


    @PostMapping("/endRide/{rideId}")
    public ResponseEntity<DriverRideDTO> endRide(@PathVariable Long rideId){
        return ResponseEntity.ok(driverService.endRide(rideId));
    }


    @PostMapping("/rateRider")
    public ResponseEntity<RiderDTO> rateRider(@Valid @RequestBody RatingDTO ratingDTO){
        return ResponseEntity.ok(driverService.rateRider(ratingDTO.getRideId(),ratingDTO.getRating()));
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<DriverRideDTO> cancelRide(@PathVariable Long rideId){
        return ResponseEntity.ok(driverService.cancelRide(rideId));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<DriverDTO> getMyProfile(){
        return ResponseEntity.ok(driverService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<DriverRideDTO>> getMyRides(
            @RequestParam(defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize);
        return ResponseEntity.ok(driverService.getAllMyRides(pageRequest));
    }

}

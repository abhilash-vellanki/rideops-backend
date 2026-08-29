package com.abhilash.rideops.controllers;

import com.abhilash.rideops.dto.*;
import com.abhilash.rideops.services.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/riders")
@RequiredArgsConstructor
@Secured("ROLE_RIDER")
@Validated
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/requestRide")
    public ResponseEntity<RideRequestDTO> requestRide(@Valid @RequestBody CreateRideRequestDTO rideRequestDTO){
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(riderService.requestRide(rideRequestDTO));
    }


    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RiderRideDTO> cancelRide(@PathVariable Long rideId){
        return ResponseEntity.ok(riderService.cancelRide(rideId));
    }


    @PostMapping("/rateDriver")
    public ResponseEntity<DriverDTO> rateDriver(@Valid @RequestBody RatingDTO ratingDTO){
        return ResponseEntity.ok(riderService.rateDriver(ratingDTO.getRideId(),ratingDTO.getRating()));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<RiderDTO> getMyProfile(){
        return ResponseEntity.ok(riderService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RiderRideDTO>> getMyRides(
            @RequestParam(defaultValue = "0") @Min(0) Integer pageNumber,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize);
        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }

}

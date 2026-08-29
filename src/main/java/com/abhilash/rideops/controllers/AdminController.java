package com.abhilash.rideops.controllers;

import com.abhilash.rideops.dto.PlatformCommissionSummaryDTO;
import com.abhilash.rideops.dto.DriverDTO;
import com.abhilash.rideops.dto.DriverOnboardingDTO;
import com.abhilash.rideops.services.AuthService;
import jakarta.validation.Valid;
import com.abhilash.rideops.services.PlatformCommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminController {

    private final PlatformCommissionService platformCommissionService;
    private final AuthService authService;

    @PostMapping("/drivers/{userId}")
    public ResponseEntity<DriverDTO> onBoardNewDriver(
            @PathVariable Long userId,
            @Valid @RequestBody DriverOnboardingDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.onBoardNewDriver(userId, request.vehicleId()));
    }

    @GetMapping("/platform-commission/summary")
    public ResponseEntity<PlatformCommissionSummaryDTO> getPlatformCommissionSummary() {
        return ResponseEntity.ok(platformCommissionService.getSummary());
    }
}

package com.abhilash.rideops.controllers;

import com.abhilash.rideops.dto.PlatformCommissionSummaryDTO;
import com.abhilash.rideops.services.PlatformCommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminController {

    private final PlatformCommissionService platformCommissionService;

    @GetMapping("/platform-commission/summary")
    public ResponseEntity<PlatformCommissionSummaryDTO> getPlatformCommissionSummary() {
        return ResponseEntity.ok(platformCommissionService.getSummary());
    }
}

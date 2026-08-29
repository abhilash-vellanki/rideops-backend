package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.repositories.RideRequestRepository;
import com.abhilash.rideops.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Ride request not found: rideRequestId="+rideRequestId));
    }

    @Override
    public void update(RideRequest rideRequest) {
        rideRequestRepository.save(rideRequest);
        log.info("Ride request updated: rideRequestId={}, status={}",
                rideRequest.getId(), rideRequest.getRideRequestStatus());
    }
}

package com.abhilash.rideops.config;

import com.abhilash.rideops.dto.PointDTO;
import com.abhilash.rideops.dto.RideRequestDTO;
import com.abhilash.rideops.dto.WalletTransactionDTO;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.WalletTransactions;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import com.abhilash.rideops.utils.GeometryUtil;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperConfigTest {

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    @Test
    void mapsRideRequestMismatchesAndPointsBothWays() {
        LocalDateTime requestedTime = LocalDateTime.of(2026, 8, 24, 1, 30);
        RideRequest rideRequest = new RideRequest();
        rideRequest.setRequestedTime(requestedTime);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setPickupLocation(GeometryUtil.createPoint(
                new PointDTO(new Double[]{77.5946, 12.9716})));

        RideRequestDTO dto = modelMapper.map(rideRequest, RideRequestDTO.class);
        RideRequest mappedBack = modelMapper.map(dto, RideRequest.class);

        assertAll(
                () -> assertEquals(requestedTime, dto.getCreatedTime()),
                () -> assertEquals(RideRequestStatus.PENDING, dto.getRideStatus()),
                () -> assertEquals(77.5946, dto.getPickupLocation().getCoordinates()[0]),
                () -> assertEquals(12.9716, dto.getPickupLocation().getCoordinates()[1]),
                () -> assertEquals(4326, mappedBack.getPickupLocation().getSRID()),
                () -> assertEquals(requestedTime, mappedBack.getRequestedTime()),
                () -> assertEquals(RideRequestStatus.PENDING, mappedBack.getRideRequestStatus())
        );
    }

    @Test
    void mapsWalletTransactionAssociationsToIds() {
        Ride ride = new Ride();
        ride.setId(41L);
        Wallet wallet = new Wallet();
        wallet.setId(73L);
        WalletTransactions transaction = new WalletTransactions();
        transaction.setRide(ride);
        transaction.setWallet(wallet);

        WalletTransactionDTO dto = modelMapper.map(transaction, WalletTransactionDTO.class);

        assertAll(
                () -> assertEquals(41L, dto.getRideId()),
                () -> assertEquals(73L, dto.getWalletId())
        );
    }
}

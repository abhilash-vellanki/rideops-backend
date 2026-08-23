package com.abhilash.rideops.config;

import com.abhilash.rideops.dto.PointDTO;
import com.abhilash.rideops.dto.RideRequestDTO;
import com.abhilash.rideops.dto.WalletTransactionDTO;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.RideRequest;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.WalletTransactions;
import com.abhilash.rideops.utils.GeometryUtil;
import org.locationtech.jts.geom.Point;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(PointDTO.class, Point.class)
                .setConverter(context -> context.getSource() == null
                        ? null
                        : GeometryUtil.createPoint(context.getSource()));
        modelMapper.typeMap(Point.class, PointDTO.class)
                .setConverter(context -> context.getSource() == null
                        ? null
                        : GeometryUtil.createPointDTO(context.getSource()));

        modelMapper.typeMap(RideRequest.class, RideRequestDTO.class)
                .addMappings(mapper -> {
                    mapper.map(RideRequest::getRequestedTime, RideRequestDTO::setCreatedTime);
                    mapper.map(RideRequest::getRideRequestStatus, RideRequestDTO::setRideStatus);
                });
        modelMapper.typeMap(RideRequestDTO.class, RideRequest.class)
                .addMappings(mapper -> {
                    mapper.map(RideRequestDTO::getCreatedTime, RideRequest::setRequestedTime);
                    mapper.map(RideRequestDTO::getRideStatus, RideRequest::setRideRequestStatus);
                });

        Converter<Ride, Long> rideToId = context -> context.getSource() == null
                ? null
                : context.getSource().getId();
        Converter<Wallet, Long> walletToId = context -> context.getSource() == null
                ? null
                : context.getSource().getId();
        modelMapper.typeMap(WalletTransactions.class, WalletTransactionDTO.class)
                .addMappings(mapper -> {
                    mapper.using(rideToId).map(WalletTransactions::getRide, WalletTransactionDTO::setRideId);
                    mapper.using(walletToId).map(WalletTransactions::getWallet, WalletTransactionDTO::setWalletId);
                });

        return modelMapper;
    }
}

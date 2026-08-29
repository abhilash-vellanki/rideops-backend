package com.abhilash.rideops.services.impl;


import com.abhilash.rideops.exceptions.ExternalServiceException;
import com.abhilash.rideops.services.DistanceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@Slf4j
public class DistanceServiceOSRMImpl implements DistanceService {

    private final RestClient osrmRestClient;

    public DistanceServiceOSRMImpl(@Qualifier("osrmRestClient") RestClient osrmRestClient) {
        this.osrmRestClient = osrmRestClient;
    }

    @Override
    public double calculateDistance(Point src, Point dest) {
        if (src == null || dest == null) {
            throw new IllegalArgumentException("Both source and destination locations are required");
        }
        try {
            String uri=src.getX()+","+src.getY()+";"+dest.getX()+","+dest.getY();
            OSRMResponseDto responseDto = osrmRestClient
                    .get()
                    .uri(uri + "?overview=false")
                    .retrieve()
                    .body(OSRMResponseDto.class);
            if (responseDto == null || responseDto.getRoutes() == null
                    || responseDto.getRoutes().isEmpty()
                    || responseDto.getRoutes().getFirst().getDistance() == null) {
                throw new ExternalServiceException("The routing service returned no usable route");
            }
            double distanceInKilometers = responseDto.getRoutes().get(0).getDistance() / 1000.0;
            log.debug("OSRM route distance calculated: distanceKm={}", distanceInKilometers);
            return distanceInKilometers;
        }
        catch (ExternalServiceException exception) {
            throw exception;
        }
        catch (RestClientException exception){
            log.error("OSRM distance request failed", exception);
            throw new ExternalServiceException(
                    "Unable to calculate route distance because the routing service request failed", exception);
        }
    }
}

@Data
class OSRMResponseDto{
    private List<OSRMRoutes> routes;
}

@Data
class OSRMRoutes{
    private Double distance;
}

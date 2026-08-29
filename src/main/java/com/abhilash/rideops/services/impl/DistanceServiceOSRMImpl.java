package com.abhilash.rideops.services.impl;


import com.abhilash.rideops.services.DistanceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class DistanceServiceOSRMImpl implements DistanceService {

    private static final String OSRM_API_BASE_URL="http://router.project-osrm.org/route/v1/driving/";
    @Override
    public double calculateDistance(Point src, Point dest) {
        try {
            String uri=src.getX()+","+src.getY()+";"+dest.getX()+","+dest.getY();
            OSRMResponseDto responseDto = RestClient.builder()
                    .baseUrl(OSRM_API_BASE_URL)
                    .build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(OSRMResponseDto.class);
            double distanceInKilometers = responseDto.getRoutes().get(0).getDistance() / 1000.0;
            log.debug("OSRM route distance calculated: distanceKm={}", distanceInKilometers);
            return distanceInKilometers;
        }
        catch (Exception e){
            log.error("OSRM distance request failed", e);
            throw new IllegalStateException(
                    "Unable to calculate route distance because the routing service request failed", e);
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

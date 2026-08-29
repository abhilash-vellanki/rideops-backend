package com.abhilash.rideops.strategies;

import com.abhilash.rideops.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.abhilash.rideops.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.abhilash.rideops.strategies.impl.RideFareDefaultFareCalculationStrategy;
import com.abhilash.rideops.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategy surgePricingFareCalculationStrategy;
    private final RideFareDefaultFareCalculationStrategy defaultFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating){
        if(riderRating>4.8){
            log.debug("Selected highest-rated driver matching strategy");
            return highestRatedDriverStrategy;
        }
        else{
            log.debug("Selected nearest-driver matching strategy");
            return nearestDriverStrategy;
        }
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy(){
        LocalTime surgeStartTime=LocalTime.of(20,0);// 20:00 (8 PM)
        LocalTime surgeEndTime=LocalTime.of(6,0);// (6 AM)
        LocalTime currentTime=LocalTime.now();
        boolean isSurgeTime=isCurrentTimeInRange(currentTime, surgeStartTime, surgeEndTime);
        log.debug("Selected fare calculation strategy: surgePricing={}", isSurgeTime);

        if(isSurgeTime) return surgePricingFareCalculationStrategy;
        else return defaultFareCalculationStrategy;

    }

    public static boolean isCurrentTimeInRange(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(endTime)) {
            // Range does not span across midnight
            return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        } else {
            // Range spans across midnight
            return !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
        }
    }

}

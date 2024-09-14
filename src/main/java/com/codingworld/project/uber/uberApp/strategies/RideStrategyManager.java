package com.codingworld.project.uber.uberApp.strategies;

import com.codingworld.project.uber.uberApp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.codingworld.project.uber.uberApp.strategies.impl.DriverMatchingNearestDriverStrategyImpl;
import com.codingworld.project.uber.uberApp.strategies.impl.RideFareDefaultFareCalculationStrategy;
import com.codingworld.project.uber.uberApp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {
    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategyImpl nearestDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategy surgePricingFareCalculationStrategy;
    private final RideFareDefaultFareCalculationStrategy defaultFareCalculationStrategy;
    public DriverMatchingStrategy driverMatchingStrategy(double rideRating) {
        if(rideRating >= 4.0) {
            return highestRatedDriverStrategy;
        }
        return nearestDriverStrategy;
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy() {
        LocalTime surgeStartTime = LocalTime.of(18, 0);
        LocalTime surgeEndTime = LocalTime.of(21, 0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);
        if(isSurgeTime) {
            return surgePricingFareCalculationStrategy;
        }
        return defaultFareCalculationStrategy;
    }
}

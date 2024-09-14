package com.codingworld.project.uber.uberApp.strategies.impl;

import com.codingworld.project.uber.uberApp.entities.Driver;
import com.codingworld.project.uber.uberApp.entities.RideRequest;
import com.codingworld.project.uber.uberApp.repositories.DriverRepository;
import com.codingworld.project.uber.uberApp.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategyImpl implements DriverMatchingStrategy {
    private final DriverRepository driverRepository;
    @Override
    public List<Driver> findMatchingDriver(RideRequest rideRequest) {
        return driverRepository.findTenNearestDrivers(rideRequest.getPickupLocation());
    }
}

package com.codingworld.project.uber.uberApp.strategies;

import com.codingworld.project.uber.uberApp.entities.Driver;
import com.codingworld.project.uber.uberApp.entities.RideRequest;

import java.util.List;

public interface DriverMatchingStrategy {

     List<Driver> findMatchingDriver(RideRequest rideRequest);
}

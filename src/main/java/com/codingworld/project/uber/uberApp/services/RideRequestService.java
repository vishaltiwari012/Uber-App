package com.codingworld.project.uber.uberApp.services;

import com.codingworld.project.uber.uberApp.entities.RideRequest;

public interface RideRequestService {
    RideRequest findRideRequestById(Long rideRequestId);

    void update(RideRequest rideRequest);
}

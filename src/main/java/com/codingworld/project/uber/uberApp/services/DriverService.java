package com.codingworld.project.uber.uberApp.services;

import com.codingworld.project.uber.uberApp.dto.DriverDTO;
import com.codingworld.project.uber.uberApp.dto.RideDTO;
import com.codingworld.project.uber.uberApp.dto.RiderDTO;
import com.codingworld.project.uber.uberApp.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DriverService {
    RideDTO acceptRide(Long rideRequestId);
    RideDTO cancelRide(Long rideId);
    RideDTO startRide(Long rideId, String otp);
    RideDTO endRide(Long rideId);
    RiderDTO rateRider(Long rideId, Integer rating);
    DriverDTO getMyProfile();
    Page<RideDTO> getAllMyRides(PageRequest pageRequest);

    Driver getCurrentDriver();

    Driver updateDriverAvailability(Driver driver, boolean available);

    Driver createNewDriver(Driver driver);
}

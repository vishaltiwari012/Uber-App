package com.codingworld.project.uber.uberApp.services;

import com.codingworld.project.uber.uberApp.dto.DriverDTO;
import com.codingworld.project.uber.uberApp.dto.RideDTO;
import com.codingworld.project.uber.uberApp.dto.RideRequestDTO;
import com.codingworld.project.uber.uberApp.dto.RiderDTO;
import com.codingworld.project.uber.uberApp.entities.Rider;
import com.codingworld.project.uber.uberApp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RiderService {
    RideRequestDTO requestRide(RideRequestDTO rideRequestDTO);
    RideDTO cancelRide(Long rideId);
    DriverDTO rateDriver(Long rideId, Integer rating);
    RiderDTO getMyProfile();
    Page<RideDTO> getAllMyRides(PageRequest pageRequest);

    Rider createNewRider(User user);

    Rider getCurrentRider();
}

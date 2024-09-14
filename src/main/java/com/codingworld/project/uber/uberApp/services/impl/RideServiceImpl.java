package com.codingworld.project.uber.uberApp.services.impl;

import com.codingworld.project.uber.uberApp.entities.Driver;
import com.codingworld.project.uber.uberApp.entities.Ride;
import com.codingworld.project.uber.uberApp.entities.RideRequest;
import com.codingworld.project.uber.uberApp.entities.Rider;
import com.codingworld.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.codingworld.project.uber.uberApp.entities.enums.RideStatus;
import com.codingworld.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.codingworld.project.uber.uberApp.repositories.RideRepository;
import com.codingworld.project.uber.uberApp.services.RideRequestService;
import com.codingworld.project.uber.uberApp.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId).
                orElseThrow(() -> new ResourceNotFoundException("Ride not found by id " + rideId));
    }


    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);

        Ride ride = modelMapper.map(rideRequest, Ride.class);
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setOtp(generateRandomOTP(5));
        ride.setId(null);

        rideRequestService.update(rideRequest);
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {
        return rideRepository.findByRider(rider, pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return rideRepository.findByDriver(driver, pageRequest);
    }

    private String generateRandomOTP(int length) {
        String digits = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(length);

        for(int i = 0; i < length; i++) {
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }

        return otp.toString();
    }
}

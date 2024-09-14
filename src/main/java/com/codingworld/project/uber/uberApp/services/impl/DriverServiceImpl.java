package com.codingworld.project.uber.uberApp.services.impl;

import com.codingworld.project.uber.uberApp.dto.DriverDTO;
import com.codingworld.project.uber.uberApp.dto.RideDTO;
import com.codingworld.project.uber.uberApp.dto.RiderDTO;
import com.codingworld.project.uber.uberApp.entities.Driver;
import com.codingworld.project.uber.uberApp.entities.Ride;
import com.codingworld.project.uber.uberApp.entities.RideRequest;
import com.codingworld.project.uber.uberApp.entities.User;
import com.codingworld.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.codingworld.project.uber.uberApp.entities.enums.RideStatus;
import com.codingworld.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.codingworld.project.uber.uberApp.repositories.DriverRepository;
import com.codingworld.project.uber.uberApp.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideDTO acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)) {
            throw new RuntimeException("Ride request cannot be accepted, status is" + rideRequest.getRideRequestStatus());
        }

        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.isAvailable()) {
            throw new RuntimeException("Driver cannot accept ride due to unavailability");
        }
        Driver savedDriver = updateDriverAvailability(currentDriver, false);
        Ride ride = rideService.createNewRide(rideRequest, savedDriver);

        return modelMapper.map(ride, RideDTO.class);
    }

    @Override
    public RideDTO cancelRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);

        Driver driver = getCurrentDriver();

        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver cannot start a ride as he has not accepted it earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride cannot be cancelled, invalid status : " + ride.getRideStatus());
        }

        rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        updateDriverAvailability(driver, true);

        return modelMapper.map(ride, RideDTO.class);
    }

    @Override
    public RideDTO startRide(Long rideId, String otp) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver cannot start a ride as he has not accepted it earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride is not confirmed by driver yet, status " + ride.getRideStatus());
        }

        if(!otp.equals(ride.getOtp())) {
            throw new RuntimeException("Otp is not valid, otp " + ride.getOtp());
        }

        ride.setStartedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ONGOING);

        paymentService.createNewPayment(savedRide);
        ratingService.createNewRating(savedRide);

        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    @Transactional
    public RideDTO endRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver cannot start a ride as he has not accepted it earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.ONGOING)) {
            throw new RuntimeException("Ride status is not ONGOING hence cannot be ended, invalid status : " + ride.getRideStatus());
        }

        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ENDED);
        updateDriverAvailability(driver, true);

        paymentService.processPayment(ride);

        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public RiderDTO rateRider(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();
        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver is not the owner of this Ride.");
        }

        if(!ride.getRideStatus().equals(RideStatus.ENDED)) {
            throw new RuntimeException("Ride status is not ended hence cannot start rating, status : " + ride.getRideStatus());
        }

        return ratingService.rateRider(ride, rating);
    }

    @Override
    public DriverDTO getMyProfile() {
        Driver currentDriver = getCurrentDriver();
        return modelMapper.map(currentDriver, DriverDTO.class);
    }

    @Override
    public Page<RideDTO> getAllMyRides(PageRequest pageRequest) {
        Driver currentDriver = getCurrentDriver();
        return rideService.getAllRidesOfDriver(currentDriver, pageRequest)
                .map(ride -> modelMapper.map(ride, RideDTO.class));
    }

    @Override
    public Driver getCurrentDriver() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Driver not associated with user with id "+ user.getId()));
    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {
        driver.setAvailable(available);
        return driverRepository.save(driver);
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}

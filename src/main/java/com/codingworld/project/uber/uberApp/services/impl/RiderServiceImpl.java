package com.codingworld.project.uber.uberApp.services.impl;

import com.codingworld.project.uber.uberApp.dto.DriverDTO;
import com.codingworld.project.uber.uberApp.dto.RideDTO;
import com.codingworld.project.uber.uberApp.dto.RideRequestDTO;
import com.codingworld.project.uber.uberApp.dto.RiderDTO;
import com.codingworld.project.uber.uberApp.entities.*;
import com.codingworld.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.codingworld.project.uber.uberApp.entities.enums.RideStatus;
import com.codingworld.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.codingworld.project.uber.uberApp.repositories.RideRequestRepository;
import com.codingworld.project.uber.uberApp.repositories.RiderRepository;
import com.codingworld.project.uber.uberApp.services.DriverService;
import com.codingworld.project.uber.uberApp.services.RatingService;
import com.codingworld.project.uber.uberApp.services.RideService;
import com.codingworld.project.uber.uberApp.services.RiderService;
import com.codingworld.project.uber.uberApp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;


    @Override
    @Transactional
    public RideRequestDTO requestRide(RideRequestDTO rideRequestDTO) {
        Rider rider = getCurrentRider();
        RideRequest rideRequest = modelMapper.map(rideRequestDTO, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);
        
        Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);
        
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        List<Driver> drivers = rideStrategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDriver(savedRideRequest);

//        TODO : Send notification to all the drivers about this ride request

        return modelMapper.map(savedRideRequest, RideRequestDTO.class);
    }

    @Override
    public RideDTO cancelRide(Long rideId) {
        Rider rider = getCurrentRider();
        Ride ride = rideService.getRideById(rideId);

        if(!rider.equals(ride.getRider())) {
            throw new RuntimeException("Rider does not own this ride with id: " + rideId);
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride cannot be cancelled, invalid status: " + ride.getRideStatus());
        }

        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        driverService.updateDriverAvailability(ride.getDriver(), true);
        return modelMapper.map(savedRide, RideDTO.class);
    }

    @Override
    public DriverDTO rateDriver(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Rider rider = getCurrentRider();
        if(!rider.equals(ride.getRider())) {
            throw new RuntimeException("Rider is not the owner of this Ride.");
        }

        if(!ride.getRideStatus().equals(RideStatus.ENDED)) {
            throw new RuntimeException("Ride status is not ended hence cannot start rating, status : " + ride.getRideStatus());
        }

        return ratingService.rateDriver(ride, rating);
    }

    @Override
    public RiderDTO getMyProfile() {
        Rider currentRider = getCurrentRider();
        return modelMapper.map(currentRider, RiderDTO.class);
    }

    @Override
    public Page<RideDTO> getAllMyRides(PageRequest pageRequest) {
        Rider currentRider = getCurrentRider();
        return rideService.getAllRidesOfRider(currentRider, pageRequest)
                .map(ride -> modelMapper.map(ride, RideDTO.class));
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .user(user)
                .rating(0.0).build();
        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return riderRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Rider not associated with user with id "+ user.getId()));
    }

}

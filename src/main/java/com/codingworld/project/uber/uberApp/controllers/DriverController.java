package com.codingworld.project.uber.uberApp.controllers;

import com.codingworld.project.uber.uberApp.dto.*;
import com.codingworld.project.uber.uberApp.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
@Secured("ROLE_DRIVER")
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/acceptRide/{rideRequestId}")
    public ResponseEntity<RideDTO> acceptRide(@PathVariable Long rideRequestId) {
        try {
            return new ResponseEntity<>(driverService.acceptRide(rideRequestId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/startRide/{rideRequestId}")
    public ResponseEntity<RideDTO> startRide(@PathVariable Long rideRequestId,
                                             @RequestBody RideStartDTO rideStartDTO) {
        try {
            return new ResponseEntity<>(driverService.startRide(rideRequestId, rideStartDTO.getOtp()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/endRide/{rideId}")
    public ResponseEntity<RideDTO> endRide(@PathVariable Long rideId) {
        try {
            return new ResponseEntity<>(driverService.endRide(rideId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDTO> cancelRide(@PathVariable Long rideId) {
        try {
            return new ResponseEntity<>(driverService.cancelRide(rideId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/rateRider")
    public ResponseEntity<RiderDTO> rateRider(@RequestBody RatingDTO ratingDTO) {
        try {
            return new ResponseEntity<>(driverService.rateRider(ratingDTO.getRideId(), ratingDTO.getRating()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<DriverDTO> getMyProfile() {
        try {
            return new ResponseEntity<>(driverService.getMyProfile(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDTO>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageOffset,
                                                       @RequestParam(defaultValue = "10", required = false) Integer pageSize)
    {
        try {
            PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "createdTime", "id"));
            return new ResponseEntity<>(driverService.getAllMyRides(pageRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

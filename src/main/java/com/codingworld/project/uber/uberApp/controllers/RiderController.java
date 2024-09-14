package com.codingworld.project.uber.uberApp.controllers;

import com.codingworld.project.uber.uberApp.dto.*;
import com.codingworld.project.uber.uberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
@Secured("ROLE_RIDER")
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/requestRide")
    public ResponseEntity<RideRequestDTO> requestRide(@RequestBody RideRequestDTO rideRequestDTO) {
        return new ResponseEntity<>(riderService.requestRide(rideRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDTO> cancelRide(@PathVariable Long rideId) {
        return new ResponseEntity<>(riderService.cancelRide(rideId), HttpStatus.OK);
    }

    @PostMapping("/rateDriver")
    public ResponseEntity<DriverDTO> rateDriver(@RequestBody RatingDTO ratingDTO) {
        return new ResponseEntity<>(riderService.rateDriver(ratingDTO.getRideId(), ratingDTO.getRating()), HttpStatus.OK);
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<RiderDTO> getMyProfile() {
        return new ResponseEntity<>(riderService.getMyProfile(), HttpStatus.OK);

    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDTO>> getAllMyRides(@RequestParam(defaultValue = "0") Integer pageOffset,
                                                       @RequestParam(defaultValue = "10", required = false) Integer pageSize)
    {
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "createdTime", "id"));
        return new ResponseEntity<>(riderService.getAllMyRides(pageRequest), HttpStatus.OK);

    }
}

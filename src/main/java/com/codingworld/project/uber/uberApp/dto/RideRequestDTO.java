package com.codingworld.project.uber.uberApp.dto;

import com.codingworld.project.uber.uberApp.entities.enums.PaymentMethod;
import com.codingworld.project.uber.uberApp.entities.enums.RideRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDTO {
    private Long id;
    private PointDTO pickupLocation;
    private PointDTO dropOffLocation;
    private LocalDateTime requestedTime;
    private RiderDTO rider;
    private Double fare;
    private PaymentMethod paymentMethod;
    private RideRequestStatus rideRequestStatus;
}

package com.codingworld.project.uber.uberApp.services;

import com.codingworld.project.uber.uberApp.entities.Payment;
import com.codingworld.project.uber.uberApp.entities.Ride;
import com.codingworld.project.uber.uberApp.entities.enums.PaymentStatus;

public interface PaymentService {

    void processPayment(Ride ride);

    Payment createNewPayment(Ride ride);

    void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus);
}

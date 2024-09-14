package com.codingworld.project.uber.uberApp.strategies.impl;

import com.codingworld.project.uber.uberApp.entities.Driver;
import com.codingworld.project.uber.uberApp.entities.Payment;
import com.codingworld.project.uber.uberApp.entities.enums.PaymentStatus;
import com.codingworld.project.uber.uberApp.entities.enums.TransactionMethod;
import com.codingworld.project.uber.uberApp.repositories.PaymentRepository;
import com.codingworld.project.uber.uberApp.services.WalletService;
import com.codingworld.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Rider -> 100
// Driver -> get 70, deduct 30 from wallet

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount() * PLATFORM_COMMISSION;
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission, null, payment.getRide(), TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}

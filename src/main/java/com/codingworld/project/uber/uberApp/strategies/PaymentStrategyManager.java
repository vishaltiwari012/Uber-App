package com.codingworld.project.uber.uberApp.strategies;

import com.codingworld.project.uber.uberApp.entities.enums.PaymentMethod;
import com.codingworld.project.uber.uberApp.strategies.impl.CashPaymentStrategy;
import com.codingworld.project.uber.uberApp.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {
    private final CashPaymentStrategy cashPaymentStrategy;
    private final WalletPaymentStrategy walletPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
        };
    }
}

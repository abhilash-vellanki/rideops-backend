package com.abhilash.rideops.strategies;

import com.abhilash.rideops.entities.enums.PaymentMethod;
import com.abhilash.rideops.strategies.impl.CashPaymentStrategy;
import com.abhilash.rideops.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentStrategyManager {
    private final CashPaymentStrategy cashPaymentStrategy;
    private final WalletPaymentStrategy walletPaymentStrategy;

    public PaymentStrategy getPaymentStrategy(PaymentMethod paymentMethod){
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if(paymentMethod == PaymentMethod.WALLET){
            log.debug("Selected wallet payment strategy");
            return walletPaymentStrategy;
        }
        else{
            log.debug("Selected cash payment strategy");
            return cashPaymentStrategy;
        }
    }
}

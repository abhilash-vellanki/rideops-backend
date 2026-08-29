package com.abhilash.rideops.strategies.impl;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.enums.PaymentStatus;
import com.abhilash.rideops.entities.enums.TransactionMethod;
import com.abhilash.rideops.repositories.PaymentRepository;
import com.abhilash.rideops.services.PlatformCommissionService;
import com.abhilash.rideops.services.WalletService;
import com.abhilash.rideops.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;
    private final PlatformCommissionService platformCommissionService;

    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        BigDecimal platformCommission = calculatePlatformCommission(payment.getAmount());
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission.doubleValue(), null
                , payment.getRide(), TransactionMethod.RIDE);
        platformCommissionService.recordCommission(payment, platformCommission);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
        log.debug("Cash payment strategy completed: paymentId={}, rideId={}",
                payment.getId(), payment.getRide().getId());

    }
}

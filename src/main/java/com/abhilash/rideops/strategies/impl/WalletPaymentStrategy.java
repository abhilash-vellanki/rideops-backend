package com.abhilash.rideops.strategies.impl;

import com.abhilash.rideops.entities.Driver;
import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.Rider;
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

//Rider has 500 in wallet,Driver has 500 in wallet
//Ride cost 100, commission=30
//Rider->500-100=400
//Driver->500+(100-30)70=570

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletPaymentStrategy implements PaymentStrategy {
    private final WalletService walletService;
    private final PaymentRepository paymentRepository;
    private final PlatformCommissionService platformCommissionService;

    @Override
    public void processPayment(Payment payment) {
        Driver driver=payment.getRide().getDriver();
        Rider rider=payment.getRide().getRider();
        BigDecimal platformCommission = calculatePlatformCommission(payment.getAmount());
        BigDecimal driversCut = payment.getAmount().subtract(platformCommission);
        walletService.deductMoneyFromWallet(rider.getUser(),payment.getAmount(),null
                ,payment.getRide(), TransactionMethod.RIDE);
        walletService.addMoneyToWallet(driver.getUser(),driversCut,null
                ,payment.getRide(), TransactionMethod.RIDE);
        platformCommissionService.recordCommission(payment, platformCommission);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
        log.debug("Wallet payment strategy completed: paymentId={}, rideId={}",
                payment.getId(), payment.getRide().getId());
    }
}

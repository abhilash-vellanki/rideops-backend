package com.abhilash.rideops.services.impl;


import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.enums.PaymentStatus;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.PaymentRepository;
import com.abhilash.rideops.services.PaymentService;
import com.abhilash.rideops.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;
    @Override
    @Transactional
    public void processPayment(Ride ride) {
        Payment payment=paymentRepository.findByRideForUpdate(ride).orElseThrow(
                ()->new ResourceNotFoundException("Payment not found: rideId="+ride.getId())
        );
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeConflictException(
                    "Payment cannot be processed while status is " + payment.getPaymentStatus()
                            + ": paymentId=" + payment.getId()
            );
        }
        log.debug("Payment processing started: paymentId={}, rideId={}, method={}",
                payment.getId(), ride.getId(), ride.getPaymentMethod());
        paymentStrategyManager.getPaymentStrategy(ride.getPaymentMethod()).processPayment(payment);
        log.info("Payment processing completed: paymentId={}, rideId={}", payment.getId(), ride.getId());
    }

    @Override
    public Payment createNewPayment(Ride ride) {
        Payment payment=Payment.builder()
                .paymentMethod(ride.getPaymentMethod())
                .amount(ride.getFare())
                .ride(ride)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created: paymentId={}, rideId={}, method={}",
                savedPayment.getId(), ride.getId(), savedPayment.getPaymentMethod());
        return savedPayment;
    }


    @Override
    public void updatePaymentStatus(Payment payment, PaymentStatus status) {
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
        log.info("Payment status updated: paymentId={}, status={}", payment.getId(), status);
    }
}

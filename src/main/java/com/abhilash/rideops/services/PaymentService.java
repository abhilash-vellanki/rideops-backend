package com.abhilash.rideops.services;

import com.abhilash.rideops.entities.Payment;
import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.enums.PaymentStatus;

public interface PaymentService {
    void processPayment(Ride ride);
    Payment createNewPayment(Ride ride);
    void updatePaymentStatus(Payment payment, PaymentStatus status);
}
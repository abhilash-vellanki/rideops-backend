package com.abhilash.rideops.services;


import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.enums.TransactionMethod;

import java.math.BigDecimal;

public interface WalletService {
    Wallet addMoneyToWallet(User user, BigDecimal amount, String transactionId, Ride ride, TransactionMethod transactionMethod);
    Wallet findWalletById(Long walletId);
    Wallet createNewWallet(User user);
    Wallet findByUser(User user);
    Wallet deductMoneyFromWallet(User user, BigDecimal amount, String transactionId, Ride ride, TransactionMethod transactionMethod);
    Wallet deductCommissionFromDriverWallet(User user, BigDecimal amount, Ride ride);
}

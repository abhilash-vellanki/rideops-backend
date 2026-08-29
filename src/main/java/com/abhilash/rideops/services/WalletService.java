package com.abhilash.rideops.services;


import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.enums.TransactionMethod;

public interface WalletService {
    Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);
    void withdrawAllMyMoneyFromWallet();
    Wallet findWalletById(Long walletId);
    Wallet createNewWallet(User user);
    Wallet findByUser(User user);
    Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);
}
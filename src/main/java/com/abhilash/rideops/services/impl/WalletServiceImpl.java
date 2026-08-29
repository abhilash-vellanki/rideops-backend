package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.WalletTransactions;
import com.abhilash.rideops.entities.enums.TransactionMethod;
import com.abhilash.rideops.entities.enums.TransactionType;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.repositories.WalletRepository;
import com.abhilash.rideops.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final ModelMapper mapper;
    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
        Wallet wallet=findByUser(user);
        wallet.setBalance(wallet.getBalance()+amount);
        WalletTransactions walletTransactions=WalletTransactions.builder()
                .transactionId(transactionId)
                .transactionMethod(transactionMethod)
                .transactionType(TransactionType.CREDIT)
                .wallet(wallet)
                .ride(ride)
                .amount(amount)
                .build();
        // walletTransactionService.createNewWalletTransaction(walletTransactions);
        wallet.getTransactions().add(walletTransactions);
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet credited: walletId={}, method={}, rideId={}",
                savedWallet.getId(), transactionMethod, ride == null ? null : ride.getId());
        return savedWallet;
    }

    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(()->new ResourceNotFoundException("Wallet not found: walletId="+walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet=new Wallet();
        wallet.setUser(user);
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created: walletId={}, userId={}", savedWallet.getId(), user.getId());
        return savedWallet;
    }

    @Override
    public Wallet findByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(()->new ResourceNotFoundException(
                        "No wallet is associated with userId="+user.getId()));
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
        Wallet wallet=findByUser(user);
        wallet.setBalance(wallet.getBalance()-amount);
        WalletTransactions walletTransactions=WalletTransactions.builder()
                .transactionId(transactionId)
                .transactionMethod(transactionMethod)
                .transactionType(TransactionType.DEBIT)
                .wallet(wallet)
                .ride(ride)
                .amount(amount)
                .build();

        wallet.getTransactions().add(walletTransactions);
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet debited: walletId={}, method={}, rideId={}",
                savedWallet.getId(), transactionMethod, ride == null ? null : ride.getId());
        return savedWallet;
    }
}

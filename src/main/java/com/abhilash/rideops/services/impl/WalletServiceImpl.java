package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.entities.Ride;
import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.WalletTransactions;
import com.abhilash.rideops.entities.enums.TransactionMethod;
import com.abhilash.rideops.entities.enums.TransactionType;
import com.abhilash.rideops.exceptions.ResourceNotFoundException;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.WalletTransactionRepository;
import com.abhilash.rideops.repositories.WalletRepository;
import com.abhilash.rideops.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, BigDecimal amount, String transactionId, Ride ride,
                                   TransactionMethod transactionMethod) {
        validatePositiveAmount(amount);
        Wallet wallet = findByUser(user);
        wallet.setBalance(wallet.getBalance().add(amount));
        recordTransaction(wallet, amount, transactionId, ride, transactionMethod, TransactionType.CREDIT);
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet credited: walletId={}, method={}, rideId={}",
                savedWallet.getId(), transactionMethod, ride == null ? null : ride.getId());
        return savedWallet;
    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(()->new ResourceNotFoundException("Wallet not found: walletId="+walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = new Wallet();
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
    public Wallet deductMoneyFromWallet(User user, BigDecimal amount, String transactionId, Ride ride,
                                        TransactionMethod transactionMethod) {
        validatePositiveAmount(amount);
        Wallet wallet = findByUser(user);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeConflictException("Insufficient wallet balance for userId=" + user.getId());
        }
        return debitWallet(wallet, amount, transactionId, ride, transactionMethod);
    }

    @Override
    @Transactional
    public Wallet deductCommissionFromDriverWallet(User user, BigDecimal amount, Ride ride) {
        validatePositiveAmount(amount);
        Wallet wallet = findByUser(user);
        return debitWallet(wallet, amount, null, ride, TransactionMethod.RIDE);
    }

    private Wallet debitWallet(Wallet wallet, BigDecimal amount, String transactionId, Ride ride,
                               TransactionMethod transactionMethod) {
        wallet.setBalance(wallet.getBalance().subtract(amount));
        recordTransaction(wallet, amount, transactionId, ride, transactionMethod, TransactionType.DEBIT);
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet debited: walletId={}, method={}, rideId={}",
                savedWallet.getId(), transactionMethod, ride == null ? null : ride.getId());
        return savedWallet;
    }

    private void recordTransaction(Wallet wallet, BigDecimal amount, String transactionId, Ride ride,
                                   TransactionMethod transactionMethod, TransactionType transactionType) {
        WalletTransactions walletTransaction = WalletTransactions.builder()
                .transactionId(transactionId)
                .transactionMethod(transactionMethod)
                .transactionType(transactionType)
                .wallet(wallet)
                .ride(ride)
                .amount(amount)
                .build();
        walletTransactionRepository.save(walletTransaction);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Wallet amount must be greater than zero");
        }
    }
}

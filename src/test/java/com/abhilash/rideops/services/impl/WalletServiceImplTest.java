package com.abhilash.rideops.services.impl;

import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.Wallet;
import com.abhilash.rideops.entities.enums.TransactionMethod;
import com.abhilash.rideops.exceptions.RuntimeConflictException;
import com.abhilash.rideops.repositories.WalletRepository;
import com.abhilash.rideops.repositories.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletServiceImplTest {
    private final WalletRepository walletRepository = mock(WalletRepository.class);
    private final WalletTransactionRepository transactionRepository = mock(WalletTransactionRepository.class);
    private final WalletServiceImpl walletService = new WalletServiceImpl(walletRepository, transactionRepository);
    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(7L);
        wallet = new Wallet();
        wallet.setId(11L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("100.00"));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);
    }

    @Test
    void debitsWalletAndRecordsTransaction() {
        walletService.deductMoneyFromWallet(
                user, new BigDecimal("30.00"), "txn-1", null, TransactionMethod.BANKING);

        assertEquals(new BigDecimal("70.00"), wallet.getBalance());
        verify(transactionRepository).save(any());
    }

    @Test
    void rejectsDebitWhenBalanceIsInsufficient() {
        assertThrows(RuntimeConflictException.class, () -> walletService.deductMoneyFromWallet(
                user, new BigDecimal("100.01"), "txn-2", null, TransactionMethod.BANKING));

        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        verify(transactionRepository, never()).save(any());
    }
}

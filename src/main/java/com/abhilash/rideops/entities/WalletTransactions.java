package com.abhilash.rideops.entities;

import com.abhilash.rideops.entities.enums.TransactionMethod;
import com.abhilash.rideops.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = {
        @Index(name="idx_wallet_transaction_wallet",columnList = "wallet_id"),
        @Index(name="idx_wallet_transaction_ride",columnList = "ride_id")
})
public class WalletTransactions {
    @Id
    @SequenceGenerator(name = "wallet_transactions_id_generator", sequenceName = "wallet_transactions_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_transactions_id_generator")
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionMethod transactionMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ride ride;

    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Wallet wallet;
    @CreationTimestamp
    private LocalDateTime timeStamp;

}

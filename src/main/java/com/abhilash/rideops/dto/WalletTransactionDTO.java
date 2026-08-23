package com.abhilash.rideops.dto;
import com.abhilash.rideops.entities.enums.TransactionMethod;
import com.abhilash.rideops.entities.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransactionDTO {
    private Long id;
    @NotNull
    @Positive
    private Double amount;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private TransactionMethod transactionMethod;
    @Positive
    private Long rideId;
    private String transactionId;
    @Positive
    private Long walletId;
    private LocalDateTime timeStamp;
}

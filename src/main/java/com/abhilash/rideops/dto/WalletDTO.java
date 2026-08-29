package com.abhilash.rideops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private Long id;
    private UserDTO user;
    private BigDecimal balance;
    private List<WalletTransactionDTO> transactions;
}

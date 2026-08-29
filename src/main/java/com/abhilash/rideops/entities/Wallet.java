package com.abhilash.rideops.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @SequenceGenerator(name = "wallet_id_generator", sequenceName = "wallet_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_generator")
    private Long id;
    @Version
    private Long version;
    @OneToOne(fetch = FetchType.LAZY,optional = false)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WalletTransactions> transactions = new ArrayList<>();

}

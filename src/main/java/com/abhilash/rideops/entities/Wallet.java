package com.abhilash.rideops.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @OneToOne(fetch = FetchType.LAZY,optional = false)
    private User user;

    private Double balance=0.0;

    @OneToMany(mappedBy = "wallet",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WalletTransactions> transactions;

}

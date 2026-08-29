package com.abhilash.rideops.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Rider {
    @Id
    @SequenceGenerator(name = "rider_id_generator", sequenceName = "rider_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rider_id_generator")
    private Long id;
    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false, unique = true)
    private User user;
    @Column(nullable = false)
    private Double rating;

}

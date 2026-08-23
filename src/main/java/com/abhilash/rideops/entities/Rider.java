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

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    private Double rating;

}

package com.abhilash.rideops.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = {
        @Index(name="idx_rating_rider",columnList = "rider_id"),
        @Index(name="idx_rating_driver",columnList = "driver_id")
})
public class Rating {
    @Id
    @SequenceGenerator(name = "rating_id_generator", sequenceName = "rating_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rating_id_generator")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    private Integer driverRating;
    private Integer riderRating;
}

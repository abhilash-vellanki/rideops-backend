package com.abhilash.rideops.entities;

import com.abhilash.rideops.entities.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Driver {
    @Id
    @SequenceGenerator(name = "driver_id_generator", sequenceName = "driver_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_id_generator")
    private Long id;
    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false, unique = true)
    private User user;
    @Column(nullable = false)
    private Double rating;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private DriverStatus status = DriverStatus.OFFLINE;
    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point currentLocation;
    @Column(name = "vehicle_id", nullable = false, unique = true)
    private String vehicleId;
}

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
@Table(indexes = {
        @Index(name="idx_driver_vehicle_id",columnList = "vehicleId")
})
public class Driver {
    @Id
    @SequenceGenerator(name = "driver_id_generator", sequenceName = "driver_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_id_generator")
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    private Double rating;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DriverStatus status = DriverStatus.OFFLINE;
    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point currentLocation;
    @Column(unique = true)
    private String vehicleId;
}

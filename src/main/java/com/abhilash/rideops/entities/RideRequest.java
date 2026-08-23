package com.abhilash.rideops.entities;

import com.abhilash.rideops.entities.enums.PaymentMethod;
import com.abhilash.rideops.entities.enums.RideRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name="idx_ride_req_rider",columnList = "rider_id")
})
public class RideRequest {
    @Id
    @SequenceGenerator(name = "ride_request_id_generator", sequenceName = "ride_request_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ride_request_id_generator")
    private Long id;
    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point pickupLocation;
    @Column(columnDefinition = "Geometry(Point,4326)")
    private Point dropOffLocation;
    @CreationTimestamp
    private LocalDateTime requestedTime;
    @ManyToOne(fetch= FetchType.LAZY)
    private Rider rider;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private RideRequestStatus rideRequestStatus;

    private Double fare;

}

package com.abhilash.rideops.utils;


import com.abhilash.rideops.dto.PointDTO;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {
    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    private GeometryUtil() {
    }

    public static Point createPoint(PointDTO pointDTO) {
        if (pointDTO == null) {
            throw new IllegalArgumentException("Point must not be null");
        }

        Double[] coordinates = pointDTO.getCoordinates();
        if (coordinates == null) {
            throw new IllegalArgumentException("Point coordinates must not be null");
        }
        if (coordinates.length != 2) {
            throw new IllegalArgumentException("Point coordinates must contain longitude and latitude");
        }

        Double longitude = coordinates[0];
        Double latitude = coordinates[1];
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("Point coordinates must not contain null values");
        }
        if (!Double.isFinite(longitude) || !Double.isFinite(latitude)) {
            throw new IllegalArgumentException("Point coordinates must be finite");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }

        Coordinate coordinate = new Coordinate(longitude, latitude);
        return GEOMETRY_FACTORY.createPoint(coordinate);
    }

    public static PointDTO createPointDTO(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null");
        }
        if (point.isEmpty()) {
            throw new IllegalArgumentException("Point must not be empty");
        }

        return new PointDTO(new Double[]{point.getX(), point.getY()});
    }
}

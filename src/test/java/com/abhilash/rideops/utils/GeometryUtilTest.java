package com.abhilash.rideops.utils;

import com.abhilash.rideops.dto.PointDTO;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeometryUtilTest {

    @Test
    void createsSrid4326PointFromLongitudeAndLatitude() {
        Point point = GeometryUtil.createPoint(new PointDTO(new Double[]{77.5946, 12.9716}));

        assertAll(
                () -> assertEquals(4326, point.getSRID()),
                () -> assertEquals(77.5946, point.getX()),
                () -> assertEquals(12.9716, point.getY())
        );
    }

    @Test
    void rejectsMalformedCoordinates() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> GeometryUtil.createPoint(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GeometryUtil.createPoint(new PointDTO(null))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GeometryUtil.createPoint(new PointDTO(new Double[]{77.0}))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GeometryUtil.createPoint(new PointDTO(new Double[]{null, 12.0}))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GeometryUtil.createPoint(new PointDTO(new Double[]{Double.NaN, 12.0}))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GeometryUtil.createPoint(new PointDTO(new Double[]{181.0, 12.0}))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GeometryUtil.createPoint(new PointDTO(new Double[]{77.0, -91.0})))
        );
    }
}

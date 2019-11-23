package model;

import lombok.Value;
import model.overpass.Node;

@Value(staticConstructor = "of")
public class GeoPoint {
    private double lat;
    private double lon;

    public static GeoPoint from(final Node node) {
        return GeoPoint.of(node.getLat(), node.getLon());
    }
}

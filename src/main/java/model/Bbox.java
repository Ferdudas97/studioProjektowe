package model;

import lombok.Builder;
import lombok.Value;

@Builder
//@AllArgsConstructor
@Value
public class Bbox {
    private double southernLat;
    private double westernLon;
    private double northernLat;
    private double easternLon;

    public double width() {
        return Math.abs(westernLon - easternLon);
    }

    public double height() {
        return Math.abs(northernLat - southernLat);
    }

}

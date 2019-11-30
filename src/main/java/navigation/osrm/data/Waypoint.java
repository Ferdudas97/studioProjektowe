package navigation.osrm.data;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class Waypoint {
    @SerializedName("hint")
    private String hint;

    @SerializedName("distance")
    private Double distance;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private List<Double> locationCoordinates;
}

package navigation.osrm.data;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class Route {
    @SerializedName("geometry")
    private String geometry;

    @SerializedName("legs")
    private List<StepsContainer> legs;

    @SerializedName("distance")
    private Double distance;

    @SerializedName("duration")
    private Double durationSeconds;

    @SerializedName("weight")
    private Double weight;
}

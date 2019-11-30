package navigation.osrm.data;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class Maneuver {
    @SerializedName("location")
    private List<Double> locationCoordinates;
}

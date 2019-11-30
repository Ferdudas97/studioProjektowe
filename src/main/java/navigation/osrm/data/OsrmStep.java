package navigation.osrm.data;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class OsrmStep {
    @SerializedName("duration")
    private Double durationSeconds;

    @SerializedName("maneuver")
    private Maneuver maneuver;

    @SerializedName("distance")
    private Double distance;
}

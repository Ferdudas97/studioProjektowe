package navigation.osrm.data;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.List;

@Value
public class NavResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("routes")
    private List<Route> routes;

    @SerializedName("waypoints")
    private List<Waypoint> waypoints;
}

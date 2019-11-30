package model.overpass;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.Map;

@Value
public class Node {
    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private long id;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;

    @SerializedName("tags")
    private Map<String, String> tags;
}

package model.overpass;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class OverpassQueryResult {
    @SerializedName("elements")
    private List<Node> elements = new ArrayList<>();
}

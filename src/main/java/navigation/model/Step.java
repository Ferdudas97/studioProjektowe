package navigation.model;

import lombok.Value;

@Value
public class Step {
    private Double longitude;
    private Double latitude;
    private Double deltaSeconds;
    private Double distance;
}

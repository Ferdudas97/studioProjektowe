package model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Profile {

    private final int daysToSpend;
    private final int peopleCount;
    private final PreferenceWeight<Integer> publicTransport;
    private final PreferenceWeight<Boolean> usingCar;
    private final PreferenceWeight<Integer> eatingInRestaurants;

}

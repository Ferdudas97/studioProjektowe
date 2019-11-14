package model;

import lombok.Value;

@Value(staticConstructor = "of")
public class PreferenceWeight {
    private final double value;
    private final String name;

    public double relativePreference(final PreferenceWeight other) {
        return value / other.value;
    }
}

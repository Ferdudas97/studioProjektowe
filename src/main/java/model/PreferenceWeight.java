package model;

import lombok.Value;

@Value(staticConstructor = "of")
public class PreferenceWeight<T> {
    private final T value;
}

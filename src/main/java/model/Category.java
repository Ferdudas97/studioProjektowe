package model;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class Category<CAT> {

    private final Map<CAT, PreferenceWeight> categories;
    private final int prefereneceBonus;
    private final int averageTime;
}

package model;

import lombok.Builder;
import lombok.Value;
import lombok.val;
import util.ahp.data.PairwiseComparisonMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class Profile {

    private final int daysToSpend;
    private final int peopleCount;
    private final PreferenceWeight publicTransport;
    private final PreferenceWeight usingCar;
    private final PreferenceWeight eatingInRestaurants;
    private final PreferenceWeight culture;
    private final PreferenceWeight religion;
    private final PreferenceWeight historic;
    private final PreferenceWeight xfg;


    public PairwiseComparisonMatrix toMatrix() {
        val preferences = Stream.of(publicTransport, usingCar, eatingInRestaurants)
                .collect(Collectors.toList());
        val matrix = new PairwiseComparisonMatrix();
        toPairs(preferences)
                .forEach(p -> matrix.addNewComparisionBetweenCategories(p.getFirst().getName(),
                        p.getSecond().getName(),
                        p.getFirst().relativePreference(p.getSecond()))
                );
        return matrix;
    }

    private List<Pair<PreferenceWeight, PreferenceWeight>> toPairs(List<PreferenceWeight> preferences) {
        val list = new ArrayList<Pair<PreferenceWeight, PreferenceWeight>>();
        for (int i = 0; i < preferences.size(); i++)
            for (int j = i + 1; j < preferences.size(); j++) {
                list.add(Pair.of(preferences.get(i), preferences.get(j)));
            }
        return list;
    }


}

package model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class Profile {
    @Singular("category")
    private final List<Pair<Category, Integer>> categoriesToVisit;
    private final double avgSpendTime;

    public void reducePriority(final Category category) {
        categoriesToVisit.stream().filter(pair -> pair.getFirst() == category)
                .findFirst()
                .ifPresent(p -> p.setSecond(p.getSecond() - 1));
    }

    public List<Category> getCategories() {
        return categoriesToVisit.stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    public List<Integer> getWeights() {
        return categoriesToVisit.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());
    }

}

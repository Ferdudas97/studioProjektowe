package model;

import lombok.Value;
import lombok.val;
import model.overpass.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Value(staticConstructor = "of")
public class Person {
    private final Profile profile;
    private final List<Node> road = new ArrayList<>();


    public void visit(final Node node) {
        road.add(node);
    }

    public long getNumberOfPlacesToVisit() {
        return Math.round(24 / profile.getAvgSpendTime());
    }

    public GeoPoint getLastPosition() {
        val lastNode = road.get(road.size() - 1);
        return GeoPoint.of(lastNode.getLat(), lastNode.getLon());
    }

    public Category getCategoryToGo() {
        val sum = profile.getWeights().stream()
                .mapToInt(e -> e)
                .sum();
        val random = new Random();
        val index = random.nextInt(sum);
        return getCategory(profile.getCategoriesToVisit(), index);
    }

    private Category getCategory(final List<Pair<Category, Integer>> list, final int index) {
        if (list.isEmpty()) return null;
        val pair = list.get(0);
        return pair.getSecond() >= index ? pair.getFirst() : getCategory(list.subList(0, list.size() - 1), index - pair.getSecond());

    }
}

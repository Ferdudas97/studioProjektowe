package model;

import lombok.Value;
import lombok.val;
import model.overpass.Node;

import java.util.*;

@Value(staticConstructor = "of")
public class Person {
    private final Profile profile;
    private final List<Node> road = new ArrayList<>();


    public void visit(final Node node) {
        road.add(node);
    }

    public long getNumberOfPlacesToVisit() {
        return Math.round(30 / profile.getAvgSpendTime());
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

    public void printRoadCsv() {
        //return Arrays.toString(road.toArray());
        for (Node n:road) {
            System.out.println(nodeToCsvFormat(n));
        }
    }

    public String getRoadCsv() {
        StringBuilder out = new StringBuilder();
        for (Node n : road) {
            out.append(nodeToCsvFormat(n));
            if (road.indexOf(n) != road.size() - 1) {
                out.append("\n");
            }
        }
        return out.toString();
    }

    private String nodeToCsvFormat(Node n) {
        return (!n.getType().equals("navigationNode") ? "point" : "road") +";"+n.getLat()+";"+n.getLon()+";"+
                String.format("%02d", (int)Double.parseDouble(n.getTags().get("time_visited"))/3600)+":"+
                String.format("%02d", (int)(Double.parseDouble(n.getTags().get("time_visited"))%3600)/60)+":"+
                String.format("%02d", (int)Double.parseDouble(n.getTags().get("time_visited"))%60) + ";" +
                getNodeTagsInKeyValueFormat(n.getTags());
    }

    private String getNodeTagsInKeyValueFormat(Map<String, String> tags) {
        StringBuilder res = new StringBuilder();
        for (val tag : tags.entrySet()) {
            res.append(tag.getKey()).append("=").append(tag.getValue()).append(",");
        }
        return res.substring(0, res.length() - 1);
    }
}

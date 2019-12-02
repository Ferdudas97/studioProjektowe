package algo;

import hu.supercluster.overpasser.library.query.OverpassFilterQuery;
import hu.supercluster.overpasser.library.query.OverpassQuery;
import lombok.AllArgsConstructor;
import lombok.val;
import model.Category;
import model.GeoPoint;
import model.Person;
import model.overpass.Bbox;
import model.overpass.Node;
import navigation.NavigationService;
import navigation.model.Step;
import remote.QueryService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.supercluster.overpasser.library.output.OutputFormat.JSON;

@AllArgsConstructor(staticName = "of")
public class Simulation {
    private QueryService queryService;
    private NavigationService navigationService;
    private Bbox simulationArea;
    private static final long earthRadius = 6371; //earth radius in kilometers

    public void simulate(final Person person) {
        setHotel(person);
        val places = getInterestedInPlaces(person);
        Stream.iterate(0, i -> i + 1)
                .limit(person.getNumberOfPlacesToVisit())
                .forEach(p -> visitNewPlace(person, places));
        addNavigationPointsToRoad(person);
        for (Node n:person.getRoad()) {
            if (!n.getType().equals("navigationNode")){
                n.getTags().put("duration", Integer.toString(
                        (int)(3000*person.getProfile().getAvgSpendTime() +
                                new Random().nextInt(1200)*person.getProfile().getAvgSpendTime())));
            }
        }
        int time = 8*3600;
        for (Node n:person.getRoad()) {
            if (!n.getType().equals("navigationNode")){
                n.getTags().put("time_visited", String.valueOf(time));
                time = time + Integer.parseInt(n.getTags().get("duration"));
            }
        }
    }

    private void addNavigationPointsToRoad(Person person) {
        if (person.getRoad().size() > 1) {
            val navRoad = new ArrayList<Node>();
            navRoad.add(person.getRoad().get(0));
            for (int i = 0; i < person.getRoad().size() - 1; i++) {
                val source = person.getRoad().get(i);
                val destination = person.getRoad().get(i + 1);
                val steps = navigationService.getStepsInRoute(source.getLat(), source.getLon(), destination.getLat(), destination.getLon());
                navRoad.addAll(steps.stream()
                        .map(this::convertStepToNode)
                        .collect(Collectors.toList()));
                navRoad.add(destination);
            }
            replacePersonRoadWithNavRoad(person, navRoad);
        }
    }

    private void replacePersonRoadWithNavRoad(Person person, ArrayList<Node> roadWithNav) {
        person.getRoad().clear();
        person.getRoad().addAll(roadWithNav);
    }

    private Node convertStepToNode(final Step step) {
        val tags = new HashMap<String, String>() {{
            put("duration", step.getDeltaSeconds().toString());
            put("distance", step.getDistance().toString());
        }};
        return new Node("navigationNode", 0, step.getLatitude(), step.getLongitude(), tags);
    }

    private void setHotel(final Person person) {
        val hotels = queryService.execute(simulationArea, this::getHotel, 2);
        val randomIndex = new Random().nextInt(hotels.size());
        person.visit(hotels.get(randomIndex));
    }


    private void visitNewPlace(final Person person, final List<Node> places) {
        val nearestPlaces = getNearestPlaces(places, person.getLastPosition(), 1);
        val category = person.getCategoryToGo();
        val nextPlace = placeToGo(category, nearestPlaces);
        person.visit(nextPlace);
    }

    private List<Node> getNearestPlaces(final List<Node> places, final GeoPoint lastPosition, final double radius) {
        return places.stream()
                .filter(e -> isInRadius(GeoPoint.from(e), lastPosition, radius))
                .collect(Collectors.toList());
    }

    private Node placeToGo(final Category category, final List<Node> places) {
        val placesOfSpecifiedCategory = places.stream()
                .filter(place -> place.getTags().containsKey(category.getTag()))
                .filter(place -> place.getTags().get(category.getTag()).equals(category.getValue()))
                .collect(Collectors.toList());
        val randomIndex = new Random().nextInt(placesOfSpecifiedCategory.size());
        return placesOfSpecifiedCategory.get(randomIndex);
    }


    private List<Node> getInterestedInPlaces(final Person person) {
        return person.getProfile()
                .getCategories()
                .stream()
                .map(category -> queryService.execute(simulationArea, () -> createQuery(category), 2))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .peek(System.out::println)
                .collect(Collectors.toList());
    }


    private OverpassFilterQuery getHotel() {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .node()
                .tag("tourism", "hotel");
    }

    private OverpassFilterQuery createQuery(final Category category) {
        if (category.getValue() == null) {
            return createQuery(category.getTag());
        }
        return createQuery(category.getTag(), category.getValue());
    }

    private OverpassFilterQuery createQuery(final String tag) {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .node()
                .amenity(tag);
    }

    private OverpassFilterQuery createQuery(final String tag, final String value) {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .node()
                .tag(tag, value);
    }

    private boolean isInRadius(final GeoPoint pointA, final GeoPoint pointB, final double radius) {
        // algorithm from https://www.movable-type.co.uk/scripts/latlong.html
        val apiLatInRadians = Math.toRadians(pointA.getLat());
        val apiLongInRadians = Math.toRadians(pointB.getLon());
        val latInRadians = Math.toRadians(pointB.getLat());
        val longInRadians = Math.toRadians(pointB.getLon());
        val latDelta = apiLatInRadians - latInRadians;
        val longDelta = apiLongInRadians - longInRadians;
        val a = Math.sin(latDelta / 2) * Math.sin(latDelta / 2) +
                Math.cos(apiLatInRadians) * Math.cos(latInRadians) *
                        Math.sin(longDelta / 2) * Math.sin(longDelta / 2);
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        val computed = earthRadius * c;

        return radius >= computed;

    }
}

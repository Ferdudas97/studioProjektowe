package algo;

import exception.InvalidProfileException;
import hu.supercluster.overpasser.library.query.OverpassFilterQuery;
import hu.supercluster.overpasser.library.query.OverpassQuery;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
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

    public void simulate(final Person person) throws InvalidProfileException {
        setHotel(person);
        val places = getInterestedInPlaces(person);
        if (places.isEmpty()) {
            throw new InvalidProfileException("No places were found for such profile. Please specify another profile.");
        }
        Stream.iterate(0, i -> i + 1)
                .limit(person.getNumberOfPlacesToVisit())
                .forEach(p -> visitNewPlace(person, places));
        addNavigationPointsToRoad(person);
        person.getRoad().get(0).getTags().put("duration","0");
        for (Node n:person.getRoad()) {
            if (!n.getType().equals("navigationNode") && !n.getTags().containsKey("duration")){
                n.getTags().put("duration", Double.toString(
                        (int)(3000*person.getProfile().getAvgSpendTime() +
                                new Random().nextInt(1200)*person.getProfile().getAvgSpendTime())));
            }
        }
        double time = 8*3600;
        for (int i=0;i<person.getRoad().size();i++) {
            if (time < 22*3600 && person.getRoad().get(i).getTags().containsKey("duration")){
                time = time + Double.parseDouble(person.getRoad().get(i).getTags().get("duration"));
                person.getRoad().get(i).getTags().put("time_visited", String.valueOf(time));
            }
            else{
                person.getRoad().remove(i);
                i--;
            }
        }
        val source = person.getRoad().get(person.getRoad().size()-1);
        val destination = new Node(person.getRoad().get(0).getType(),
                person.getRoad().get(0).getId(),
                person.getRoad().get(0).getLat(),
                person.getRoad().get(0).getLon(),
                new HashMap<>(),
                new HashMap<>(person.getRoad().get(0).getTags()));
        destination.getTags().remove("time_visited");
        val steps = navigationService.getStepsInRoute(source.getLat(), source.getLon(), destination.getLat(), destination.getLon());
        val navRoad = steps.stream()
                .map(this::convertStepToNode)
                .collect(Collectors.toCollection(ArrayList::new));
        navRoad.add(destination);
        for (Node n:navRoad) {
            if (!n.getTags().containsKey("time_visited")){
                time = time + Double.parseDouble(n.getTags().get("duration"));
                n.getTags().put("time_visited", String.valueOf(time));
            }
        }

        person.getRoad().addAll(navRoad);
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
        return new Node("navigationNode", 0, step.getLatitude(), step.getLongitude(), new HashMap<>(), tags);
    }

    private void setHotel(final Person person) {
        val hotels = queryService.execute(simulationArea, this::getHotel, 2);
        val randomIndex = new Random().nextInt(hotels.size());
        person.visit(hotels.get(randomIndex));
    }


    private void visitNewPlace(final Person person, final List<Node> places) {
        var radius = 3;
        var nearestPlaces = getNearestPlaces(places, person.getLastPosition(), radius);
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
                .filter(place -> {
                    if (category.getValue() == null) {
                        return place.getTags().containsKey("amenity");
                    }
                    return place.getTags().containsKey(category.getTag());
                })
                .filter(place -> {
                    if (category.getValue() == null) {
                        return place.getTags().get("amenity").equals(category.getTag());
                    }
                    return place.getTags().get(category.getTag()).equals(category.getValue());
                })
                .collect(Collectors.toList());
        if (placesOfSpecifiedCategory.isEmpty()) {
            return places.get(new Random().nextInt(places.size()));
        }
        val randomIndex = new Random().nextInt(placesOfSpecifiedCategory.size());
        return placesOfSpecifiedCategory.get(randomIndex);
    }


    private List<Node> getInterestedInPlaces(final Person person) {
        return person.getProfile()
                .getCategories()
                .stream()
                .map(category -> queryService.execute(simulationArea, bbox -> createQuery(category, bbox), 2))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .peek(System.out::println)
                .collect(Collectors.toList());
    }


    private OverpassFilterQuery getHotel(Bbox bbox) {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .node()
                .tag("tourism", "hotel")
                .boundingBox(bbox.getSouthernLat(), bbox.getWesternLon(), bbox.getNorthernLat(), bbox.getEasternLon());
    }

    private OverpassFilterQuery createQuery(final Category category, Bbox bbox) {
        if (category.getValue() == null) {
            return createQuery(category.getTag(), bbox);
        }
        return createQuery(category.getTag(), category.getValue(), bbox);
    }

    private OverpassFilterQuery createQuery(final String tag, Bbox bbox) {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .node()
                .amenity(tag)
                .boundingBox(bbox.getSouthernLat(), bbox.getWesternLon(), bbox.getNorthernLat(), bbox.getEasternLon());
    }

    private OverpassFilterQuery createQuery(final String tag, final String value, Bbox bbox) {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .node()
                .tag(tag, value)
                .boundingBox(bbox.getSouthernLat(), bbox.getWesternLon(), bbox.getNorthernLat(), bbox.getEasternLon())
                .prepareNext()
                .rel()
                .tag(tag,value)
                .boundingBox(bbox.getSouthernLat(), bbox.getWesternLon(), bbox.getNorthernLat(), bbox.getEasternLon())
                .prepareNext()
                .way()
                .tag(tag, value)
                .boundingBox(bbox.getSouthernLat(), bbox.getWesternLon(), bbox.getNorthernLat(), bbox.getEasternLon());
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

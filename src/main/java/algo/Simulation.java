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
import remote.QueryService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.supercluster.overpasser.library.output.OutputFormat.JSON;

@AllArgsConstructor(staticName = "of")
public class Simulation {
    private QueryService queryService;
    private Bbox simulationArea;
    private static final long earthRadius = 6371; //earth radius in kilometers

    public void simulate(final Person person) {
        setHotel(person);
        val places = getInterestedInPlaces(person);
        Stream.iterate(0, i -> i + 1)
                .limit(person.getNumberOfPlacesToVisit())
                .forEach(p -> visitNewPlace(person, places));

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

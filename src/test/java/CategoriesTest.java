import algo.Simulation;
import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import exception.InvalidProfileException;
import lombok.val;
import model.Category;
import model.Pair;
import model.Person;
import model.Profile;
import model.overpass.Bbox;
import navigation.NavigationServiceProvider;
import org.junit.Before;
import org.junit.Test;
import remote.OverpassService;
import remote.OverpassServiceProvider;
import remote.QueryService;

public class CategoriesTest {

    private Simulation simulation;

    @Before
    public void init() {
        val cracowBbox = Bbox.builder()
                .southernLat(50.03575315324749)
                .westernLon(19.90370750427246)
                .northernLat(50.084958885657535)
                .easternLon(19.994258880615234)
                .build();
        val osm = new OsmConnection("https://api.openstreetmap.org/api/0.6/",
                "my user agent", null);
        val mapDao = new MapDataDao(osm);

        val overpass = OverpassServiceProvider.get();
        val queryService = new QueryService(overpass);
        val navigationService = NavigationServiceProvider.get();
        simulation = Simulation.of(queryService, navigationService, cracowBbox);
    }

    @Test
    public void testAllCategories() {
        for (Category c : Category.values()) {
            System.out.println("[   TEST   ] Category being tested  ---  " + c.getTag() + " : " + c.getValue());
            val person = Person.of(Profile.builder()
                    .category(Pair.of(c, 1))
                    .avgSpendTime(3)
                    .build());
            try {
                simulation.simulate(person);
                System.out.println("[   TEST   ] Got person: " + person.getRoad());
                assert person.getRoad().stream().filter(n -> !n.getType().equals("navigationNode")).count() >= 2;
            } catch (InvalidProfileException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

import algo.Simulation;
import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import exception.InvalidProfileException;
import lombok.val;
import model.Person;
import model.Profile;
import model.overpass.Bbox;
import navigation.NavigationServiceProvider;
import remote.OverpassServiceProvider;
import remote.QueryService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
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
        val simulation = Simulation.of(queryService, navigationService, cracowBbox);
//        val profile = Profile.builder()
//                .category(Pair.of(Category.RESTAURANT, 3))
//                .avgSpendTime(2)
//                .build();
        val profile = Profile.PredefinedProfiles.MUSEUM_FAN.getProfile();
        val person = Person.of(profile);
        try {
            simulation.simulate(person);
            System.out.println(person);
            person.printRoadCsv();

            if (args.length == 2 && args[0].equals("-o")) {
                val outputName = args[1];
                val outputFile = new BufferedWriter(new FileWriter(outputName));
                outputFile.write(person.getRoadCsv());
                outputFile.close();
            }

        } catch (InvalidProfileException e) {
            System.err.println(e.getMessage());
        }

    }
}

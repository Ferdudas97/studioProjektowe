import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import hu.supercluster.overpasser.library.query.OverpassFilterQuery;
import hu.supercluster.overpasser.library.query.OverpassQuery;
import lombok.val;
import model.Bbox;
import remote.OverpassServiceProvider;
import remote.QueryService;

import java.io.IOException;

import static hu.supercluster.overpasser.library.output.OutputFormat.JSON;

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
        val result = queryService.execute(cracowBbox, Main::createQuery, 10);

        System.out.println(result);
    }

    private static OverpassFilterQuery createQuery() {
        return new OverpassQuery().format(JSON)
                .timeout(100000)
                .filterQuery()
                .way()
                .tag("highway");
    }
}

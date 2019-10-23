import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import hu.supercluster.overpasser.library.output.OutputModificator;
import hu.supercluster.overpasser.library.output.OutputOrder;
import hu.supercluster.overpasser.library.output.OutputVerbosity;
import hu.supercluster.overpasser.library.query.OverpassQuery;
import lombok.val;
import remote.OverpassServiceProvider;

import java.io.IOException;

import static hu.supercluster.overpasser.library.output.OutputFormat.JSON;

public class Main {

    public static void main(String[] args) throws IOException {
        val osm = new OsmConnection("https://api.openstreetmap.org/api/0.6/",
                "my user agent", null);
        val mapDao = new MapDataDao(osm);

        val overpass = OverpassServiceProvider.get();
        val query = new OverpassQuery().format(JSON)
                .timeout(30)
                .filterQuery()
                .node()
                .amenity("drinking_water")
                .boundingBox(
                        47.48047027491862, 19.039797484874725,
                        47.51331674014172, 19.07404761761427
                )
                .end()
                .output(OutputVerbosity.BODY, OutputModificator.CENTER, OutputOrder.QT, 100)
                .build();
        val result = overpass.interpreter(query).execute();

        System.out.println(result);
    }
}

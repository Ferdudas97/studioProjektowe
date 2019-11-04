package remote;

import hu.supercluster.overpasser.library.output.OutputModificator;
import hu.supercluster.overpasser.library.output.OutputOrder;
import hu.supercluster.overpasser.library.output.OutputVerbosity;
import hu.supercluster.overpasser.library.query.OverpassFilterQuery;
import lombok.AllArgsConstructor;
import lombok.val;
import model.Bbox;
import model.OverpassQueryResult;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class QueryService {

    private final OverpassService remoteService;

    public List<OverpassQueryResult> execute(final Bbox bbox, final Supplier<OverpassFilterQuery> querySupplier, final int partNumber) {
        val deltaLat = bbox.height() / partNumber;
        val deltaLon = bbox.width() / partNumber;

        return Stream.iterate(1, i -> i + 1)
                .limit(partNumber)
                .map(i -> bboxPartitioning(bbox, deltaLat * (i - 1), deltaLat * i, deltaLon, partNumber))
                .flatMap(Collection::stream)
//                .parallel()
                .map(b -> buildQuery(querySupplier.get(), b))
                .map(remoteService::interpreter)
                .map(overpassQueryResultCall -> {
                    try {
                        return overpassQueryResultCall.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(Response::body)
                .collect(Collectors.toList());
    }

    private String buildQuery(final OverpassFilterQuery query, final Bbox bbox) {
        return query.boundingBox(bbox.getSouthernLat(), bbox.getWesternLon(), bbox.getNorthernLat(), bbox.getEasternLon())
                .end()
                .output(OutputVerbosity.BODY, OutputModificator.CENTER, OutputOrder.QT, 100)
                .build();
    }

    private List<Bbox> bboxPartitioning(final Bbox bbox,
                                        final double northD,
                                        final double southD,
                                        final double deltaLon,
                                        final int partNumber) {
        return Stream.iterate(1, i -> i + 1).limit(partNumber)
                .map(i -> createBbox(bbox, deltaLon * (i - 1), deltaLon * i, northD, southD))
                .collect(Collectors.toList());
    }

    private Bbox createBbox(final Bbox bbox,
                            final double westD,
                            final double eastD,
                            final double northD,
                            final double southD) {
        return Bbox.builder()
                .westernLon(bbox.getWesternLon() + westD)
                .easternLon(bbox.getWesternLon() + eastD)
                .northernLat(bbox.getNorthernLat() - northD)
                .southernLat(bbox.getNorthernLat() - southD)
                .build();
    }
}

package navigation.osrm;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.val;
import navigation.NavigationService;
import navigation.model.Step;
import navigation.osrm.data.NavResponse;
import navigation.osrm.data.Route;
import navigation.osrm.data.StepsContainer;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OsrmNavigationService implements NavigationService {
    private final String apiUrl;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<Step> getStepsInRoute(final Double sourceLatitude, final Double sourceLongitude, final Double targetLatitude, final Double targetLongitude) {
        String url = createUrlWithQueryParams(sourceLatitude, sourceLongitude, targetLatitude, targetLongitude);
        val request = new Request.Builder().url(url).build();
        val steps = new ArrayList<Step>();
        try {
            val response = client.newCall(request).execute();
            NavResponse resp = deserializeResponse(response);
            resp.getRoutes()
                    .stream()
                    .map(Route::getLegs)
                    .map(l -> l.get(0))
                    .map(StepsContainer::getSteps)
                    .map(l -> l.stream().map(StepAdapter::adapt).collect(Collectors.toList()))
                    .forEach(steps::addAll);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return steps;
    }

    private NavResponse deserializeResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected error: " + response);
        }
        val gson = new Gson();
        assert response.body() != null;
        return gson.fromJson(response.body().string(), NavResponse.class);
    }

    private String createUrlWithQueryParams(Double sourceLatitude, Double sourceLongitude, Double targetLatitude, Double targetLongitude) {
        val urlBuilder = Objects.requireNonNull(HttpUrl.parse(apiUrl + sourceLongitude + "," + sourceLatitude + ";" + targetLongitude + "," + targetLatitude))
                .newBuilder();
        urlBuilder.addQueryParameter("alternatives", "false");
        urlBuilder.addQueryParameter("steps", "true");
        urlBuilder.addQueryParameter("overview", "full");
        return urlBuilder.build().toString();
    }
}

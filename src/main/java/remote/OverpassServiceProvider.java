package remote;

import lombok.experimental.UtilityClass;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@UtilityClass
public class OverpassServiceProvider {

    private OverpassService service;

    public OverpassService get() {
        if (service == null) {
            service = createService();
        }

        return service;
    }


    private OverpassService createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://overpass-api.de")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return OverpassServiceProxy.wrap(retrofit.create(OverpassService.class));
    }
}

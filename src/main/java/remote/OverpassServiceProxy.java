package remote;

import lombok.AllArgsConstructor;
import model.overpass.OverpassQueryResult;
import retrofit2.Call;

@AllArgsConstructor(staticName = "wrap")
public class OverpassServiceProxy implements OverpassService {

    private final OverpassService service;

    @Override
    public Call<OverpassQueryResult> interpreter(String data) {
        return service.interpreter(data.replaceAll("\"", ""));
    }
}

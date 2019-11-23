package remote;

import lombok.AllArgsConstructor;
import lombok.val;
import model.overpass.OverpassQueryResult;
import retrofit2.Call;

@AllArgsConstructor(staticName = "wrap")
public class OverpassServiceProxy implements OverpassService {

    private final OverpassService service;

    @Override
    public Call<OverpassQueryResult> interpreter(String data) {
        val result = data.replaceAll("\"", "");
        System.out.println(result);
        return service.interpreter(result);
    }
}

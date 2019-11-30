package navigation;

import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import navigation.osrm.OsrmNavigationService;

@UtilityClass
public class NavigationServiceProvider {
    private NavigationService service;

    @Synchronized
    public NavigationService get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private NavigationService createService() {
        return new OsrmNavigationService("http://localhost:5000/route/v1/foot/");
    }


}

package navigation;

import navigation.model.Step;

import java.util.List;

public interface NavigationService {
    List<Step> getStepsInRoute(final Double sourceLatitude, final Double sourceLongitude, final Double targetLatitude, final Double targetLongitude);
}

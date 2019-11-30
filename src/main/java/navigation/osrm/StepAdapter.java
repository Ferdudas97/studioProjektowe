package navigation.osrm;

import lombok.experimental.UtilityClass;
import navigation.model.Step;
import navigation.osrm.data.OsrmStep;

@UtilityClass
public class StepAdapter {
    public Step adapt(OsrmStep osrmStep) {
        return new Step(osrmStep.getManeuver().getLocationCoordinates().get(0),
                osrmStep.getManeuver().getLocationCoordinates().get(1),
                osrmStep.getDurationSeconds(),
                osrmStep.getDistance());
    }
}

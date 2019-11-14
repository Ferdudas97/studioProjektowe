package model;

import lombok.Value;
import model.overpass.Node;

import java.time.LocalDateTime;

@Value(staticConstructor = "of")
public class VisitedPlace {

    private Node node;
    private LocalDateTime started;
    private LocalDateTime end;

}

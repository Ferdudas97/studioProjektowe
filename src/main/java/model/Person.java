package model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Person {
    private Profile profile;
    private Map<Integer, List<VisitedPlace>> road;
}

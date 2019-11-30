package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Category {
    RESTAURANT("restaurant", null),
    HISTORIC_BUILDING("historic", "building"),
    CULTURE_ARTWORK("culture", "artwork"),
    CULTURE_GALLERY("culture", "gallery"),
    RELIGION("religion", null),
    CHAPEL("place_of_worship", null),
    HISTORIC("historic", null),
    TOURISM_MUSEUM("tourism", "museum"),
    HISTORIC_CASTLE("historic", "castle"),
    HISTORIC_ARCHAEOLOGICAL("historic", "archaeological_site"),
    HISTORIC_TOMB("historic", "tomb"),
    GRAVEYARD("grave_yard", null),
    PLACE_OF_WORSHIP("place_of_worship", null),
    CAFE("cafe", null)
    ;

    private String tag;
    private String value;
}
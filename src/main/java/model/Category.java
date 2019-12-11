package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Category {
    RESTAURANT("restaurant", null),
    HISTORIC_BUILDING("historic", "building"),
    CULTURE_ARTWORK("culture", "artwork"),
    CULTURE_GALLERY("culture", "gallery"),
    CULTURE_THEATRE("theatre", null),
    RELIGION("religion", null),
    CHAPEL("place_of_worship", null),
    HISTORIC("historic", null),
    TOURISM_MUSEUM("tourism", "museum"),
    HISTORIC_CASTLE("historic", "castle"),
    HISTORIC_ARCHAEOLOGICAL("historic", "archaeological_site"),
    HISTORIC_TOMB("historic", "tomb"),
    GRAVEYARD("grave_yard", null),
    PLACE_OF_WORSHIP("place_of_worship", null),
    CAFE("cafe", null),
    BAR("bar", null),
    PUB("pub", null),
    BIERGARTEN("biergarten", null),
    LANDUSE_RELIGIOUS("landuse", "religious")
    ;

    private String tag;
    private String value;

    public static Category drawCategoryWithExclusion(Category... excludedCategories) {
        Random random = new Random();
        List<Category> categoriesToDraw = Arrays.stream(values())
                .parallel()
                .filter(c -> Arrays.stream(excludedCategories).noneMatch(c::equals))
                .collect(Collectors.toList());
        return categoriesToDraw.get(random.nextInt(categoriesToDraw.size()));
    }
}
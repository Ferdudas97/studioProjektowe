package model;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class Profile {
    @Singular("category")
    private final List<Pair<Category, Integer>> categoriesToVisit;
    private final double avgSpendTime;

    @AllArgsConstructor
    @Getter
    public enum PredefinedProfiles {
        RECREATION(Profile.builder()
                .category(Pair.of(Category.CULTURE_ARTWORK, 3))
                .category(Pair.of(Category.CULTURE_GALLERY, 2))
                .category(Pair.of(Category.CHAPEL, 1))
                .category(Pair.of(Category.HISTORIC_BUILDING, 2))
                .category(Pair.of(Category.HISTORIC, 2))
                .avgSpendTime(1)
                .build()),
        MUSEUM_FAN(Profile.builder()
                .category(Pair.of(Category.TOURISM_MUSEUM, 5))
                .category(Pair.of(Category.CULTURE_GALLERY, 3))
                .category(Pair.of(Category.HISTORIC_CASTLE, 3))
                .category(Pair.of(Category.HISTORIC_ARCHAEOLOGICAL, 1))
                .avgSpendTime(1)
                .build()),
        RELIGIOUS(Profile.builder()
                .category(Pair.of(Category.HISTORIC_TOMB, 5))
                .category(Pair.of(Category.GRAVEYARD, 4))
                .category(Pair.of(Category.PLACE_OF_WORSHIP, 5))
                .category(Pair.of(Category.CHAPEL, 4))
                .category(Pair.of(Category.CAFE, 2))
                .avgSpendTime(1)
                .build()),
        FULL_CULTURE(Profile.builder()
                .category(Pair.of(Category.CULTURE_GALLERY, 2))
                .category(Pair.of(Category.CULTURE_THEATRE, 2))
                .category(Pair.of(Category.TOURISM_MUSEUM, 2))
                .category(Pair.of(Category.RESTAURANT, 2))
                .avgSpendTime(1)
                .build()),
        YEASTER(Profile.builder()
                .category(Pair.of(Category.BAR, 3))
                .category(Pair.of(Category.PUB, 3))
                .category(Pair.of(Category.BIERGARTEN, 3))
                .avgSpendTime(2)
                .build()),
        ANTYTEIST(Profile.builder()
                .category(Pair.of(Category.drawCategoryWithExclusion(Category.RELIGION, Category.PLACE_OF_WORSHIP, Category.LANDUSE_RELIGIOUS), 2))
                .category(Pair.of(Category.drawCategoryWithExclusion(Category.RELIGION, Category.PLACE_OF_WORSHIP, Category.LANDUSE_RELIGIOUS), 1))
                .category(Pair.of(Category.drawCategoryWithExclusion(Category.RELIGION, Category.PLACE_OF_WORSHIP, Category.LANDUSE_RELIGIOUS), 1))
                .avgSpendTime(1)
                .build()),
        ONLY_CULTURE(Profile.builder()
                .category(Pair.of(Category.drawCategoryWithExclusion(Category.RELIGION, Category.PLACE_OF_WORSHIP,
                        Category.LANDUSE_RELIGIOUS, Category.HISTORIC, Category.HISTORIC_ARCHAEOLOGICAL,
                        Category.HISTORIC_BUILDING, Category.HISTORIC_CASTLE, Category.HISTORIC_TOMB), 3))
                .category(Pair.of(Category.drawCategoryWithExclusion(Category.RELIGION, Category.PLACE_OF_WORSHIP,
                        Category.LANDUSE_RELIGIOUS, Category.HISTORIC, Category.HISTORIC_ARCHAEOLOGICAL,
                        Category.HISTORIC_BUILDING, Category.HISTORIC_CASTLE, Category.HISTORIC_TOMB), 2))
                .category(Pair.of(Category.drawCategoryWithExclusion(Category.RELIGION, Category.PLACE_OF_WORSHIP,
                        Category.LANDUSE_RELIGIOUS, Category.HISTORIC, Category.HISTORIC_ARCHAEOLOGICAL,
                        Category.HISTORIC_BUILDING, Category.HISTORIC_CASTLE, Category.HISTORIC_TOMB), 1))
                .avgSpendTime(2)
                .build())
        ;

        private Profile profile;
    }

    public void reducePriority(final Category category) {
        categoriesToVisit.stream().filter(pair -> pair.getFirst() == category)
                .findFirst()
                .ifPresent(p -> p.setSecond(p.getSecond() - 1));
    }

    public List<Category> getCategories() {
        return categoriesToVisit.stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    public List<Integer> getWeights() {
        return categoriesToVisit.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());
    }

}

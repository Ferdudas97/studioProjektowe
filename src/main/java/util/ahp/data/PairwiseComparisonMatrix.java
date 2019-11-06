package util.ahp.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PairwiseComparisonMatrix implements Cloneable {

    @Getter
    private Set<String> categories = new HashSet<>();

    /**
     * This map's key is generated based on two categories concatenated with ":". Should never be returned explicitly.
     */
    private Map<String, Double> categoriesToComparisionValue = new HashMap<>();

    public void addNewComparisionBetweenCategories(String rowCategory, String columnCategory, Double preferRowOverColumn) {
        assert rowCategory != null;
        assert columnCategory != null;
        assert preferRowOverColumn != null;
        categories.add(rowCategory);
        categories.add(columnCategory);
        categoriesToComparisionValue.put(generateKeyForComparison(rowCategory, columnCategory), preferRowOverColumn);
        categoriesToComparisionValue.put(generateKeyForComparison(columnCategory, rowCategory), 1 / preferRowOverColumn);
        categoriesToComparisionValue.put(generateKeyForComparison(rowCategory, rowCategory), 1.0);
        categoriesToComparisionValue.put(generateKeyForComparison(columnCategory, columnCategory), 1.0);
    }

    public Double getComparisionBetweenCategories(String category1, String category2) {
        assert category1 != null;
        assert category2 != null;
        String comparisonKey = generateKeyForComparison(category1, category2);
        return categoriesToComparisionValue.get(comparisonKey);
    }

    public boolean isMatrixCompleted() {
        return categoriesToComparisionValue.size() == Math.pow(categories.size(), 2);
    }

    public Double sumRow(String rowCategory) {
        return categories.stream().mapToDouble(cat -> getComparisionBetweenCategories(rowCategory, cat)).sum();
    }

    public Double sumColumn(String columnCategory) {
        return categories.stream().mapToDouble(cat -> getComparisionBetweenCategories(cat, columnCategory)).sum();
    }

    private String generateKeyForComparison(String rowCategory, String columnCategory) {
        return rowCategory + ":" + columnCategory;
    }

    public Map<String, Double> getColumn(String columnCategory) {
        Map<String, Double> columnValuesByCategory = new HashMap<>();
        for (String category : categories) {
            String key = generateKeyForComparison(category, columnCategory);
            columnValuesByCategory.put(category, categoriesToComparisionValue.get(key));
        }
        return columnValuesByCategory;
    }

    public void divideColumn(String columnCategory, double divider) {
        for (String rowCategory : categories) {
            String key = generateKeyForComparison(rowCategory, columnCategory);
            categoriesToComparisionValue.put(key, categoriesToComparisionValue.get(key) / divider);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void multiplyColumn(String columnCategory, double multiplier) {
        for (String rowCategory : categories) {
            String key = generateKeyForComparison(rowCategory, columnCategory);
            categoriesToComparisionValue.put(key, categoriesToComparisionValue.get(key) * multiplier);
        }
    }
}

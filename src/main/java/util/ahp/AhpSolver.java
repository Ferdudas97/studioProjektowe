package util.ahp;

import lombok.val;
import util.ahp.data.PairwiseComparisonMatrix;
import util.ahp.exceptions.ConsistencyRatioException;
import util.ahp.exceptions.NoAverageCalculatedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * AhpSolver is util class allowing user to calculate rankings for categories based on matrix given.
 */
public class AhpSolver {

    /**
     * Random Index is Saaty's Random Index map of constants declared for specific matrix dimensions.
     */
    private static final Map<Integer, Double> randomIndex = new HashMap<>();

    static {
        randomIndex.put(1, 0.0);
        randomIndex.put(2, 0.0);
        randomIndex.put(3, 0.58);
        randomIndex.put(4, 0.9);
        randomIndex.put(5, 1.12);
        randomIndex.put(6, 1.24);
        randomIndex.put(7, 1.32);
        randomIndex.put(8, 1.41);
        randomIndex.put(9, 1.45);
        randomIndex.put(10, 1.49);
        randomIndex.put(11, 1.41);
        randomIndex.put(12, 1.48);
        randomIndex.put(13, 1.56);
        randomIndex.put(14, 1.57);
        randomIndex.put(15, 1.59);
    }

    /**
     * Method generates ranking for choosing categories based on pairwise comparison matrix. Returned map consists of
     * category as key and preference value as value.
     * @param comparisonMatrix - pairwise comparison matrix defined by user
     * @return map of categories with their preference value
     * @throws CloneNotSupportedException should not be thrown.
     * @throws NoAverageCalculatedException exception is thrown when average from list cannot be calculated.
     * @throws ConsistencyRatioException exception is thrown when Consistency Ratio is higher than 0.1. User should
     *                                   specify better pairwise comparison matrix.
     */
    public static Map<String, Double> createRanking(PairwiseComparisonMatrix comparisonMatrix) throws CloneNotSupportedException, NoAverageCalculatedException, ConsistencyRatioException {
        PairwiseComparisonMatrix originalComparisonMatrix = (PairwiseComparisonMatrix) comparisonMatrix.clone();
        Map<String, Double> categoriesWeights = calculateRankingForCategories(comparisonMatrix);
        double consistencyRatio = calculateConsistencyRatio(comparisonMatrix, originalComparisonMatrix, categoriesWeights);
        validateConsistencyRatio(consistencyRatio);
        return categoriesWeights;
    }

    private static Map<String, Double> calculateRankingForCategories(PairwiseComparisonMatrix comparisonMatrix) {
        Map<String, Double> columnCategoriesToSums = generateColumnSumsByCategories(comparisonMatrix);
        divideColumnsByTheirSums(comparisonMatrix, columnCategoriesToSums);
        return generateWeightsOfCategories(comparisonMatrix);
    }

    private static double calculateConsistencyRatio(PairwiseComparisonMatrix comparisonMatrix, PairwiseComparisonMatrix originalComparisonMatrix, Map<String, Double> categoriesWeights) throws NoAverageCalculatedException {
        Map<String, Double> weightedSumByCategory = calculateWeightedSumsByCategories(comparisonMatrix, originalComparisonMatrix, categoriesWeights);
        Map<String, Double> lambdaForCategory = calculateLambdas(originalComparisonMatrix, categoriesWeights, weightedSumByCategory);
        double maxLambda = calculateAverage(lambdaForCategory);
        int matrixDim = lambdaForCategory.values().size();
        double consistencyIndex = (maxLambda - matrixDim) / (matrixDim - 1);
        return consistencyIndex / randomIndex.get(matrixDim);
    }

    private static void validateConsistencyRatio(double consistencyRatio) throws ConsistencyRatioException {
        if (consistencyRatio >= 0.1) {
            throw new ConsistencyRatioException();
        }
    }

    private static double calculateAverage(Map<String, Double> lambdaForCategory) throws NoAverageCalculatedException {
        return lambdaForCategory.values().stream().mapToDouble(a -> a).average().orElseThrow(NoAverageCalculatedException::new);
    }

    private static Map<String, Double> calculateLambdas(PairwiseComparisonMatrix originalComparisonMatrix, Map<String, Double> categoriesWeights, Map<String, Double> weightedSumByCategory) {
        Map<String, Double> lambdaForCategory = new HashMap<>();
        for (String category : originalComparisonMatrix.getCategories()) {
            lambdaForCategory.put(category, weightedSumByCategory.get(category) / categoriesWeights.get(category));
        }
        return lambdaForCategory;
    }

    private static Map<String, Double> calculateWeightedSumsByCategories(PairwiseComparisonMatrix comparisonMatrix, PairwiseComparisonMatrix originalComparisonMatrix, Map<String, Double> categoriesWeights) {
        Map<String, Double> weightedSumByCategory = new HashMap<>();
        for (String columnCategory : comparisonMatrix.getCategories()) {
            originalComparisonMatrix.multiplyColumn(columnCategory, categoriesWeights.get(columnCategory));
        }
        for (String rowCategory : originalComparisonMatrix.getCategories()) {
            weightedSumByCategory.put(rowCategory, originalComparisonMatrix.sumRow(rowCategory));
        }
        return weightedSumByCategory;
    }

    private static Map<String, Double> generateWeightsOfCategories(PairwiseComparisonMatrix comparisonMatrix) {
        Map<String, Double> categoriesWeights = new HashMap<>();
        Set<String> categories = comparisonMatrix.getCategories();
        for (String category : categories) {
            categoriesWeights.put(category, arithmeticAverageOfRow(comparisonMatrix, categories.size(), category));
        }
        return categoriesWeights;
    }

    private static double arithmeticAverageOfRow(PairwiseComparisonMatrix comparisonMatrix, int numberOfElements, String category) {
        return comparisonMatrix.sumRow(category) / numberOfElements;
    }

    private static void divideColumnsByTheirSums(PairwiseComparisonMatrix comparisonMatrix, Map<String, Double> columnCategoriesToSums) {
        for (String category : comparisonMatrix.getCategories()) {
            comparisonMatrix.divideColumn(category, columnCategoriesToSums.get(category));
        }
    }

    private static Map<String, Double> generateColumnSumsByCategories(PairwiseComparisonMatrix comparisonMatrix) {
        Map<String, Double> columnCategoriesToSums = new HashMap<>();
        for (val category : comparisonMatrix.getCategories()) {
            columnCategoriesToSums.put(category, comparisonMatrix.sumColumn(category));
        }
        return columnCategoriesToSums;
    }
}

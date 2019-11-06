package util.ahp;

import lombok.val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.ahp.data.PairwiseComparisonMatrix;

import java.util.Map;

import static util.ahp.AhpSolver.createRanking;

public class AhpSolverTests {

    private PairwiseComparisonMatrix matrix;

    @Before
    public void initMatrix() {
        matrix = new PairwiseComparisonMatrix();
        matrix.addNewComparisionBetweenCategories("Price", "Storage space", 5.0);
        matrix.addNewComparisionBetweenCategories("Price", "Camera", 4.0);
        matrix.addNewComparisionBetweenCategories("Price", "Looks", 7.0);
        matrix.addNewComparisionBetweenCategories("Storage space", "Camera", 0.5);
        matrix.addNewComparisionBetweenCategories("Storage space", "Looks", 3.0);
        matrix.addNewComparisionBetweenCategories("Camera", "Looks", 3.0);
    }

    @Test
    public void testRankingCreation() throws Exception {
        Map<String, Double> categoriesToPreferenceRanking = AhpSolver.createRanking(matrix);
        Assert.assertNotNull("Ranking is null", categoriesToPreferenceRanking);
        System.out.println("Ranking:");
        for (val categoryToPreference : categoriesToPreferenceRanking.entrySet()) {
            System.out.println(categoryToPreference.getKey() + " -> " + categoryToPreference.getValue());
        }
        Assert.assertTrue(Math.abs(categoriesToPreferenceRanking.get("Price") - 0.6038) < 0.01);
        Assert.assertTrue(Math.abs(categoriesToPreferenceRanking.get("Storage space") - 0.1365) < 0.01);
        Assert.assertTrue(Math.abs(categoriesToPreferenceRanking.get("Camera") - 0.1958) < 0.01);
        Assert.assertTrue(Math.abs(categoriesToPreferenceRanking.get("Looks") - 0.0646) < 0.01);
    }
}

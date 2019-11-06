package util.ahp.data;

import lombok.val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PairwiseComparisonMatrixTests {

    private PairwiseComparisonMatrix matrix;

    @Before
    public void init() {
        matrix = new PairwiseComparisonMatrix();
        matrix.addNewComparisionBetweenCategories("aaa", "bbb", 2.0);
        matrix.addNewComparisionBetweenCategories("ccc", "ddd", 3.0);
        matrix.addNewComparisionBetweenCategories("aaa", "ddd", 5.0);
        matrix.addNewComparisionBetweenCategories("aaa", "ccc", 7.0);
        matrix.addNewComparisionBetweenCategories("ccc", "bbb", 2.0);
        matrix.addNewComparisionBetweenCategories("bbb", "ddd", 4.0);
    }

    @Test
    public void testAllCategoriesExists() {
        Assert.assertNotNull(matrix.getCategories());
        Assert.assertEquals(matrix.getCategories().size(), 4);
    }

    @Test
    public void testCategoriesElements() {
        Assert.assertTrue(matrix.getCategories().containsAll(Arrays.asList("aaa", "bbb", "ccc", "ddd")));
    }

    @Test
    public void testComparisonForSameCategoryIsOne() {
        testComparisonValueForCategories("aaa", "aaa", 1.0);
        testComparisonValueForCategories("bbb", "bbb", 1.0);
        testComparisonValueForCategories("ccc", "ccc", 1.0);
        testComparisonValueForCategories("ddd", "ddd", 1.0);
    }

    @Test
    public void testComparisonValues() {
        testComparisonValueForCategories("aaa", "bbb", 2.0);
        testComparisonValueForCategories("ccc", "ddd", 3.0);
        testComparisonValueForCategories("aaa", "ddd", 5.0);
    }

    @Test
    public void testMatrixCompletion() {
        PairwiseComparisonMatrix mat = new PairwiseComparisonMatrix();
        Assert.assertTrue(mat.isMatrixCompleted());

        mat.addNewComparisionBetweenCategories("a", "b", 1.0);
        Assert.assertTrue(mat.isMatrixCompleted());

        mat.addNewComparisionBetweenCategories("a", "c", 2.0);
        Assert.assertFalse(mat.isMatrixCompleted());

        mat.addNewComparisionBetweenCategories("b", "c", 0.4);
        Assert.assertTrue(mat.isMatrixCompleted());
    }

    @Test
    public void testRowSum() {
        Assert.assertEquals(15.0, matrix.sumRow("aaa"), Math.abs(15.0 - matrix.sumRow("aaa")));
        Assert.assertEquals(6.0, matrix.sumRow("bbb"), Math.abs(15.0 - matrix.sumRow("bbb")));
        Assert.assertEquals(6.14, matrix.sumRow("ccc"), Math.abs(15.0 - matrix.sumRow("ccc")));
        Assert.assertEquals(1.78, matrix.sumRow("ddd"), Math.abs(15.0 - matrix.sumRow("ddd")));
    }

    @Test
    public void testColumnSum() {
        Assert.assertEquals((1.7 + (1.0 / 7.0)), matrix.sumColumn("ddd"), Math.abs((1.7 + (1.0 / 7.0)) - matrix.sumColumn("ddd")));
        Assert.assertEquals(5.25, matrix.sumColumn("ddd"), Math.abs(5.25 - matrix.sumColumn("ddd")));
        Assert.assertEquals(8.5 + (1.0 / 3.0), matrix.sumColumn("ddd"), Math.abs(8.5 + (1.0 / 3.0) - matrix.sumColumn("ddd")));
        Assert.assertEquals(13.0, matrix.sumColumn("ddd"), Math.abs(13.0 - matrix.sumColumn("ddd")));
    }

    @Test
    public void testGetColumn() {
        Map<String, Double> expectedColumnValues = new HashMap<>();
        expectedColumnValues.put("aaa", 1.0);
        expectedColumnValues.put("bbb", 0.5);
        expectedColumnValues.put("ccc", 1.0 / 7.0);
        expectedColumnValues.put("ddd", 1.0 / 5.0);
        Map<String, Double> aaaColumnValuesFromMatrix = matrix.getColumn("aaa");
        for (val valueForCategory : aaaColumnValuesFromMatrix.entrySet()) {
            Assert.assertEquals(valueForCategory.getValue(), expectedColumnValues.get(valueForCategory.getKey()),
                    Math.abs(valueForCategory.getValue() - expectedColumnValues.get(valueForCategory.getKey())));
        }
    }

    @Test
    public void testDivideColumnByDouble() {
        Map<String, Double> columnAaa = matrix.getColumn("aaa");
        matrix.divideColumn("aaa", 1.0);
        for (val columnValueAfterDivision : matrix.getColumn("aaa").entrySet()) {
            Assert.assertEquals(columnAaa.get(columnValueAfterDivision.getKey()), columnValueAfterDivision.getValue(),
                    Math.abs(columnAaa.get(columnValueAfterDivision.getKey()) - columnValueAfterDivision.getValue()));
        }

        matrix.divideColumn("aaa", 2.0);
        for (val columnValueAfterDivision : matrix.getColumn("aaa").entrySet()) {
            Assert.assertEquals(columnAaa.get(columnValueAfterDivision.getKey()) / 2.0, columnValueAfterDivision.getValue(),
                    Math.abs(columnAaa.get(columnValueAfterDivision.getKey()) / 2.0 - columnValueAfterDivision.getValue()));
        }
    }

    @Test
    public void testMultiplyColumnByDouble() {
        Map<String, Double> columnAaa = matrix.getColumn("aaa");
        matrix.multiplyColumn("aaa", 1.0);
        for (val columnValueAfterMultiplication : matrix.getColumn("aaa").entrySet()) {
            Assert.assertEquals(columnAaa.get(columnValueAfterMultiplication.getKey()), columnValueAfterMultiplication.getValue(),
                    Math.abs(columnAaa.get(columnValueAfterMultiplication.getKey()) - columnValueAfterMultiplication.getValue()));
        }

        matrix.multiplyColumn("aaa", 2.0);
        for (val columnValueAfterMultiplication : matrix.getColumn("aaa").entrySet()) {
            Assert.assertEquals(columnAaa.get(columnValueAfterMultiplication.getKey()) * 2.0, columnValueAfterMultiplication.getValue(),
                    Math.abs(columnAaa.get(columnValueAfterMultiplication.getKey()) * 2.0 - columnValueAfterMultiplication.getValue()));
        }
    }

    private void testComparisonValueForCategories(String cat1, String cat2, double expected) {
        Assert.assertEquals(expected, matrix.getComparisionBetweenCategories(cat1, cat2),
                Math.abs(expected - matrix.getComparisionBetweenCategories(cat1, cat2)));
    }
}

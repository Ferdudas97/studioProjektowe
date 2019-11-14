package service;

import lombok.val;
import model.Profile;
import parser.CsvParser;
import util.ahp.AhpSolver;
import util.ahp.data.PairwiseComparisonMatrix;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class AhpService {

    public void getRanking(final File file) {
        val rankings = CsvParser.parseFile(file).stream()
                .map(Profile::toMatrix)
                .map(this::createRanking)
                .collect(Collectors.toList());
    }

    private Map<String, Double> createRanking(final PairwiseComparisonMatrix matrix) {
        try {
            return AhpSolver.createRanking(matrix);
        } catch (Throwable e) {

        }
        return Collections.emptyMap();
    }
}

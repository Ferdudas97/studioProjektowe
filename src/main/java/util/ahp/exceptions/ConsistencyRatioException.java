package util.ahp.exceptions;

public class ConsistencyRatioException extends Exception {
    @Override
    public String getMessage() {
        return "Consistency Ratio for AHP method is too large. You need to specify more consistent comparision matrix.";
    }
}

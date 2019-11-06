package util.ahp.exceptions;

public class NoAverageCalculatedException extends Exception {
    @Override
    public String getMessage() {
        return "No average value was present after calculation.";
    }
}

package util.ahp.exceptions;

public class IncompleteMatrixException extends Exception {
    @Override
    public String getMessage() {
        return "Matrix is not completed (not all categories have preference with other categories).";
    }
}

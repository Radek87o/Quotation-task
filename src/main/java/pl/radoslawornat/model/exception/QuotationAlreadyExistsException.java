package pl.radoslawornat.model.exception;

public class QuotationAlreadyExistsException extends RuntimeException {

    public QuotationAlreadyExistsException(String message) {
        super(message);
    }
}

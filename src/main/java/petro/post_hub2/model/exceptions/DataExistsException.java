package petro.post_hub2.model.exceptions;

public class DataExistsException extends RuntimeException {
    public DataExistsException(String message) {
        super(message);
    }
}

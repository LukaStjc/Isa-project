package rs.ac.uns.ftn.informatika.jpa.exception;


public class CustomRetryableException extends RuntimeException {
    public CustomRetryableException(String message) {
        super(message);
    }
}


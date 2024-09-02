package rs.ac.uns.ftn.informatika.jpa.exception;

public class ReservationConflictException extends RuntimeException {
    public ReservationConflictException(String message) {
        super(message);
    }
}

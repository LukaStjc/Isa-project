package rs.ac.uns.ftn.informatika.jpa.exception;

public class ReservationLockedException extends RuntimeException {
    public ReservationLockedException(String message) {
        super(message);
    }
}


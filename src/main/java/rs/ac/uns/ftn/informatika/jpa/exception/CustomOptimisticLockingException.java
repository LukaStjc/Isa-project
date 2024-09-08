package rs.ac.uns.ftn.informatika.jpa.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomOptimisticLockingException extends RuntimeException {
    public CustomOptimisticLockingException(String message) {
        super(message);
    }
}


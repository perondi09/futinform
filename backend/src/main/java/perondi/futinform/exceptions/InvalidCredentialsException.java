package perondi.futinform.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("Invalid username/email or password", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }
}
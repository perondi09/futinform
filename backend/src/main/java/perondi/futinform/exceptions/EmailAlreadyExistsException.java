package perondi.futinform.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("Email already in use: " + email, HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
    }
}
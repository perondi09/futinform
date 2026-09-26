package perondi.futinform.exceptions;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException(String username) {
        super("Username already in use: " + username, HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS");
    }
}
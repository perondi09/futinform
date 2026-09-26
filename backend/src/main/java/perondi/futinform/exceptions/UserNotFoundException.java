package perondi.futinform.exceptions;

import org.springframework.http.HttpStatus;
import java.util.UUID;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(UUID id) {
        super("User not found: " + id, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}
package perondi.futinform.exceptions;

import org.springframework.http.HttpStatus;

public class PhoneAlreadyExistsException extends BusinessException {
    public PhoneAlreadyExistsException(String phone) {
        super("Phone already in use: " + phone, HttpStatus.CONFLICT, "PHONE_ALREADY_EXISTS");
    }
}
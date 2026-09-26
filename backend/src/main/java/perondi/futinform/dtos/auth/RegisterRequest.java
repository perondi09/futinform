package perondi.futinform.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Size(min = 3, max = 20) String username,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format") String phone,
        @NotBlank @Size(min = 8) String password
) {}    
package perondi.futinform.controllers;

import perondi.futinform.dtos.ApiResponse;
import perondi.futinform.dtos.user.UserResponse;
import perondi.futinform.entities.UserEntity;
import perondi.futinform.exceptions.UserNotFoundException;
import perondi.futinform.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        log.info("Listing all users");
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("Fetching user id={}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id, Authentication authentication) {
        UUID authenticatedId = (UUID) authentication.getPrincipal();

        if (!authenticatedId.equals(id)) {
            log.warn("User {} attempted to delete another account: {}", authenticatedId, id);
            throw new AccessDeniedException("You can only delete your own account");
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
        log.info("User deleted successfully: id={}", id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}
package perondi.futinform.controllers;

import perondi.futinform.dtos.ApiResponse;
import perondi.futinform.dtos.auth.*;
import perondi.futinform.entities.UserEntity;
import perondi.futinform.exceptions.*;
import perondi.futinform.repositories.UserRepository;
import perondi.futinform.security.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for username={}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new PhoneAlreadyExistsException(request.phone());
        }

        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        log.info("User created successfully: id={}, username={}", user.getId(), user.getUsername());

        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User created successfully", new AuthResponse(token)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for identifier={}", request.usernameOrEmail());

        UserEntity user = userRepository
                .findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt for identifier={}", request.usernameOrEmail());
            throw new InvalidCredentialsException();
        }

        log.info("Login successful: id={}, username={}", user.getId(), user.getUsername());
        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Login successful", new AuthResponse(token)));
    }
}
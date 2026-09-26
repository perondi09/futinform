package perondi.futinform.controllers;

import perondi.futinform.dtos.auth.*;
import perondi.futinform.dtos.auth.AuthResponse;
import perondi.futinform.dtos.auth.LoginRequest;
import perondi.futinform.dtos.auth.RegisterRequest;
import perondi.futinform.entities.UserEntity;
import perondi.futinform.repositories.UserRepository;
import perondi.futinform.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalStateException("Username já em uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email já em uso");
        }

        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        UserEntity user = userRepository
                .findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new IllegalStateException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalStateException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
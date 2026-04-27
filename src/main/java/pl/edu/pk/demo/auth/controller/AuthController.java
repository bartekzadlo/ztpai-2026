package pl.edu.pk.demo.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.demo.auth.dto.AuthResponse;
import pl.edu.pk.demo.auth.dto.LoginRequest;
import pl.edu.pk.demo.auth.dto.RegisterRequest;
import pl.edu.pk.demo.auth.service.AuthService;
import pl.edu.pk.demo.exception.ErrorResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Rejestruje nowego użytkownika. Hasło jest hashowane BCryptem.
     * Body: { "username": "...", "password": "...", "role": "USER|ADMIN" }
     * Role jest opcjonalne – domyślnie USER.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(409, e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     * Zwraca JWT po poprawnym logowaniu.
     * Body: { "username": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, e.getMessage()));
        }
    }
}

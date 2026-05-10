package pl.edu.pk.gamelibrary.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.auth.dto.AuthResponse;
import pl.edu.pk.gamelibrary.auth.dto.LoginRequest;
import pl.edu.pk.gamelibrary.auth.dto.RegisterRequest;
import pl.edu.pk.gamelibrary.auth.service.AuthService;
import pl.edu.pk.gamelibrary.exception.ErrorResponse;

@Tag(name = "Uwierzytelnianie", description = "Rejestracja i logowanie użytkowników")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Rejestracja nowego użytkownika",
               description = "Tworzy konto. Pole `role` jest opcjonalne (domyślnie USER). DEV ONLY: w produkcji rola ADMIN powinna być nadawana tylko przez admina.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Konto utworzone, zwraca token JWT"),
        @ApiResponse(responseCode = "400", description = "Błąd walidacji danych"),
        @ApiResponse(responseCode = "409", description = "Nazwa użytkownika już zajęta",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "Logowanie użytkownika",
               description = "Zwraca token JWT do użycia w nagłówku `Authorization: Bearer <token>`.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Zalogowano pomyślnie, zwraca token JWT"),
        @ApiResponse(responseCode = "401", description = "Nieprawidłowe dane logowania",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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

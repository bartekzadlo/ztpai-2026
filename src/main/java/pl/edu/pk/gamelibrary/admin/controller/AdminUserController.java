package pl.edu.pk.gamelibrary.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.admin.dto.AdminCreateUserRequest;
import pl.edu.pk.gamelibrary.auth.dto.AuthResponse;
import pl.edu.pk.gamelibrary.auth.model.Role;
import pl.edu.pk.gamelibrary.auth.service.AuthService;
import pl.edu.pk.gamelibrary.exception.ErrorResponse;

@Tag(name = "Admin", description = "Zarządzanie użytkownikami (tylko ADMIN)")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Utwórz użytkownika z wybraną rolą",
               description = "Endpoint dostępny tylko dla administratorów. Pozwala tworzyć konta z dowolną rolą (USER lub ADMIN).",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Użytkownik utworzony"),
        @ApiResponse(responseCode = "400", description = "Błąd walidacji"),
        @ApiResponse(responseCode = "401", description = "Brak tokenu JWT"),
        @ApiResponse(responseCode = "403", description = "Brak roli ADMIN"),
        @ApiResponse(responseCode = "409", description = "Nazwa użytkownika już zajęta")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        try {
            Role role = Role.USER;
            if ("ADMIN".equalsIgnoreCase(request.getRole())) {
                role = Role.ADMIN;
            }

            AuthResponse created = authService.createUserAsAdmin(
                    request.getUsername(),
                    request.getPassword(),
                    role
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(409, e.getMessage()));
        }
    }
}


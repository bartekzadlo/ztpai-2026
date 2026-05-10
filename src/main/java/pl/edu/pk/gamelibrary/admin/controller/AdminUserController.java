package pl.edu.pk.gamelibrary.admin.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.admin.dto.AdminCreateUserRequest;
import pl.edu.pk.gamelibrary.auth.dto.AuthResponse;
import pl.edu.pk.gamelibrary.auth.model.Role;
import pl.edu.pk.gamelibrary.auth.service.AuthService;
import pl.edu.pk.gamelibrary.exception.ErrorResponse;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/admin/users – tworzy użytkownika z rolą (tylko ADMIN) */
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


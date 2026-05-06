package pl.edu.pk.gamelibrary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.auth.repository.UserRepository;

import java.util.List;
import java.util.Map;

/**
 * Przykładowy kontroler z endpointami dostępnymi TYLKO dla roli ADMIN.
 * Wywołanie przez użytkownika z rolą USER -> 403 Forbidden.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /api/admin/users
     * Zwraca listę wszystkich zarejestrowanych użytkowników (bez haseł).
     * Wymaga roli ADMIN. Brak tokenu -> 401, token USER -> 403.
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "role", u.getRole().name()
                ))
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/admin/dashboard
     * Przykładowy endpoint administracyjny.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of(
                "message", "Witaj w panelu administratora!",
                "status", "OK"
        ));
    }
}

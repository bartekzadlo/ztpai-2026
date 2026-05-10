package pl.edu.pk.gamelibrary.library;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.auth.repository.UserRepository;
import pl.edu.pk.gamelibrary.common.dto.PagedResponse;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.library.dto.UserGameRequest;
import pl.edu.pk.gamelibrary.library.dto.UserGameResponse;

@Tag(name = "Biblioteka", description = "Zarządzanie osobistą biblioteką gier użytkownika")
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final UserRepository userRepository;

    public LibraryController(LibraryService libraryService, UserRepository userRepository) {
        this.libraryService = libraryService;
        this.userRepository = userRepository;
    }

    @Operation(
        summary = "Pobierz bibliotekę użytkownika",
        description = "Wymaga uwierzytelnienia. Zwraca listę gier w bibliotece zalogowanego użytkownika.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista gier w bibliotece"),
        @ApiResponse(responseCode = "401", description = "Brak tokenu JWT")
    })
    @GetMapping
    public PagedResponse<UserGameResponse> getLibrary(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", auth.getName()));
        
        Page<UserGameResponse> result = libraryService.getUserLibrary(user, PageRequest.of(page, size));
        
        return new PagedResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    @Operation(
        summary = "Pobierz status gry w bibliotece użytkownika",
        description = "Wymaga uwierzytelnienia. Zwraca status pojedynczej gry w bibliotece lub null jeśli nie ma.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status gry (może być null)"),
        @ApiResponse(responseCode = "401", description = "Brak tokenu JWT")
    })
    @GetMapping("/game/{gameId}")
    public ResponseEntity<UserGameResponse> getGameStatus(Authentication auth, @PathVariable Long gameId) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", auth.getName()));

        UserGameResponse response = libraryService.getUserGameByGameId(user, gameId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Dodaj lub zaktualizuj grę w bibliotece",
        description = "Wymaga uwierzytelnienia. Jeśli gra już jest w bibliotece, zostanie zaktualizowana.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gra dodana/zaktualizowana"),
        @ApiResponse(responseCode = "400", description = "Błąd walidacji"),
        @ApiResponse(responseCode = "401", description = "Brak tokenu JWT"),
        @ApiResponse(responseCode = "404", description = "Gra nie istnieje")
    })
    @PutMapping
    public UserGameResponse upsertGame(Authentication auth, @Valid @RequestBody UserGameRequest request) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", auth.getName()));
        
        return libraryService.upsertUserGame(user, request);
    }

    @Operation(
        summary = "Usuń grę z biblioteki",
        description = "Wymaga uwierzytelnienia. Usuwa grę z biblioteki zalogowanego użytkownika.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Gra usunięta"),
        @ApiResponse(responseCode = "401", description = "Brak tokenu JWT"),
        @ApiResponse(responseCode = "404", description = "Gra nie znajduje się w bibliotece")
    })
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> removeGame(Authentication auth, @PathVariable Long gameId) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", auth.getName()));
        
        libraryService.removeFromLibrary(user, gameId);
        return ResponseEntity.noContent().build();
    }
}

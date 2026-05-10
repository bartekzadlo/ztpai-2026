package pl.edu.pk.gamelibrary.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.auth.repository.UserRepository;
import pl.edu.pk.gamelibrary.common.dto.PagedResponse;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.review.dto.GameRatingStatsResponse;
import pl.edu.pk.gamelibrary.review.dto.ReviewRequest;
import pl.edu.pk.gamelibrary.review.dto.ReviewResponse;

import java.util.Map;

@Tag(name = "Recenzje", description = "Recenzje gier i statystyki ocen")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Recenzje danej gry (stronicowane)", description = "Publiczny endpoint.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista recenzji"),
        @ApiResponse(responseCode = "404", description = "Gra nie istnieje")
    })
    @GetMapping("/game/{gameId}")
    public ResponseEntity<PagedResponse<ReviewResponse>> getReviewsByGame(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Sort parsedSort = parseSort(sort);
        PageRequest pageable = PageRequest.of(page, size, parsedSort);

        Page<ReviewResponse> result = reviewService.getReviewsByGame(gameId, pageable)
                .map(ReviewMapper::toResponse);

        return ResponseEntity.ok(new PagedResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        ));
    }

    @Operation(summary = "Szczegóły recenzji po ID", description = "Publiczny endpoint.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dane recenzji"),
        @ApiResponse(responseCode = "404", description = "Recenzja nie istnieje")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(ReviewMapper.toResponse(reviewService.getReviewById(id)));
    }

    @Operation(summary = "Średnia ocena gry", description = "Publiczny endpoint.")
    @ApiResponse(responseCode = "200", description = "Średnia ocena (pole `averageScore`)")
    @GetMapping("/game/{gameId}/average")
    public ResponseEntity<Map<String, Double>> getAverageScore(@PathVariable Long gameId) {
        double avg = reviewService.getAverageScoreForGame(gameId);
        return ResponseEntity.ok(Map.of("averageScore", avg));
    }

    @Operation(summary = "Szczegółowe statystyki ocen gry", description = "Publiczny endpoint.")
    @ApiResponse(responseCode = "200", description = "Statystyki ocen")
    @GetMapping("/game/{gameId}/stats")
    public ResponseEntity<GameRatingStatsResponse> getRatingStats(@PathVariable Long gameId) {
        return ResponseEntity.ok(reviewService.getRatingStatsForGame(gameId));
    }

    @Operation(summary = "Dodaj recenzję", description = "Wymaga zalogowania (token JWT).",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recenzja dodana"),
        @ApiResponse(responseCode = "400", description = "Błąd walidacji"),
        @ApiResponse(responseCode = "401", description = "Brak tokenu JWT")
    })
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        Long authorId = resolveAuthorId(authentication);
        Review created = reviewService.createReview(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReviewMapper.toResponse(created));
    }

    @Operation(summary = "Aktualizuj recenzję", description = "Wymaga zalogowania. Można edytować tylko własną recenzję.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recenzja zaktualizowana"),
        @ApiResponse(responseCode = "403", description = "Nie jesteś autorem tej recenzji"),
        @ApiResponse(responseCode = "404", description = "Recenzja nie istnieje")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        Long authorId = resolveAuthorId(authentication);
        Review updated = reviewService.updateReview(id, request, authorId);
        return ResponseEntity.ok(ReviewMapper.toResponse(updated));
    }

    @Operation(summary = "Usuń recenzję", description = "Wymaga zalogowania. Można usunąć tylko własną recenzję.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Recenzja usunięta"),
        @ApiResponse(responseCode = "403", description = "Nie jesteś autorem tej recenzji"),
        @ApiResponse(responseCode = "404", description = "Recenzja nie istnieje")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {
        Long authorId = resolveAuthorId(authentication);
        reviewService.deleteReview(id, authorId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveAuthorId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(u -> u.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", "username=" + username));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        String dir = parts.length > 1 ? parts[1].trim() : "desc";
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}

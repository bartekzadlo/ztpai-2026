package pl.edu.pk.gamelibrary.review;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.review.dto.ReviewRequest;
import pl.edu.pk.gamelibrary.review.dto.ReviewResponse;

import java.util.List;
import java.util.Map;

/**
 * REST API dla modułu recenzji.
 *
 * W docelowej implementacji authorId powinien być pobierany
 * z kontekstu bezpieczeństwa (Principal / JWT), tu przekazywany
 * jako nagłówek X-User-Id dla uproszczenia.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** GET /api/reviews/game/{gameId} – wszystkie recenzje danej gry */
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByGame(@PathVariable Long gameId) {
        List<ReviewResponse> responses = reviewService.getReviewsByGame(gameId)
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** GET /api/reviews/{id} – pojedyncza recenzja */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(ReviewMapper.toResponse(reviewService.getReviewById(id)));
    }

    /** GET /api/reviews/game/{gameId}/average – średnia ocena gry */
    @GetMapping("/game/{gameId}/average")
    public ResponseEntity<Map<String, Double>> getAverageScore(@PathVariable Long gameId) {
        double avg = reviewService.getAverageScoreForGame(gameId);
        return ResponseEntity.ok(Map.of("averageScore", avg));
    }

    /** POST /api/reviews – nowa recenzja */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("X-User-Id") Long authorId) {
        Review created = reviewService.createReview(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReviewMapper.toResponse(created));
    }

    /** PUT /api/reviews/{id} – aktualizacja recenzji */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("X-User-Id") Long authorId) {
        Review updated = reviewService.updateReview(id, request, authorId);
        return ResponseEntity.ok(ReviewMapper.toResponse(updated));
    }

    /** DELETE /api/reviews/{id} – usunięcie recenzji */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long authorId) {
        reviewService.deleteReview(id, authorId);
        return ResponseEntity.noContent().build();
    }
}

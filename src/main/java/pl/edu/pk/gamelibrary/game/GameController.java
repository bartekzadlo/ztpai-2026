package pl.edu.pk.gamelibrary.game;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.game.dto.GameRequest;
import pl.edu.pk.gamelibrary.game.dto.GameResponse;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /** GET /api/games -> 200 z listą wszystkich gier */
    @GetMapping
    public List<GameResponse> getAll() {
        return gameService.getAllGames().stream()
                .map(GameMapper::toResponse)
                .toList();
    }

    /** GET /api/games/{id} -> 200 lub 404 */
    @GetMapping("/{id}")
    public GameResponse getById(@PathVariable Long id) {
        return GameMapper.toResponse(gameService.getGameById(id));
    }

    /** POST /api/games -> 201 lub 400 przy złych danych. Wymaga roli ADMIN */
    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest request) {
        GameResponse response = GameMapper.toResponse(
                gameService.createGame(GameMapper.toEntity(request))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** PUT /api/games/{id} -> 200 lub 404. Wymaga roli ADMIN */
    @PutMapping("/{id}")
    public GameResponse update(@PathVariable Long id, @Valid @RequestBody GameRequest request) {
        return GameMapper.toResponse(
                gameService.updateGame(id, GameMapper.toEntity(request))
        );
    }

    /** DELETE /api/games/{id} -> 204 lub 404. Wymaga roli ADMIN */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}

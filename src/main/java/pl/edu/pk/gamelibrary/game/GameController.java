package pl.edu.pk.gamelibrary.game;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pk.gamelibrary.common.dto.PagedResponse;
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
    public PagedResponse<GameResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title,asc") String sort,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) Integer releaseYearFrom,
            @RequestParam(required = false) Integer releaseYearTo,
            @RequestParam(required = false) Boolean hasStory
    ) {
        Sort parsedSort = parseSort(sort);
        PageRequest pageable = PageRequest.of(page, size, parsedSort);

        GameSearchCriteria criteria = new GameSearchCriteria();
        criteria.setTitle(title);
        criteria.setGenre(genre);
        criteria.setPlatform(platform);
        criteria.setReleaseYearFrom(releaseYearFrom);
        criteria.setReleaseYearTo(releaseYearTo);
        criteria.setHasStory(hasStory);

        Page<GameResponse> result = gameService.searchGames(criteria, pageable)
                .map(GameMapper::toResponse);

        return new PagedResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
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

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "title");
        }
        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        String dir = parts.length > 1 ? parts[1].trim() : "asc";
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}

package pl.edu.pk.gamelibrary.library;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pk.gamelibrary.auth.model.AppUser;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;
import pl.edu.pk.gamelibrary.game.Game;
import pl.edu.pk.gamelibrary.game.GameRepository;
import pl.edu.pk.gamelibrary.library.dto.UserGameRequest;
import pl.edu.pk.gamelibrary.library.dto.UserGameResponse;

@Service
public class LibraryService {

    private final UserGameRepository userGameRepository;
    private final GameRepository gameRepository;

    public LibraryService(UserGameRepository userGameRepository, GameRepository gameRepository) {
        this.userGameRepository = userGameRepository;
        this.gameRepository = gameRepository;
    }

    public Page<UserGameResponse> getUserLibrary(AppUser user, Pageable pageable) {
        Page<UserGame> userGames = userGameRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("user"), user),
                pageable
        );
        return userGames.map(this::toResponse);
    }

    @Transactional
    public UserGameResponse upsertUserGame(AppUser user, UserGameRequest request) {
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Gra", request.getGameId()));

        UserGame userGame = userGameRepository.findByUserIdAndGameId(user.getId(), game.getId())
                .orElseGet(() -> {
                    UserGame newEntry = new UserGame();
                    newEntry.setUser(user);
                    newEntry.setGame(game);
                    return newEntry;
                });

        userGame.setStatus(request.getStatus());
        userGame.setFavorite(request.isFavorite());
        userGame.setOwned(request.isOwned());
        userGame.setHoursPlayed(request.getHoursPlayed());

        UserGame saved = userGameRepository.save(userGame);
        return toResponse(saved);
    }

    public UserGameResponse getUserGameByGameId(AppUser user, Long gameId) {
        return userGameRepository.findByUserIdAndGameId(user.getId(), gameId)
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public void removeFromLibrary(AppUser user, Long gameId) {
        if (!userGameRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new ResourceNotFoundException("Wpis w bibliotece", gameId);
        }
        userGameRepository.deleteByUserIdAndGameId(user.getId(), gameId);
    }

    private UserGameResponse toResponse(UserGame ug) {
        UserGameResponse res = new UserGameResponse();
        res.setGameId(ug.getGame().getId());
        res.setGameTitle(ug.getGame().getTitle());
        res.setStatus(ug.getStatus());
        res.setFavorite(ug.isFavorite());
        res.setOwned(ug.isOwned());
        res.setHoursPlayed(ug.getHoursPlayed());
        return res;
    }
}

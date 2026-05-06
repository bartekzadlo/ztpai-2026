package pl.edu.pk.gamelibrary.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pk.gamelibrary.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService – testy jednostkowe")
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Nested
    @DisplayName("getGameById")
    class GetGameById {

        @Test
        @DisplayName("zwraca grę gdy findById(1L) ją znajdzie")
        void getGameById_shouldReturnGame_whenFound() {
            Game mockGame = new Game("The Witcher 3", "RPG z otwartym światem",
                    "RPG", "PC", 2015, null);
            when(gameRepository.findById(1L)).thenReturn(Optional.of(mockGame));

            Game result = gameService.getGameById(1L);

            assertNotNull(result, "Zwrócona gra nie powinna być null");
            assertEquals("The Witcher 3", result.getTitle(),
                    "Tytuł gry powinien być 'The Witcher 3'");

            verify(gameRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy findById(99L) zwróci Optional.empty()")
        void getGameById_shouldThrow_whenNotFound() {
            when(gameRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> gameService.getGameById(99L),
                    "Powinien zostać rzucony ResourceNotFoundException gdy gra nie istnieje");

            verify(gameRepository, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("createGame")
    class CreateGame {

        @Test
        @DisplayName("zapisuje grę i zwraca ją z nadanym id")
        void createGame_shouldReturnSavedGameWithId() {
            Game input = new Game("Cyberpunk 2077", "RPG przyszłości", "RPG", "PC", 2020, null);
            Game saved = new Game("Cyberpunk 2077", "RPG przyszłości", "RPG", "PC", 2020, null);
            setId(saved, 42L);
            when(gameRepository.save(any(Game.class))).thenReturn(saved);

            Game result = gameService.createGame(input);

            assertNotNull(result.getId(), "Zwrócona gra powinna mieć nadane id");
            assertEquals(42L, result.getId(), "Id powinno wynosić 42");

            verify(gameRepository, times(1)).save(any(Game.class));
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy title jest null")
        void createGame_shouldThrow_whenTitleIsNull() {
            Game game = new Game(null, "opis", "RPG", "PC", 2020, null);

            assertThrows(IllegalArgumentException.class,
                    () -> gameService.createGame(game),
                    "Powinien zostać rzucony IllegalArgumentException gdy title == null");

            verify(gameRepository, never()).save(any());
        }

        @Test
        @DisplayName("rzuca IllegalArgumentException gdy title jest pustym stringiem")
        void createGame_shouldThrow_whenTitleIsBlank() {
            Game game = new Game("   ", "opis", "RPG", "PC", 2020, null);

            assertThrows(IllegalArgumentException.class,
                    () -> gameService.createGame(game),
                    "Powinien zostać rzucony IllegalArgumentException gdy title jest pusty");

            verify(gameRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteGame")
    class DeleteGame {

        @Test
        @DisplayName("usuwa grę gdy istnieje")
        void deleteGame_shouldDelete_whenFound() {
            Game mockGame = new Game("Dark Souls", "Trudne RPG", "RPG", "PC", 2011, null);
            setId(mockGame, 1L);
            when(gameRepository.findById(1L)).thenReturn(Optional.of(mockGame));

            assertDoesNotThrow(() -> gameService.deleteGame(1L));

            verify(gameRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("rzuca ResourceNotFoundException gdy gra nie istnieje")
        void deleteGame_shouldThrow_whenNotFound() {
            when(gameRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> gameService.deleteGame(99L));

            verify(gameRepository, never()).deleteById(any());
        }
    }

    // Pomocnicza metoda ustawiająca id przez refleksję (pole prywatne bez settera)
    private static void setId(Game game, Long id) {
        try {
            var field = Game.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(game, id);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się ustawić id przez refleksję", e);
        }
    }
}

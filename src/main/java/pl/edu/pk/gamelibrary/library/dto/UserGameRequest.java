package pl.edu.pk.gamelibrary.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import pl.edu.pk.gamelibrary.library.LibraryStatus;

public class UserGameRequest {
    @NotNull(message = "ID gry nie może być puste")
    private Long gameId;

    @NotNull(message = "Status nie może być pusty")
    private LibraryStatus status;

    private boolean favorite;
    private boolean owned;

    @Min(value = 0, message = "Liczba godzin nie może być ujemna")
    private Integer hoursPlayed;

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public LibraryStatus getStatus() { return status; }
    public void setStatus(LibraryStatus status) { this.status = status; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public boolean isOwned() { return owned; }
    public void setOwned(boolean owned) { this.owned = owned; }

    public Integer getHoursPlayed() { return hoursPlayed; }
    public void setHoursPlayed(Integer hoursPlayed) { this.hoursPlayed = hoursPlayed; }
}

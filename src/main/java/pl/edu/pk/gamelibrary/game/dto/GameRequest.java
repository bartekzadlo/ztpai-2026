package pl.edu.pk.gamelibrary.game.dto;

import jakarta.validation.constraints.*;

public class GameRequest {

    @NotBlank(message = "Tytuł gry nie może być pusty")
    @Size(min = 1, max = 200, message = "Tytuł musi mieć od 1 do 200 znaków")
    private String title;

    @Size(max = 1000, message = "Opis nie może przekraczać 1000 znaków")
    private String description;

    @NotBlank(message = "Gatunek nie może być pusty")
    private String genre;

    @NotBlank(message = "Platforma nie może być pusta")
    private String platform;

    @Min(value = 1950, message = "Rok wydania nie może być wcześniejszy niż 1950")
    @Max(value = 2100, message = "Rok wydania nie może być późniejszy niż 2100")
    private Integer releaseYear;

    private String coverUrl;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}

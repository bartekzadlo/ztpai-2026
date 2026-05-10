package pl.edu.pk.gamelibrary.game.dto;

import jakarta.validation.constraints.*;
import pl.edu.pk.gamelibrary.review.RatingProfile;

import java.util.List;

public class GameRequest {

    @NotBlank(message = "Tytuł gry nie może być pusty")
    @Size(min = 1, max = 200, message = "Tytuł musi mieć od 1 do 200 znaków")
    private String title;

    @Size(max = 1000, message = "Opis nie może przekraczać 1000 znaków")
    private String description;

    @NotNull(message = "Lista gatunków nie może być pusta")
    @Size(min = 1, message = "Gra musi mieć przynajmniej jeden gatunek")
    private List<String> genres;

    @NotNull(message = "Lista platform nie może być pusta")
    @Size(min = 1, message = "Gra musi mieć przynajmniej jedną platformę")
    private List<String> platforms;

    @Min(value = 1950, message = "Rok wydania nie może być wcześniejszy niż 1950")
    @Max(value = 2100, message = "Rok wydania nie może być późniejszy niż 2100")
    private Integer releaseYear;

    private String coverUrl;

    /** Czy gra ma fabułę/narrację (gdy null -> domyślnie true). */
    private Boolean hasStory;

    /** Domyślny profil oceniania (gdy null -> DEFAULT). */
    private RatingProfile defaultRatingProfile;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public List<String> getPlatforms() { return platforms; }
    public void setPlatforms(List<String> platforms) { this.platforms = platforms; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public Boolean getHasStory() { return hasStory; }
    public void setHasStory(Boolean hasStory) { this.hasStory = hasStory; }

    public RatingProfile getDefaultRatingProfile() { return defaultRatingProfile; }
    public void setDefaultRatingProfile(RatingProfile defaultRatingProfile) { this.defaultRatingProfile = defaultRatingProfile; }
}

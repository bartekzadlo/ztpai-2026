package pl.edu.pk.gamelibrary.game;

import jakarta.persistence.*;
import pl.edu.pk.gamelibrary.review.RatingProfile;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String genre;
    private String platform;
    private Integer releaseYear;
    private String coverUrl;

    /** Czy gra ma sensownie ocenialną fabułę/narrację (true dla narracyjnych). */
    @Column(nullable = false)
    private boolean hasStory = true;

    /** Domyślny profil oceniania dla recenzji tej gry. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RatingProfile defaultRatingProfile = RatingProfile.DEFAULT;

    public Game() {}

    public Game(String title, String description, String genre, String platform,
                Integer releaseYear, String coverUrl) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.platform = platform;
        this.releaseYear = releaseYear;
        this.coverUrl = coverUrl;
    }

    public Game(String title, String description, String genre, String platform,
                Integer releaseYear, String coverUrl,
                boolean hasStory, RatingProfile defaultRatingProfile) {
        this(title, description, genre, platform, releaseYear, coverUrl);
        this.hasStory = hasStory;
        if (defaultRatingProfile != null) {
            this.defaultRatingProfile = defaultRatingProfile;
        }
    }

    public Long getId() { return id; }

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

    public boolean isHasStory() { return hasStory; }
    public void setHasStory(boolean hasStory) { this.hasStory = hasStory; }

    public RatingProfile getDefaultRatingProfile() { return defaultRatingProfile; }
    public void setDefaultRatingProfile(RatingProfile defaultRatingProfile) {
        this.defaultRatingProfile = defaultRatingProfile != null ? defaultRatingProfile : RatingProfile.DEFAULT;
    }
}

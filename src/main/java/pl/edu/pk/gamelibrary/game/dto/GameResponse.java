package pl.edu.pk.gamelibrary.game.dto;

import pl.edu.pk.gamelibrary.review.RatingProfile;

public class GameResponse {

    private Long id;
    private String title;
    private String description;
    private String genre;
    private String platform;
    private Integer releaseYear;
    private String coverUrl;

    private boolean hasStory;
    private RatingProfile defaultRatingProfile;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
    public void setDefaultRatingProfile(RatingProfile defaultRatingProfile) { this.defaultRatingProfile = defaultRatingProfile; }
}

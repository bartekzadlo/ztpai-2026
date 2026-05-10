package pl.edu.pk.gamelibrary.review.dto;

import java.time.LocalDateTime;
import pl.edu.pk.gamelibrary.review.RatingProfile;

public class ReviewResponse {

    private Long id;
    private Long gameId;
    private String gameTitle;
    private Long authorId;
    private String authorUsername;
    private String title;
    private String content;

    // Kryteria oceniania
    private Integer gameplayScore;
    private Integer graphicsScore;
    private Integer soundScore;
    private Integer storyScore;
    private Integer replayValueScore;

    /** Ważona ocena ogólna obliczona przez encję. */
    private Double overallScore;

    private RatingProfile ratingProfile;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public String getGameTitle() { return gameTitle; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getGameplayScore() { return gameplayScore; }
    public void setGameplayScore(Integer gameplayScore) { this.gameplayScore = gameplayScore; }

    public Integer getGraphicsScore() { return graphicsScore; }
    public void setGraphicsScore(Integer graphicsScore) { this.graphicsScore = graphicsScore; }

    public Integer getSoundScore() { return soundScore; }
    public void setSoundScore(Integer soundScore) { this.soundScore = soundScore; }

    public Integer getStoryScore() { return storyScore; }
    public void setStoryScore(Integer storyScore) { this.storyScore = storyScore; }

    public Integer getReplayValueScore() { return replayValueScore; }
    public void setReplayValueScore(Integer replayValueScore) { this.replayValueScore = replayValueScore; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public RatingProfile getRatingProfile() { return ratingProfile; }
    public void setRatingProfile(RatingProfile ratingProfile) { this.ratingProfile = ratingProfile; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

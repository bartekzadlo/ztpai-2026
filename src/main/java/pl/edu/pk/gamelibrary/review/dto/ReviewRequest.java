package pl.edu.pk.gamelibrary.review.dto;

import jakarta.validation.constraints.*;

public class ReviewRequest {

    @NotNull(message = "Id gry nie może być null")
    private Long gameId;

    @NotBlank(message = "Tytuł recenzji nie może być pusty")
    @Size(min = 3, max = 200, message = "Tytuł musi mieć od 3 do 200 znaków")
    private String title;

    @NotBlank(message = "Treść recenzji nie może być pusta")
    @Size(min = 10, max = 5000, message = "Treść musi mieć od 10 do 5000 znaków")
    private String content;

    @NotNull(message = "Ocena rozgrywki jest wymagana")
    @Min(value = 1, message = "Ocena musi być co najmniej 1")
    @Max(value = 10, message = "Ocena nie może przekraczać 10")
    private Integer gameplayScore;

    @NotNull(message = "Ocena grafiki jest wymagana")
    @Min(value = 1, message = "Ocena musi być co najmniej 1")
    @Max(value = 10, message = "Ocena nie może przekraczać 10")
    private Integer graphicsScore;

    @NotNull(message = "Ocena dźwięku jest wymagana")
    @Min(value = 1, message = "Ocena musi być co najmniej 1")
    @Max(value = 10, message = "Ocena nie może przekraczać 10")
    private Integer soundScore;

    @NotNull(message = "Ocena fabuły jest wymagana")
    @Min(value = 1, message = "Ocena musi być co najmniej 1")
    @Max(value = 10, message = "Ocena nie może przekraczać 10")
    private Integer storyScore;

    @NotNull(message = "Ocena regrywalności jest wymagana")
    @Min(value = 1, message = "Ocena musi być co najmniej 1")
    @Max(value = 10, message = "Ocena nie może przekraczać 10")
    private Integer replayValueScore;

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

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
}

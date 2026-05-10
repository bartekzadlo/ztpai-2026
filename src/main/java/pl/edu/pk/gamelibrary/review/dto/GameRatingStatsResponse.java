package pl.edu.pk.gamelibrary.review.dto;

import java.util.Map;

public class GameRatingStatsResponse {
    private Long gameId;
    private long ratingCount;
    private double averageOverallScore;

    private double averageGameplayScore;
    private double averageGraphicsScore;
    private double averageSoundScore;
    private double averageStoryScore;
    private double averageReplayValueScore;

    /** Histogram dla overallScore: klucze 1..10, wartości = liczba recenzji. */
    private Map<Integer, Long> overallScoreHistogram;

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public long getRatingCount() { return ratingCount; }
    public void setRatingCount(long ratingCount) { this.ratingCount = ratingCount; }

    public double getAverageOverallScore() { return averageOverallScore; }
    public void setAverageOverallScore(double averageOverallScore) { this.averageOverallScore = averageOverallScore; }

    public double getAverageGameplayScore() { return averageGameplayScore; }
    public void setAverageGameplayScore(double averageGameplayScore) { this.averageGameplayScore = averageGameplayScore; }

    public double getAverageGraphicsScore() { return averageGraphicsScore; }
    public void setAverageGraphicsScore(double averageGraphicsScore) { this.averageGraphicsScore = averageGraphicsScore; }

    public double getAverageSoundScore() { return averageSoundScore; }
    public void setAverageSoundScore(double averageSoundScore) { this.averageSoundScore = averageSoundScore; }

    public double getAverageStoryScore() { return averageStoryScore; }
    public void setAverageStoryScore(double averageStoryScore) { this.averageStoryScore = averageStoryScore; }

    public double getAverageReplayValueScore() { return averageReplayValueScore; }
    public void setAverageReplayValueScore(double averageReplayValueScore) { this.averageReplayValueScore = averageReplayValueScore; }

    public Map<Integer, Long> getOverallScoreHistogram() { return overallScoreHistogram; }
    public void setOverallScoreHistogram(Map<Integer, Long> overallScoreHistogram) { this.overallScoreHistogram = overallScoreHistogram; }
}


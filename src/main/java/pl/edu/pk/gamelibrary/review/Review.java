package pl.edu.pk.gamelibrary.review;

import jakarta.persistence.*;
import pl.edu.pk.gamelibrary.auth.model.AppUser;
import pl.edu.pk.gamelibrary.game.Game;
import pl.edu.pk.gamelibrary.util.RatingCalculator;

import java.time.LocalDateTime;

/**
 * Encja recenzji gry.
 *
 * Zawiera szczegółowe kryteria oceniania stosowane przez większość
 * profesjonalnych serwisów (IGN, Metacritic, GameSpot):
 *  - gameplay     : mechaniki, sterowanie, płynność rozgrywki
 *  - graphics     : oprawa wizualna, szczegółowość, efekty
 *  - sound        : muzyka, efekty dźwiękowe, voice acting
 *  - story        : fabuła, postacie, dialogi (dla gier narracyjnych)
 *  - replayValue  : regrywalność, zawartość, wartość za pieniądze
 *
 * Każde kryterium oceniane jest w skali 1–10.
 * Ocena ogólna (overallScore) jest liczona automatycznie jako ważona średnia.
 */
@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_review_game_user",
                columnNames = {"game_id", "user_id"}
        ))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Gra, której dotyczy recenzja. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    /** Autor recenzji. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser author;

    /** Tytuł / nagłówek recenzji. */
    @Column(nullable = false, length = 200)
    private String title;

    /** Treść recenzji. */
    @Column(nullable = false, length = 5000)
    private String content;

    // ──────────────────────────────────────────────
    // Kryteria oceniania (skala 1–10)
    // ──────────────────────────────────────────────

    /** Rozgrywka – mechaniki, sterowanie, satysfakcja z gry. */
    @Column(nullable = false)
    private Integer gameplayScore;

    /** Oprawa graficzna – wizuale, efekty, design poziomów. */
    @Column(nullable = false)
    private Integer graphicsScore;

    /** Dźwięk – muzyka, efekty dźwiękowe, dubbing. */
    @Column(nullable = false)
    private Integer soundScore;

    /** Fabuła – narracja, postacie, scenariusz. */
    @Column(nullable = true)
    private Integer storyScore;

    /** Regrywalność – zawartość, tryby gry, wartość za cenę. */
    @Column(nullable = false)
    private Integer replayValueScore;

    /**
     * Łączna ocena wyliczana automatycznie jako ważona średnia kryteriów.
     * Wagi: gameplay 30%, graphics 20%, sound 15%, story 20%, replayValue 15%.
     */
    @Column(nullable = false)
    private Double overallScore;

    /** Profil oceniania użyty do wyliczenia overallScore. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RatingProfile ratingProfile = RatingProfile.DEFAULT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Review() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        recalculateOverallScore();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        recalculateOverallScore();
    }

    /**
     * Oblicza ważoną ocenę ogólną na podstawie kryteriów.
     * Wagi odzwierciedlają priorytety typowe dla gier akcji/RPG.
     */
    public void recalculateOverallScore() {
        if (ratingProfile == null) {
            ratingProfile = RatingProfile.DEFAULT;
        }
        // Core kryteria wymagane przez API (fabuła może być N/A => null).
        if (gameplayScore != null && graphicsScore != null
                && soundScore != null && replayValueScore != null) {
            this.overallScore = RatingCalculator.calculateWeightedScore(
                    gameplayScore, graphicsScore, soundScore, storyScore, replayValueScore, ratingProfile
            );
        }
    }

    // ──────────────────────────────────────────────
    // Gettery i settery
    // ──────────────────────────────────────────────

    public Long getId() { return id; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public AppUser getAuthor() { return author; }
    public void setAuthor(AppUser author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getGameplayScore() { return gameplayScore; }
    public void setGameplayScore(Integer gameplayScore) {
        this.gameplayScore = gameplayScore;
    }

    public Integer getGraphicsScore() { return graphicsScore; }
    public void setGraphicsScore(Integer graphicsScore) {
        this.graphicsScore = graphicsScore;
    }

    public Integer getSoundScore() { return soundScore; }
    public void setSoundScore(Integer soundScore) {
        this.soundScore = soundScore;
    }

    public Integer getStoryScore() { return storyScore; }
    public void setStoryScore(Integer storyScore) {
        this.storyScore = storyScore;
    }

    public Integer getReplayValueScore() { return replayValueScore; }
    public void setReplayValueScore(Integer replayValueScore) {
        this.replayValueScore = replayValueScore;
    }

    public Double getOverallScore() { return overallScore; }

    public RatingProfile getRatingProfile() { return ratingProfile; }
    public void setRatingProfile(RatingProfile ratingProfile) { this.ratingProfile = ratingProfile; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

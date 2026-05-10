package pl.edu.pk.gamelibrary.library;

import jakarta.persistence.*;
import pl.edu.pk.gamelibrary.auth.model.AppUser;
import pl.edu.pk.gamelibrary.game.Game;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_games",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_game_user_game",
                columnNames = {"user_id", "game_id"}
        )
)
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LibraryStatus status = LibraryStatus.PLAN_TO_PLAY;

    @Column(nullable = false)
    private boolean favorite = false;

    @Column(nullable = false)
    private boolean owned = false;

    private Integer hoursPlayed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public UserGame() {}

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public LibraryStatus getStatus() { return status; }
    public void setStatus(LibraryStatus status) { this.status = status != null ? status : LibraryStatus.PLAN_TO_PLAY; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public boolean isOwned() { return owned; }
    public void setOwned(boolean owned) { this.owned = owned; }

    public Integer getHoursPlayed() { return hoursPlayed; }
    public void setHoursPlayed(Integer hoursPlayed) { this.hoursPlayed = hoursPlayed; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}


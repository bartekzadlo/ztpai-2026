package pl.edu.pk.gamelibrary.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByGameId(Long gameId);

    List<Review> findByAuthorId(Long authorId);

    boolean existsByGameIdAndAuthorId(Long gameId, Long authorId);

    Optional<Review> findByGameIdAndAuthorId(Long gameId, Long authorId);

    @Query("SELECT AVG(r.overallScore) FROM Review r WHERE r.game.id = :gameId")
    Optional<Double> findAverageOverallScoreByGameId(@Param("gameId") Long gameId);
}

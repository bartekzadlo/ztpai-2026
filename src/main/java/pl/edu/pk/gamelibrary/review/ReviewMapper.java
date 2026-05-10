package pl.edu.pk.gamelibrary.review;

import pl.edu.pk.gamelibrary.review.dto.ReviewRequest;
import pl.edu.pk.gamelibrary.review.dto.ReviewResponse;

public class ReviewMapper {

    public static Review toEntity(ReviewRequest req) {
        Review r = new Review();
        r.setTitle(req.getTitle());
        r.setContent(req.getContent());
        r.setGameplayScore(req.getGameplayScore());
        r.setGraphicsScore(req.getGraphicsScore());
        r.setSoundScore(req.getSoundScore());
        r.setStoryScore(req.getStoryScore());
        r.setReplayValueScore(req.getReplayValueScore());
        r.setRatingProfile(req.getRatingProfile());
        return r;
    }

    public static ReviewResponse toResponse(Review r) {
        ReviewResponse res = new ReviewResponse();
        res.setId(r.getId());
        res.setGameId(r.getGame().getId());
        res.setGameTitle(r.getGame().getTitle());
        res.setAuthorId(r.getAuthor().getId());
        res.setAuthorUsername(r.getAuthor().getUsername());
        res.setTitle(r.getTitle());
        res.setContent(r.getContent());
        res.setGameplayScore(r.getGameplayScore());
        res.setGraphicsScore(r.getGraphicsScore());
        res.setSoundScore(r.getSoundScore());
        res.setStoryScore(r.getStoryScore());
        res.setReplayValueScore(r.getReplayValueScore());
        res.setOverallScore(r.getOverallScore());
        res.setRatingProfile(r.getRatingProfile());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());
        return res;
    }
}

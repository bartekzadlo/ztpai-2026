package pl.edu.pk.gamelibrary.game;

import pl.edu.pk.gamelibrary.game.dto.GameRequest;
import pl.edu.pk.gamelibrary.game.dto.GameResponse;

public class GameMapper {

    public static Game toEntity(GameRequest req) {
        Game g = new Game();
        g.setTitle(req.getTitle());
        g.setDescription(req.getDescription());
        g.setGenres(req.getGenres());
        g.setPlatforms(req.getPlatforms());
        g.setReleaseYear(req.getReleaseYear());
        g.setCoverUrl(req.getCoverUrl());
        // Pola opcjonalne (profil i "czy ma fabułę")
        if (req.getHasStory() != null) {
            g.setHasStory(req.getHasStory());
        }
        g.setDefaultRatingProfile(req.getDefaultRatingProfile());
        return g;
    }

    public static GameResponse toResponse(Game g) {
        GameResponse res = new GameResponse();
        res.setId(g.getId());
        res.setTitle(g.getTitle());
        res.setDescription(g.getDescription());
        res.setGenres(g.getGenres());
        res.setPlatforms(g.getPlatforms());
        res.setReleaseYear(g.getReleaseYear());
        res.setCoverUrl(g.getCoverUrl());
        res.setHasStory(g.isHasStory());
        res.setDefaultRatingProfile(g.getDefaultRatingProfile());
        return res;
    }
}

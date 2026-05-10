package pl.edu.pk.gamelibrary.game;

import pl.edu.pk.gamelibrary.game.dto.GameRequest;
import pl.edu.pk.gamelibrary.game.dto.GameResponse;

public class GameMapper {

    public static Game toEntity(GameRequest req) {
        Game g = new Game();
        g.setTitle(req.getTitle());
        g.setDescription(req.getDescription());
        g.setGenre(req.getGenre());
        g.setPlatform(req.getPlatform());
        g.setReleaseYear(req.getReleaseYear());
        g.setCoverUrl(req.getCoverUrl());
        // Pola opcjonalne (profil i "czy ma fabułę")
        if (req.getHasStory() != null) {
            g.setHasStory(req.getHasStory());
        }
        if (req.getDefaultRatingProfile() != null) {
            g.setDefaultRatingProfile(req.getDefaultRatingProfile());
        }
        return g;
    }

    public static GameResponse toResponse(Game g) {
        GameResponse res = new GameResponse();
        res.setId(g.getId());
        res.setTitle(g.getTitle());
        res.setDescription(g.getDescription());
        res.setGenre(g.getGenre());
        res.setPlatform(g.getPlatform());
        res.setReleaseYear(g.getReleaseYear());
        res.setCoverUrl(g.getCoverUrl());
        res.setHasStory(g.isHasStory());
        res.setDefaultRatingProfile(g.getDefaultRatingProfile());
        return res;
    }
}

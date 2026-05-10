package pl.edu.pk.gamelibrary.review;

/**
 * Profil oceniania gry/recenzji.
 *
 * Profil wpływa na wagi kryteriów oraz na to, czy "storyScore" ma sens/ma być wymagane.
 */
public enum RatingProfile {
    /** Uniwersalny profil dla większości gier. */
    DEFAULT,
    /** Profil dla gier narracyjnych (większy nacisk na fabułę). */
    NARRATIVE,
    /** Profil dla gier nastawionych na rozgrywkę/multiplayer (story zwykle N/A). */
    MULTIPLAYER;

    public boolean isStoryRelevant() {
        return this != MULTIPLAYER;
    }
}

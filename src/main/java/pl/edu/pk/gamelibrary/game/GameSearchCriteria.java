package pl.edu.pk.gamelibrary.game;

public class GameSearchCriteria {
    private String title;
    private String genre;
    private String platform;
    private Integer releaseYearFrom;
    private Integer releaseYearTo;
    private Boolean hasStory;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public Integer getReleaseYearFrom() { return releaseYearFrom; }
    public void setReleaseYearFrom(Integer releaseYearFrom) { this.releaseYearFrom = releaseYearFrom; }

    public Integer getReleaseYearTo() { return releaseYearTo; }
    public void setReleaseYearTo(Integer releaseYearTo) { this.releaseYearTo = releaseYearTo; }

    public Boolean getHasStory() { return hasStory; }
    public void setHasStory(Boolean hasStory) { this.hasStory = hasStory; }
}


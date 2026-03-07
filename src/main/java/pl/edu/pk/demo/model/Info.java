package pl.edu.pk.demo.model;

public class Info {
    private String author;
    private String framework;
    private String version;

    public Info(String author, String framework, String version)
    {
        this.author = author;
        this.framework = framework;
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public String getFramework() {
        return framework;
    }

    public String getVersion() {
        return version;
    }
}

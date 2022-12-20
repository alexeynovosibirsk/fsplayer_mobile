package ru.nazarov.fsplayer;

public class Station {

    private String url;
    private String name;
    private String genre;

    public Station(String url, String name, String genre) {
        this.url = url;
        this.name = name;
        this.genre = genre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

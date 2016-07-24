package com.artemohanjanyan.nest.io;

public class SearchResult {
    private String title;
    private String link;
    private String description;

    public SearchResult(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }
}

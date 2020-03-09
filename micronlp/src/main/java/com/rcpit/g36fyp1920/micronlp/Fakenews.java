package com.rcpit.g36fyp1920.micronlp;

class Fakenews {
    private int id;
    private String data;
    private String link;
    private String tags;
    private int severity;

    Fakenews(int id, String data, String link, String tags, int severity){
        this.id = id;
        this.data = data;
        this.link = link;
        this.tags = tags;
        this.severity = severity;
    }

    Fakenews(String data, String link, String tags, int severity){
        this.data = data;
        this.link = link;
        this.tags = tags;
        this.severity = severity;
    }

    Fakenews() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
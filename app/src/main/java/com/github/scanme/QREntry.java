package com.github.scanme;

public class QREntry {
    final String ID;
    String title, description, imagePath;

    QREntry(String ID, String title, String imagePath) {
        this(ID, title, "", imagePath);
    }
    QREntry(String ID, String title, String description, String imagePath) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title  = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath() {
        this.imagePath = imagePath;
    }
}

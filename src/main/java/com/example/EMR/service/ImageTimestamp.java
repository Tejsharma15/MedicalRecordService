package com.example.EMR.service;

import java.awt.image.BufferedImage;

public class ImageTimestamp {
    private String link;
    private String timestamp;

    public ImageTimestamp( String timestamp,String link) {
        this.link = link;
        this.timestamp = timestamp;
    }

    // getters and setters
    public String getLink() {
        return link;
    }

    public void setImage(String link) {
        this.link = link;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
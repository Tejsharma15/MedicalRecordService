package com.example.EMR.service;

import java.awt.image.BufferedImage;

public class ImageTimestamp {
    private byte[] image;
    private String timestamp;

    public ImageTimestamp(byte[] image, String timestamp) {
        this.image = image;
        this.timestamp = timestamp;
    }

    // getters and setters
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
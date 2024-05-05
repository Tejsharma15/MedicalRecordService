package com.example.EMR.service;

import java.awt.image.BufferedImage;

public class ImageTimestamp {
    private byte[] image;
    private String timestamp;
    private String text;

    public ImageTimestamp(String text,byte[] image, String timestamp) {
        this.image = image;
        this.timestamp = timestamp;
        this.text = text;
    }
    public ImageTimestamp(byte[] image,String timestamp) {
        this.image = image;
        this.timestamp = timestamp;
        this.text = null;
    }
    public ImageTimestamp(String text,String timestamp) {
        this.image = null;
        this.timestamp = timestamp;
        this.text = text;
    }
    public ImageTimestamp(String timestamp){
        this.timestamp = timestamp;
        this.image = null;
        this.text=null;
    }

    // getters and setters
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text=text;
    }
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
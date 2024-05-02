package com.example.EMR.models;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Entity

public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size")
    private long size = 0;

    @Column(name = "hash", nullable = false, unique = true)
    private String hash;

//    @Column(name = "patientId", nullable = false)
//    private UUID patientId;

    public static final int RADIX = 16;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

//    public UUID getPatientId(){ return patientId; }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash() throws NoSuchAlgorithmException {
        String transformedName = this.name + new Date().getTime();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(transformedName.getBytes(StandardCharsets.UTF_8));
        this.hash = new BigInteger(1, messageDigest.digest()).toString(RADIX);
    }

//    public void setPatientId(UUID patientId){ this.patientId = patientId; }
}
package com.example.EMR.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "document")
@Component
public class DocumentStorageProperty {
    private String uploadDirectory;
    public String getUploadDirectory(){
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory){
        this.uploadDirectory = uploadDirectory;
    }
}

package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.models.PublicPrivateId;
import com.example.EMR.repository.PublicPrivateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PublicPrivateService {
    @Autowired
    private final PublicPrivateRepository publicPrivateRepository;

    @Autowired
    public PublicPrivateService(PublicPrivateRepository publicPrivateRepository) {
        this.publicPrivateRepository = publicPrivateRepository;
    }

    public UUID privateIdByPublicId(String publicId) {
        PublicPrivateId publicPrivateId = publicPrivateRepository.findByPublicId(publicId);
        if (publicPrivateId != null) {
            return publicPrivateId.getPrivateId();
        } else {
            throw new ResourceNotFoundException("No entity found with the given public ID: " + publicId);
        }
    }

    public String publicIdByPrivateId(UUID privateId) {
        List<PublicPrivateId> publicPrivateIds = publicPrivateRepository.findAll();
        PublicPrivateId publicPrivateId = publicPrivateIds.stream()
                .filter(id -> id.getPrivateId().equals(privateId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No entity found with the given private ID: " + privateId));
        return publicPrivateId.getPublicId();
    }

    public String savePublicPrivateId(UUID privateId, String type) {
        PublicPrivateId publicPrivateId = new PublicPrivateId();
        publicPrivateId.setType(type);
        publicPrivateId.setPrivateId(privateId);
        publicPrivateRepository.save(publicPrivateId);
        return publicPrivateId.getPublicId();
    }

}

package com.example.EMR.repository;


import com.example.EMR.models.PublicPrivateId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;



public interface PublicPrivateRepository extends JpaRepository<PublicPrivateId, Integer>{

    PublicPrivateId findByPublicId(String publicId);

    PublicPrivateId findByPrivateId(UUID privateId);

}


package com.example.EMR.logging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Logs, Long>{

    List<Logs> findByActorId(String actorId);

    List<Logs> findByUserId(String userId);
    
}

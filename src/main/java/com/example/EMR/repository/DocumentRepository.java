package com.example.EMR.repository;
import com.example.EMR.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DocumentRepository extends JpaRepository<Document, Long> {

}
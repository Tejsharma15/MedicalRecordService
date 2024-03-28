package com.example.EMR.Repository;
import com.example.EMR.Model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DocumentRepository extends JpaRepository<Document, Long> {

}
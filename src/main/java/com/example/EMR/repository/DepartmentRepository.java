package com.example.EMR.repository;

import com.example.EMR.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface DepartmentRepository extends JpaRepository<Department,UUID> {

}
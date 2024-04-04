package com.example.EMR.repository;

import com.example.EMR.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


public interface DepartmentRepository extends JpaRepository<Department,UUID> {

    @Query("SELECT d from Department d where d.departmentId = :departmentId")
    Optional<Department> findDepartmentById(UUID departmentId);
}
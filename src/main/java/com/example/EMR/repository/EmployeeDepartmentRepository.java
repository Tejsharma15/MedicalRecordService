package com.example.EMR.repository;

import com.example.EMR.models.CompositePrimaryKeys.Employee_DepartmentId;
import com.example.EMR.models.Department;
import com.example.EMR.models.Employee_Department;
import com.example.EMR.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDepartmentRepository extends JpaRepository<Employee_Department, Employee_DepartmentId> {
    List<Employee_Department> findEmployeesByDepartment(Department department);

    List<Employee_Department> findDepartmentsByEmployee(User employee);
}

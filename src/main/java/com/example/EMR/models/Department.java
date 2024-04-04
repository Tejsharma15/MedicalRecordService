package com.example.EMR.models;


import com.example.EMR.converter.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@Table(name = "department")
public class Department {
    public Department() {

    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "departmentId", updatable = false, nullable = false)
    private UUID departmentId;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false)
    private String departmentName;

    @Convert(converter = StringCryptoConverter.class)
    @Column(nullable = false)
    private String departmentHead;

    @Column(nullable = false)
    private int noOfDoctors;

    @Column(nullable = false)
    private int noOfNurses;

    @OneToMany(mappedBy = "department", cascade = CascadeType.REMOVE)
    private List<Employee_Department> employeeDepartments;

    @OneToMany(mappedBy = "department", cascade = CascadeType.REMOVE)
    private List<Patient_Department> patientDepartments;

    public Department(String departmentName, String departmentHead, int noOfDoctors, int noOfNurses) {
        this.departmentName = departmentName;
        this.departmentHead = departmentHead;
        this.noOfDoctors = noOfDoctors;
        this.noOfNurses = noOfNurses;
    }

    public Department orElseThrow(Department department) throws Exception {
        if (department == null) {
            throw new Exception("Department not found"); // YourException should be replaced with the appropriate exception type
        }
        return department;
    }
}

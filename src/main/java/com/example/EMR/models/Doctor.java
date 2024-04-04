package com.example.EMR.models;

import java.util.UUID;

import com.example.EMR.models.CompositePrimaryKeys.DoctorId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "doctor")
public class Doctor{
    @EmbeddedId
    private DoctorId id;

    @JoinColumn(name="users", referencedColumnName = "employeeId",nullable = false)
    @MapsId("employeeId")
    private User doctor;

    @ManyToOne
    @JoinColumn(name="department", referencedColumnName = "departmentId",nullable = false)
    @MapsId("departmentId")
    private Department department;

    @Column(name = "departmentId",nullable=false)
    private UUID departmentId;

}
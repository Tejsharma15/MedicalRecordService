package com.example.EMR.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import com.example.EMR.converter.StringCryptoConverter;

import java.util.List;
import java.util.UUID;



@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@Table(name = "patient")
public class Patient
{
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum PatientType {
        INPATIENT, OUTPATIENT
    }

    public enum DischargeStatus {
        Discharged_by_doctor, Discharged_by_nurse
    }

    public Patient(){

    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name="patientId",updatable = false, nullable = false )
    private UUID patientId;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "Name", nullable = false )
    private String name;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "aabhaId", nullable = false )
    private String aabhaId;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "aadharId", nullable = false )
    private String aadharId;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "emailId", nullable = false )
    private String emailId;

    @Column(name = "DateOfBirth", nullable = false )
    private String dateOfBirth;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name="Emergency Contact Number",nullable = false)
    private String emergencyContactNumber;

    @Column(name = "Gender", nullable = false )
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "patientType", nullable = false )
    @Enumerated(EnumType.STRING)
    private PatientType patientType;

    @Column(name = "DischargeStatus")
    @Enumerated(EnumType.STRING)
    private DischargeStatus dischargeStatus;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE)
    private List<Patient_Department> patientDepartments;

}
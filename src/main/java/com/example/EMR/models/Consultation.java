package com.example.EMR.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@Table(name="consultation")
public class Consultation {
    public Consultation() {

    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "consultationId", updatable = false, nullable = false )
    private UUID consultationId;

    //    @MapsId("patientId")
    @ManyToOne
    // @JsonIgnore
    @JoinColumn(name = "patientId", referencedColumnName = "patientId", unique = false)
    private Patient patient;

    //    @MapsId("employeeId")
    @ManyToOne
    // @JsonIgnore
    @JoinColumn(name = "employeeId", referencedColumnName = "employeeId", unique = false)
    private User doctor;


    @Column(name="EMRID", unique = true)
    private String emrId;

    @Column(name="ConsultationStatus",nullable=false)
    private Boolean isActive;
}
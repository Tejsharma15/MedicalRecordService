package com.example.EMR.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "Emr")
public class Emr {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "emrId", nullable = false)
    private UUID emrId;

    @Column(name = "publicEmrId", nullable = false)
    private UUID publicEmrId;
    @Column(name = "patientId", nullable = false)
    private UUID patientId;
    @Column(name = "accessDepartments")
    private String accessDepartments;
    @Column(name = "comments")
    private String comments;
    @Column(name = "lastUpdate", nullable = false)
    private Long lastUpdate;
    @Column(name = "prescription")
    private String prescription;
    @Column(name = "tests")
    private String tests;

    @Column(name = "accessList", nullable = false)
    private String accessList;

}

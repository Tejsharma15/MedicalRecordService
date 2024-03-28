package com.example.EMR.Model;

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
    @Column(name = "accessDepartments", nullable = false)
    private String accessDepartments;
    @Column(name = "comments", nullable = false)
    private String comments;
    @Column(name = "lastUpdate", nullable = false)
    private Long lastUpdate;
    @Column(name = "prescription", nullable = false)
    private String prescription;
    @Column(name = "accessList", nullable = false)
    private String accessList;

}

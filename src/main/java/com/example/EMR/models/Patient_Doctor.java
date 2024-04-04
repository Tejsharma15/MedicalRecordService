package com.example.EMR.models;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DoctorId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "patient_doctor")
public class Patient_Doctor {
    @EmbeddedId
    private Patient_DoctorId id;

    @ManyToOne
    @JoinColumn(name = "patientId", referencedColumnName = "patientId", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "employeeId", referencedColumnName = "employeeId", insertable = false, updatable = false)
    private User doctor;
}

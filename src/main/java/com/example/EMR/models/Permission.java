package com.example.EMR.models;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    DEP_READ("department:read"),
    DEP_UPDATE("department:update"),
    DEP_DELETE("department:delete"),
    DOCTOR_READ("doctor:read"),
    DOCTOR_UPDATE("doctor:update"),
    DOCTOR_CREATE("doctor:create"),
    NURSE_UPDATE("nurse:update"),
    NURSE_READ("nurse:read"),
    NURSE_CREATE("nurse:create"),
    PATIENT_READ("patient:read"),
    PATIENT_UPDATE("patient:update"),
    PATIENT_CREATE("patient:create"),
    PATIENT_DELETE("patient:delete"),
    PRESCRIPTION_READ("prescription:read"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    DESK_READ("desk:read"),
    DESK_UPDATE("desk:update"),
    DESK_CREATE("desk:create"),
    DESK_DELETE("desk:delete"),
    EMR_CREATE("emr:create"),
    EMR_READ("emr:read"),
    EMR_DELETE("emr:delete"),
    EMR_UPDATE("emr:update")
    ;

    @Getter
    private final String permission;
}

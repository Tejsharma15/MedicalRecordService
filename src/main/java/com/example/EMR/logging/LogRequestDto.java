package com.example.EMR.logging;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
public class LogRequestDto {
    private Long id;
    private Timestamp eventDate;
    private String level;
    private String msg;
    private Exception throwable;
    private String actorId;
    private String userId;
}

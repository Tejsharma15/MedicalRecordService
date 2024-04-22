package com.example.EMR.logging;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "logs")
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_date")
    private Timestamp eventDate;

    @Column(name = "level")
    private String level;

    // @Column(name = "logger")
    // private String logger;

    @Column(name = "msg")
    private String msg;

    @Column(name = "throwable")
    private Exception throwable;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "user_id")
    private String userId;

    @PrePersist
    protected void onCreate() {
        eventDate = new Timestamp(System.currentTimeMillis());
    }

    public Logs(String level, String msg, Exception throwable, String actorId, String userId) {
        this.level = level;
        this.msg = msg;
        this.throwable = throwable;
        this.actorId = actorId;
        this.userId = userId;
    }
}

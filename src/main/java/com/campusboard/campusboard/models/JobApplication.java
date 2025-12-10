package com.campusboard.campusboard.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JOB_APPLICATION",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "student_id"}))
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    // ========= RELATIONSHIPS =========

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // ========= FIELDS =========

    @Enumerated(EnumType.STRING)
    private Status status = Status.SUBMITTED;

    private LocalDateTime appliedAt = LocalDateTime.now();

    // ========= ENUM =========
    public enum Status {
        SUBMITTED, ACCEPTED, REJECTED
    }

    // ========= GETTERS & SETTERS ==========

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}

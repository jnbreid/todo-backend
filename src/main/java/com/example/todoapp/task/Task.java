package com.example.todoapp.task;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", updatable = false, nullable = false)
    private UUID publicId;

    private String name;
    private String description;
    private LocalDate deadline;
    private Integer priority;
    private Boolean completed;
    private Long userId;

    public Task() {
    }

    public Task(Long id, UUID publicId, String name, String description, LocalDate deadline, Integer priority, Boolean completed, Long userId) {
        this.id = id;
        this.publicId = publicId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = completed;
        this.userId = userId;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

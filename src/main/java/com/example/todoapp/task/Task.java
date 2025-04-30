package com.example.todoapp.task;

import java.time.LocalDate;

public class Task {

    private Long id;

    private String name;
    private LocalDate deadline;
    private Integer priority;
    private Boolean completed;
    private Long userId;

    public Task() {
    }

    public Task(Long id, String name, LocalDate deadline, Integer priority, Boolean completed, Long userId) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = completed;
        this.userId = userId;
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

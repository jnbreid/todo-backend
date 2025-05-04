package com.example.todoapp.task.controller;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class TaskDTO {
    private Long id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    private int priority;
    private boolean complete;
    private Long userId;

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

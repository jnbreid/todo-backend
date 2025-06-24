/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.task.controller;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public class TaskDTO {
    private UUID publicId;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    private int priority;
    private boolean complete;
    private String userName;

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

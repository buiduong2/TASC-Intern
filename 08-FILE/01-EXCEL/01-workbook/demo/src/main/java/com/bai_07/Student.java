package com.bai_07;

import java.time.LocalDateTime;

public class Student {
    
    private long id;
    private String name;
    private LocalDateTime createdAt;

    private boolean isActive;

    public Student(long id, String name, LocalDateTime createdAt, boolean isActive) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", createdAt=" + createdAt + ", isActive=" + isActive + "]";
    }
}

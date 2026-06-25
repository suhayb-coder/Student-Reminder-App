package com.example.studentremainderapp;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String dueTime;
    private int priority; // 0: Low, 1: Medium, 2: High
    private boolean isCompleted;

    public Task() {}

    public Task(String title, String description, String dueDate, String dueTime, int priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

}
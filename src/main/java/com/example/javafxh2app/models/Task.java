package com.example.javafxh2app.models;


import java.time.LocalDateTime;

public class Task {
    private long id;
    private long userId;
    private String text;
    private boolean status;
    private String priority; // Low, Medium, High
    private LocalDateTime deadline;

    public Task(long id, long userId, String text, boolean status, String priority, LocalDateTime deadline) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
    }

    // геттеры и сеттеры
    public long getId() { return id; }
    public long getUserId() { return userId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    @Override
    public String toString() {
        return (status ? "[✓] " : "[ ] ") + (text == null ? "" : text);
    }
}


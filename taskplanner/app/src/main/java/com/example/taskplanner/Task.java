package com.example.taskplanner;

public class Task {
    public int id;
    public String title;
    public String description;
    public String deadline;
    public String status;

    public Task(int id, String title, String description, String deadline, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }
}

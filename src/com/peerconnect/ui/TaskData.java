package com.peerconnect.ui;

public class TaskData {
    public String taskName;
    public String category;
    public String priority;
    public String dueDate;
    public String description;
    
    public TaskData(String taskName, String category, String priority, String dueDate, String description) {
        this.taskName = taskName;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.description = description;
    }
}
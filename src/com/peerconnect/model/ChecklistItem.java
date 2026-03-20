package com.peerconnect.model;

public class ChecklistItem {
    private int id;
    private String taskDescription;
    private String taskCreator; // <-- ADDED THIS FIELD
    private boolean isCompleted;

    // Constructor for creating a new item
    public ChecklistItem(String taskDescription, String taskCreator, boolean isCompleted) {
        this.taskDescription = taskDescription;
        this.taskCreator = taskCreator;
        this.isCompleted = isCompleted;
    }
    
    // Constructor for loading an item from the database
    public ChecklistItem(int id, String taskDescription, String taskCreator, boolean isCompleted) {
        this.id = id;
        this.taskDescription = taskDescription;
        this.taskCreator = taskCreator;
        this.isCompleted = isCompleted;
    }

    // Getters
    public int getId() { return id; }
    public String getTaskDescription() { return taskDescription; }
    public String getTaskCreator() { return taskCreator; } // <-- ADDED GETTER
    public boolean isCompleted() { return isCompleted; }

    // Setters
    public void setCompleted(boolean completed) { isCompleted = completed; }

    // We no longer need the toString() method; the renderer will handle the text.
}
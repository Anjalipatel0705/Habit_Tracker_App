package com.example.habittracker;

public class Habit {
    private String id;
    private String name;
    private String frequency;
    private boolean completed;

    // Required empty constructor for Firestore
    public Habit() {}

    public Habit(String id, String name, String frequency, boolean completed) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.completed = completed;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getFrequency() { return frequency; }
    public boolean isCompleted() { return completed; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

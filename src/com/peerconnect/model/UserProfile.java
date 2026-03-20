package com.peerconnect.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class UserProfile {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private String major;
    private int collegeYear;
    private String academicStrengths;
    private String subjectsNeedingSupport;
    private List<String> booksAvailable;
    private List<String> booksRequested;
    
    // Study tracking fields
    private int studyStreak;
    private LocalDate lastStudyDate;
    private int totalStudyMinutes;
    private List<String> achievements;
    
    // Leaderboard fields
    private int experiencePoints;
    private int level;

    // This is the ONLY constructor we will use.
    public UserProfile(String username, String password) {
        this.username = username;
        this.password = password;
        // Set sensible defaults for a new user.
        this.email = email;
        this.fullName = "";
        this.major = "";
        this.collegeYear = 1;
        this.academicStrengths = "Not set";
        this.subjectsNeedingSupport = "Not set";
        this.booksAvailable = new ArrayList<>();
        this.booksRequested = new ArrayList<>();
        
        // Initialize study tracking
        this.studyStreak = 0;
        this.lastStudyDate = null;
        this.totalStudyMinutes = 0;
        this.achievements = new ArrayList<>();
        
        // Initialize leaderboard
        this.experiencePoints = 0;
        this.level = 1;
    }
    
    // Getters and Setters for all fields
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getCollegeYear() { return collegeYear; }
    public void setCollegeYear(int collegeYear) { this.collegeYear = collegeYear; }

    public String getAcademicStrengths() { return academicStrengths; }
    public void setAcademicStrengths(String academicStrengths) { this.academicStrengths = academicStrengths; }

    public String getSubjectsNeedingSupport() { return subjectsNeedingSupport; }
    public void setSubjectsNeedingSupport(String subjectsNeedingSupport) { this.subjectsNeedingSupport = subjectsNeedingSupport; }

    public List<String> getBooksAvailable() { return booksAvailable; }
    public void setBooksAvailable(List<String> booksAvailable) { this.booksAvailable = booksAvailable; }

    public List<String> getBooksRequested() { return booksRequested; }
    public void setBooksRequested(List<String> booksRequested) { this.booksRequested = booksRequested; }
    
    // Study tracking getters and setters
    public int getStudyStreak() { return studyStreak; }
    public void setStudyStreak(int studyStreak) { this.studyStreak = studyStreak; }
    
    public LocalDate getLastStudyDate() { return lastStudyDate; }
    public void setLastStudyDate(LocalDate lastStudyDate) { this.lastStudyDate = lastStudyDate; }
    
    public int getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(int totalStudyMinutes) { this.totalStudyMinutes = totalStudyMinutes; }
    
    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }
    
    // Helper methods for study tracking
    public void incrementStudyStreak() {
        this.studyStreak++;
        this.lastStudyDate = LocalDate.now();
    }
    
    public void addStudyMinutes(int minutes) {
        this.totalStudyMinutes += minutes;
    }
    
    public void addAchievement(String achievement) {
        if (!this.achievements.contains(achievement)) {
            this.achievements.add(achievement);
        }
    }
    
    // Leaderboard getters and setters
    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { 
        this.experiencePoints = experiencePoints; 
        updateLevel();
    }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    // Helper methods for XP and leveling
    public void addExperience(int xp) {
        this.experiencePoints += xp;
        updateLevel();
    }
    
    private void updateLevel() {
        // Level formula: level = floor(sqrt(XP/100)) + 1
        this.level = (int) Math.floor(Math.sqrt(this.experiencePoints / 100.0)) + 1;
    }
    
    public int getXpForNextLevel() {
        int nextLevel = this.level + 1;
        return (nextLevel - 1) * (nextLevel - 1) * 100;
    }
    
    public int getXpProgressInCurrentLevel() {
        int currentLevelMinXp = (this.level - 1) * (this.level - 1) * 100;
        return this.experiencePoints - currentLevelMinXp;
    }
    
    public int getXpNeededForCurrentLevel() {
        int nextLevelMinXp = this.level * this.level * 100;
        int currentLevelMinXp = (this.level - 1) * (this.level - 1) * 100;
        return nextLevelMinXp - currentLevelMinXp;
    }
}

package com.peerconnect.manager;

import com.peerconnect.database.DatabaseManager;
import com.peerconnect.model.UserProfile;

public class ProfileManager {

    private DatabaseManager dbManager;

    public ProfileManager() {
        this.dbManager = new DatabaseManager();
    }

    public boolean createAccount(String username, String password, String Email, String fullName, int collegeYear) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        if (dbManager.getUserByUsername(username) != null) {
            return false;
        }
        // Use the correct, simple constructor
        UserProfile newProfile = new UserProfile(username, password);
        newProfile.setEmail(Email);
        newProfile.setFullName(fullName);
        newProfile.setCollegeYear(collegeYear); // Set the year separately
        return dbManager.saveUser(newProfile);
    }
    
    public UserProfile login(String username, String password) {
        UserProfile storedProfile = dbManager.getUserByUsername(username);
        if (storedProfile != null && storedProfile.getPassword().equals(password)) {
            return storedProfile;
        }
        return null;
    }
    
    public boolean saveProfile(UserProfile user) {
        return dbManager.saveUser(user);
    }
}
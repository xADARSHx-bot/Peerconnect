package com.peerconnect.debug;

import com.peerconnect.database.DatabaseManager;
import com.peerconnect.model.UserProfile;
import com.peerconnect.matching.PeerMatchingManager;
import com.peerconnect.model.PeerMatch;
import java.util.List;

public class DatabaseDebug {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        PeerMatchingManager matchingManager = new PeerMatchingManager();
        
        System.out.println("=== DATABASE CONTENTS ===");
        
        // Get all users (we'll use a dummy username to get all others)
        List<UserProfile> allUsers = dbManager.getAllUsersExcept("nonexistent_user");
        
        if (allUsers.isEmpty()) {
            System.out.println("No users found in database!");
            return;
        }
        
        System.out.println("Found " + allUsers.size() + " users:");
        System.out.println();
        
        for (UserProfile user : allUsers) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Academic Strengths: '" + user.getAcademicStrengths() + "'");
            System.out.println("Subjects Needing Support: '" + user.getSubjectsNeedingSupport() + "'");
            System.out.println("Books Available: " + user.getBooksAvailable());
            System.out.println("Books Requested: " + user.getBooksRequested());
            System.out.println("---");
        }
        
        // Test matching for each user
        System.out.println("\n=== PEER MATCHING RESULTS ===");
        
        for (UserProfile currentUser : allUsers) {
            System.out.println("\nMatches for user: " + currentUser.getUsername());
            List<PeerMatch> matches = matchingManager.findPeerMatches(currentUser);
            
            if (matches.isEmpty()) {
                System.out.println("  No matches found");
            } else {
                for (PeerMatch match : matches) {
                    System.out.println("  -> " + match.toString());
                }
            }
        }
    }
}

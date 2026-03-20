package com.peerconnect.matching;

import com.peerconnect.database.DatabaseManager;
import com.peerconnect.model.UserProfile;
import com.peerconnect.model.PeerMatch;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class PeerMatchingManager {
    
    private DatabaseManager dbManager;
    
    public PeerMatchingManager() {
        this.dbManager = new DatabaseManager();
    }
    
    // Main method to find peer matches for a given user
    public List<PeerMatch> findPeerMatches(UserProfile currentUser) {
        List<PeerMatch> matches = new ArrayList<>();
        
        // Get all other users from database
        List<UserProfile> otherUsers = dbManager.getAllUsersExcept(currentUser.getUsername());
        
        if (otherUsers.isEmpty()) {
            return matches; // No other users to match with
        }
        
        // Analyze each user for compatibility
        for (UserProfile otherUser : otherUsers) {
            PeerMatch match = analyzeCompatibility(currentUser, otherUser);
            if (match != null && match.getCompatibilityScore() > 0) {
                matches.add(match);
            }
        }
        
        // Sort matches by compatibility score (highest first)
        Collections.sort(matches, (a, b) -> Double.compare(b.getCompatibilityScore(), a.getCompatibilityScore()));
        
        return matches;
    }
    
    // Core matching algorithm
    private PeerMatch analyzeCompatibility(UserProfile currentUser, UserProfile otherUser) {
        
        // THE CRITICAL NEW RULE:
        // Only proceed if they are in the same year.
        if (currentUser.getCollegeYear() != otherUser.getCollegeYear()) {
            return null;
        }
        
        // Skip users with no academic information
        if (isEmptyProfile(currentUser) || isEmptyProfile(otherUser)) {
            return null;
        }
        
        double compatibilityScore = 0.0;
        List<String> matchReasons = new ArrayList<>();
        String matchType = "ONE_WAY";
        
        // Parse subjects into sets for easier comparison
        Set<String> currentStrengths = parseSubjects(currentUser.getAcademicStrengths());
        Set<String> currentNeeds = parseSubjects(currentUser.getSubjectsNeedingSupport());
        Set<String> otherStrengths = parseSubjects(otherUser.getAcademicStrengths());
        Set<String> otherNeeds = parseSubjects(otherUser.getSubjectsNeedingSupport());
        
        // Calculate compatibility scores
        double currentHelpsOther = calculateHelpScore(currentStrengths, otherNeeds, matchReasons, "You can help with");
        double otherHelpsCurrent = calculateHelpScore(otherStrengths, currentNeeds, matchReasons, "They can help you with");
        
        // Determine match type and overall score
        if (currentHelpsOther > 0 && otherHelpsCurrent > 0) {
            matchType = "MUTUAL";
            compatibilityScore = (currentHelpsOther + otherHelpsCurrent) / 2.0; // Average for mutual matches
        } else if (currentHelpsOther > 0) {
            compatibilityScore = currentHelpsOther * 0.7; // Slightly lower score for one-way matches
        } else if (otherHelpsCurrent > 0) {
            compatibilityScore = otherHelpsCurrent * 0.7;
        }
        
        // Add bonus for complementary subjects (subjects that frequently go together)
        double bonus = calculateComplementaryBonus(currentStrengths, currentNeeds, otherStrengths, otherNeeds);
        compatibilityScore += bonus;
        
        // Cap the score at 1.0
        compatibilityScore = Math.min(compatibilityScore, 1.0);
        
        if (compatibilityScore > 0) {
            return new PeerMatch(otherUser, compatibilityScore, matchReasons, matchType);
        }
        
        return null;
    }
    
    // Calculate how much one user can help another
    private double calculateHelpScore(Set<String> helperStrengths, Set<String> learnerNeeds, 
                                     List<String> matchReasons, String reasonPrefix) {
        if (helperStrengths.isEmpty() || learnerNeeds.isEmpty()) {
            return 0.0;
        }
        
        Set<String> commonSubjects = new HashSet<>(helperStrengths);
        commonSubjects.retainAll(learnerNeeds); // Intersection
        
        if (!commonSubjects.isEmpty()) {
            // Add specific subjects to match reasons
            for (String subject : commonSubjects) {
                matchReasons.add(reasonPrefix + " " + subject);
            }
            
            // Score based on number of matching subjects and total subjects
            double matchRatio = (double) commonSubjects.size() / Math.max(helperStrengths.size(), learnerNeeds.size());
            return Math.min(matchRatio * 0.8, 0.8); // Max 0.8 for direct matches
        }
        
        return 0.0;
    }
    
    // Calculate bonus for complementary academic areas
    private double calculateComplementaryBonus(Set<String> currentStrengths, Set<String> currentNeeds,
                                              Set<String> otherStrengths, Set<String> otherNeeds) {
        // Define complementary subject pairs
        String[][] complementaryPairs = {
            {"math", "physics"}, {"math", "engineering"}, {"math", "computer science"},
            {"biology", "chemistry"}, {"chemistry", "physics"},
            {"history", "english"}, {"history", "political science"},
            {"economics", "statistics"}, {"psychology", "sociology"}
        };
        
        double bonus = 0.0;
        
        for (String[] pair : complementaryPairs) {
            String subject1 = pair[0].toLowerCase();
            String subject2 = pair[1].toLowerCase();
            
            // Check if users have complementary strengths
            if ((hasSubject(currentStrengths, subject1) && hasSubject(otherStrengths, subject2)) ||
                (hasSubject(currentStrengths, subject2) && hasSubject(otherStrengths, subject1))) {
                bonus += 0.1; // Small bonus for complementary subjects
            }
        }
        
        return Math.min(bonus, 0.2); // Cap complementary bonus at 0.2
    }
    
    // Helper method to check if a set contains a subject (case-insensitive)
    private boolean hasSubject(Set<String> subjects, String target) {
        return subjects.stream().anyMatch(subject -> subject.toLowerCase().contains(target));
    }
    
    // Parse comma-separated subjects into a set
    private Set<String> parseSubjects(String subjectsString) {
        Set<String> subjects = new HashSet<>();
        if (subjectsString != null && !subjectsString.trim().isEmpty() && !subjectsString.equals("Not set")) {
            String[] subjectArray = subjectsString.split(",");
            for (String subject : subjectArray) {
                String trimmed = subject.trim();
                if (!trimmed.isEmpty()) {
                    subjects.add(trimmed);
                }
            }
        }
        return subjects;
    }
    
    // Check if a user profile has no useful academic information
    private boolean isEmptyProfile(UserProfile user) {
        return (user.getAcademicStrengths() == null || user.getAcademicStrengths().trim().isEmpty() || user.getAcademicStrengths().equals("Not set")) &&
               (user.getSubjectsNeedingSupport() == null || user.getSubjectsNeedingSupport().trim().isEmpty() || user.getSubjectsNeedingSupport().equals("Not set"));
    }
    
    // Get top N matches
    public List<PeerMatch> getTopMatches(UserProfile currentUser, int maxResults) {
        List<PeerMatch> allMatches = findPeerMatches(currentUser);
        return allMatches.subList(0, Math.min(maxResults, allMatches.size()));
    }
}
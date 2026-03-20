package com.peerconnect.model;

import java.util.List;
import java.util.ArrayList;

public class PeerMatch {
    
    private UserProfile matchedUser;
    private double compatibilityScore; // 0.0 to 1.0, higher means better match
    private List<String> matchReasons; // Specific subjects/areas where they can help each other
    private String matchType; // "MUTUAL" (both can help each other) or "ONE_WAY" (one helps the other)
    
    // Constructor
    public PeerMatch(UserProfile matchedUser, double compatibilityScore) {
        this.matchedUser = matchedUser;
        this.compatibilityScore = compatibilityScore;
        this.matchReasons = new ArrayList<>();
        this.matchType = "ONE_WAY"; // Default
    }
    
    // Constructor with all fields
    public PeerMatch(UserProfile matchedUser, double compatibilityScore, 
                     List<String> matchReasons, String matchType) {
        this.matchedUser = matchedUser;
        this.compatibilityScore = compatibilityScore;
        this.matchReasons = matchReasons != null ? matchReasons : new ArrayList<>();
        this.matchType = matchType;
    }
    
    // Getters and Setters
    public UserProfile getMatchedUser() {
        return matchedUser;
    }
    
    public void setMatchedUser(UserProfile matchedUser) {
        this.matchedUser = matchedUser;
    }
    
    public double getCompatibilityScore() {
        return compatibilityScore;
    }
    
    public void setCompatibilityScore(double compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }
    
    public List<String> getMatchReasons() {
        return matchReasons;
    }
    
    public void setMatchReasons(List<String> matchReasons) {
        this.matchReasons = matchReasons;
    }
    
    public void addMatchReason(String reason) {
        if (!this.matchReasons.contains(reason)) {
            this.matchReasons.add(reason);
        }
    }
    
    public String getMatchType() {
        return matchType;
    }
    
    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }
    
    // Utility methods
    public String getCompatibilityPercentage() {
        return String.format("%.0f%%", compatibilityScore * 100);
    }
    
    public String getMatchReasonsAsString() {
        if (matchReasons.isEmpty()) {
            return "General academic compatibility";
        }
        return String.join(", ", matchReasons);
    }
    
    public boolean isMutualMatch() {
        return "MUTUAL".equals(matchType);
    }
    
    @Override
    public String toString() {
        return String.format("Match with %s (%s compatibility) - %s", 
                           matchedUser.getUsername(), 
                           getCompatibilityPercentage(),
                           getMatchReasonsAsString());
    }
    
    // Compare matches by compatibility score (for sorting)
    public int compareTo(PeerMatch other) {
        return Double.compare(other.compatibilityScore, this.compatibilityScore); // Higher scores first
    }
}

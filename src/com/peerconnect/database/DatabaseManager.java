package com.peerconnect.database;

import com.peerconnect.model.ChecklistItem;
import com.peerconnect.model.UserProfile;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//Databasemanager
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:peerconnect.db";
    public DatabaseManager() {
        try {
            // Ensure SQLite driver is loaded - handle SLF4J dependency gracefully
            Class.forName("org.sqlite.JDBC");
            // Test connection to ensure everything works
            try (Connection testConn = getConnection()) {
                // Connection successful
            }
            createUsersTable();
            migrateUsersTable();
            migrateStudyTrackingFields();
            migrateLeaderboardFields();
            createChecklistTable();
            createCollaborationRequestsTable();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not available", e);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        } catch (Exception e) {
            System.err.println("Unexpected error during database setup: " + e.getMessage());
            e.printStackTrace();
            // For missing SLF4J, we'll provide a more helpful error message
            if (e.getCause() != null && e.getCause().getClass().getName().contains("slf4j")) {
                System.err.println("\nSLF4J logging library is missing. Please add slf4j-simple or slf4j-nop to classpath.");
            }
            throw new RuntimeException("Database setup failed", e);
        }
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    private void createUsersTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT, email TEXT, fullName TEXT, major TEXT, collegeYear INTEGER, academicStrengths TEXT, subjectsNeedingSupport TEXT, booksAvailable TEXT, booksRequested TEXT);";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    private void migrateUsersTable() throws SQLException {
        // Add email, fullName, and active peer columns if they don't exist
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // email column
            try { stmt.execute("ALTER TABLE users ADD COLUMN email TEXT"); } catch (SQLException ignored) {}
            // fullName column
            try { stmt.execute("ALTER TABLE users ADD COLUMN fullName TEXT"); } catch (SQLException ignored) {}
            // major column
            try { stmt.execute("ALTER TABLE users ADD COLUMN major TEXT"); } catch (SQLException ignored) {}
            // active collaboration peer username column
            try { stmt.execute("ALTER TABLE users ADD COLUMN activePeerUsername TEXT"); } catch (SQLException ignored) {}
        }
    }
        private void migrateStudyTrackingFields() throws SQLException {
        // Add study tracking columns if they don't exist
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN studyStreak INTEGER DEFAULT 0");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN lastStudyDate TEXT");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN totalStudyMinutes INTEGER DEFAULT 0");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN achievements TEXT");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
        }
    }
        private void migrateLeaderboardFields() throws SQLException {
        // Add leaderboard columns if they don't exist
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN experiencePoints INTEGER DEFAULT 0");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN level INTEGER DEFAULT 1");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
        }
    }
    private void createChecklistTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS checklists ("
                   + "itemId INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "spaceId TEXT NOT NULL,"
                   + "taskDescription TEXT NOT NULL,"
                   + "taskCreator TEXT NOT NULL," // <-- ADDED THIS LINE
                   + "isCompleted INTEGER DEFAULT 0);";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
        private void createCollaborationRequestsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS collaboration_requests ("
                   + "requestId INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "fromUsername TEXT NOT NULL,"
                   + "toUsername TEXT NOT NULL,"
                   + "status TEXT DEFAULT 'PENDING'," // PENDING, ACCEPTED, DECLINED
                   + "requestMessage TEXT,"
                   + "requestDate TEXT NOT NULL,"
                   + "responseDate TEXT);";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
        // --- USER PROFILE METHODS (UNCHANGED) ---
    // ... (saveUser, getUserByUsername, etc. are the same) ...
    public boolean saveUser(UserProfile user) {
        String sql = "INSERT OR REPLACE INTO users(username, password, email, fullName, major, collegeYear, academicStrengths, subjectsNeedingSupport, booksAvailable, booksRequested, studyStreak, lastStudyDate, totalStudyMinutes, achievements, experiencePoints, level) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getMajor());
            pstmt.setInt(6, user.getCollegeYear());
            pstmt.setString(7, user.getAcademicStrengths());
            pstmt.setString(8, user.getSubjectsNeedingSupport());
            pstmt.setString(9, String.join(",", user.getBooksAvailable()));
            pstmt.setString(10, String.join(",", user.getBooksRequested()));
            pstmt.setInt(11, user.getStudyStreak());
            pstmt.setString(12, user.getLastStudyDate() != null ? user.getLastStudyDate().toString() : null);
            pstmt.setInt(13, user.getTotalStudyMinutes());
            pstmt.setString(14, String.join(",", user.getAchievements()));
            pstmt.setInt(15, user.getExperiencePoints());
            pstmt.setInt(16, user.getLevel());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public UserProfile getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<UserProfile> getAllUsersExcept(String username) {
        List<UserProfile> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username != ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }
    
    public List<UserProfile> getAllUsers() {
        List<UserProfile> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY experiencePoints DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }
        // --- Active collaboration peer helpers ---
    public boolean setActivePeer(String username, String peerUsername) {
        String sql = "UPDATE users SET activePeerUsername = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, peerUsername);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
        public String getActivePeerUsername(String username) {
        String sql = "SELECT activePeerUsername FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("activePeerUsername");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
        private UserProfile extractUserFromResultSet(ResultSet rs) throws SQLException {
        UserProfile user = new UserProfile(rs.getString("username"), rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("fullName"));
        try {
            user.setMajor(rs.getString("major"));
        } catch (SQLException e) {
            user.setMajor(""); // Default for existing users without major
        }
        user.setCollegeYear(rs.getInt("collegeYear"));
        user.setAcademicStrengths(rs.getString("academicStrengths"));
        user.setSubjectsNeedingSupport(rs.getString("subjectsNeedingSupport"));
        String booksAvailableStr = rs.getString("booksAvailable");
        if (booksAvailableStr != null && !booksAvailableStr.isEmpty()) {
            user.setBooksAvailable(new ArrayList<>(Arrays.asList(booksAvailableStr.split(","))));
        }
        String booksRequestedStr = rs.getString("booksRequested");
        if (booksRequestedStr != null && !booksRequestedStr.isEmpty()) {
            user.setBooksRequested(new ArrayList<>(Arrays.asList(booksRequestedStr.split(","))));
        }
        
        // Load study tracking fields (with safe defaults for existing users)
        try {
            user.setStudyStreak(rs.getInt("studyStreak"));
        } catch (SQLException e) {
            user.setStudyStreak(0); // Default for existing users
        }
        
        try {
            String lastStudyDateStr = rs.getString("lastStudyDate");
            if (lastStudyDateStr != null && !lastStudyDateStr.isEmpty()) {
                user.setLastStudyDate(java.time.LocalDate.parse(lastStudyDateStr));
            }
        } catch (SQLException e) {
            // Default null for existing users
        }
        
        try {
            user.setTotalStudyMinutes(rs.getInt("totalStudyMinutes"));
        } catch (SQLException e) {
            user.setTotalStudyMinutes(0); // Default for existing users
        }
        
        try {
            String achievementsStr = rs.getString("achievements");
            if (achievementsStr != null && !achievementsStr.isEmpty()) {
                user.setAchievements(new ArrayList<>(Arrays.asList(achievementsStr.split(","))));
            }
        } catch (SQLException e) {
            // Default empty list for existing users
        }
        
        // Load leaderboard fields (with safe defaults for existing users)
        try {
            user.setExperiencePoints(rs.getInt("experiencePoints"));
        } catch (SQLException e) {
            user.setExperiencePoints(0); // Default for existing users
        }
        
        try {
            user.setLevel(rs.getInt("level"));
        } catch (SQLException e) {
            user.setLevel(1); // Default for existing users
        }
        
        return user;
    }

    // --- CHECKLIST METHODS (UPDATED) ---

    public String getCollaborationSpaceId(String user1, String user2) {
        List<String> users = Arrays.asList(user1, user2);
        Collections.sort(users);
        return String.join("_", users);
    }

    public List<ChecklistItem> getChecklistItems(String spaceId) {
        List<ChecklistItem> items = new ArrayList<>();
        String sql = "SELECT * FROM checklists WHERE spaceId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spaceId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(new ChecklistItem(
                    rs.getInt("itemId"),
                    rs.getString("taskDescription"),
                    rs.getString("taskCreator"), // <-- GET THE CREATOR
                    rs.getInt("isCompleted") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public ChecklistItem addChecklistItem(String spaceId, String taskDescription, String creatorUsername) {
        String sql = "INSERT INTO checklists(spaceId, taskDescription, taskCreator) VALUES(?, ?, ?)"; // <-- ADDED CREATOR
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spaceId);
            pstmt.setString(2, taskDescription);
            pstmt.setString(3, creatorUsername); // <-- SAVE THE CREATOR
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the last inserted row ID using SQLite's last_insert_rowid()
                try (Statement stmt = conn.createStatement(); 
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        return new ChecklistItem(id, taskDescription, creatorUsername, false);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateChecklistItemStatus(int itemId, boolean isCompleted) {
        String sql = "UPDATE checklists SET isCompleted = ? WHERE itemId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, isCompleted ? 1 : 0);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteChecklistItem(int itemId) {
        String sql = "DELETE FROM checklists WHERE itemId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCompletedChecklistItems(String spaceId) {
        String sql = "DELETE FROM checklists WHERE spaceId = ? AND isCompleted = 1";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spaceId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- COLLABORATION REQUEST METHODS ---
    
    public boolean sendCollaborationRequest(String fromUsername, String toUsername, String message) {
        // Check if request already exists
        if (hasExistingRequest(fromUsername, toUsername)) {
            return false; // Request already exists
        }
        
        String sql = "INSERT INTO collaboration_requests(fromUsername, toUsername, requestMessage, requestDate) VALUES(?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromUsername);
            pstmt.setString(2, toUsername);
            pstmt.setString(3, message);
            pstmt.setString(4, java.time.LocalDateTime.now().toString());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean hasExistingRequest(String fromUsername, String toUsername) {
        String sql = "SELECT COUNT(*) FROM collaboration_requests WHERE fromUsername = ? AND toUsername = ? AND status = 'PENDING'";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromUsername);
            pstmt.setString(2, toUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public java.util.List<CollaborationRequest> getPendingRequests(String username) {
        java.util.List<CollaborationRequest> requests = new java.util.ArrayList<>();
        String sql = "SELECT * FROM collaboration_requests WHERE toUsername = ? AND status = 'PENDING' ORDER BY requestDate DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(new CollaborationRequest(
                    rs.getInt("requestId"),
                    rs.getString("fromUsername"),
                    rs.getString("toUsername"),
                    rs.getString("status"),
                    rs.getString("requestMessage"),
                    rs.getString("requestDate"),
                    rs.getString("responseDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public java.util.List<CollaborationRequest> getAcceptedCollaborations(String username) {
        java.util.List<CollaborationRequest> requests = new java.util.ArrayList<>();
        String sql = "SELECT * FROM collaboration_requests WHERE (fromUsername = ? OR toUsername = ?) AND status = 'ACCEPTED' ORDER BY responseDate DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(new CollaborationRequest(
                    rs.getInt("requestId"),
                    rs.getString("fromUsername"),
                    rs.getString("toUsername"),
                    rs.getString("status"),
                    rs.getString("requestMessage"),
                    rs.getString("requestDate"),
                    rs.getString("responseDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public boolean respondToCollaborationRequest(int requestId, String status) {
        String sql = "UPDATE collaboration_requests SET status = ?, responseDate = ? WHERE requestId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, java.time.LocalDateTime.now().toString());
            pstmt.setInt(3, requestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Inner class for collaboration requests
    public static class CollaborationRequest {
        public final int requestId;
        public final String fromUsername;
        public final String toUsername;
        public final String status;
        public final String message;
        public final String requestDate;
        public final String responseDate;
        
        public CollaborationRequest(int requestId, String fromUsername, String toUsername, 
                                  String status, String message, String requestDate, String responseDate) {
            this.requestId = requestId;
            this.fromUsername = fromUsername;
            this.toUsername = toUsername;
            this.status = status;
            this.message = message;
            this.requestDate = requestDate;
            this.responseDate = responseDate;
        }
    }
}

package com.peerconnect.ui;

import com.peerconnect.model.UserProfile;
import com.peerconnect.database.DatabaseManager;
import com.peerconnect.ui.components.RoundedPanels;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private DatabaseManager dbManager;
    private UserProfile currentUser;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    
    public LeaderboardPanel(UserProfile currentUser) {
        this.currentUser = currentUser;
        this.dbManager = new DatabaseManager();
        initializeUI();
        loadLeaderboardData();
    }
    
    private void initializeUI() {
        setTitle("🏆 PeerConnect+ Leaderboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(67, 56, 202),
                    0, getHeight(), new Color(124, 58, 237)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header
        mainPanel.add(createHeaderSection(), BorderLayout.NORTH);
        
        // Leaderboard table
        mainPanel.add(createTableSection(), BorderLayout.CENTER);
        
        // Bottom info
        mainPanel.add(createBottomSection(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderSection() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        
        // Title
        JLabel titleLabel = new JLabel("🏆 Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Compete with fellow students!", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(subtitleLabel);
        header.add(Box.createVerticalStrut(25));
        
        return header;
    }
    
    private JPanel createTableSection() {
        RoundedPanels tablePanel = new RoundedPanels(15, new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create table model
        String[] columnNames = {"Rank", "Username", "Level", "XP"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setRowHeight(50);
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leaderboardTable.setSelectionBackground(new Color(74, 144, 226, 50));
        leaderboardTable.setGridColor(new Color(240, 240, 240));
        leaderboardTable.setShowVerticalLines(false);
        
        // Custom cell renderer
        leaderboardTable.setDefaultRenderer(Object.class, new LeaderboardCellRenderer());
        
        // Column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Rank
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Username (wider since no Progress column)
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Level
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(120); // XP (slightly wider)
        
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }
    
    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);
        
        // Current user stats
        if (currentUser != null) {
            RoundedPanels userStatsPanel = new RoundedPanels(10, new FlowLayout(FlowLayout.CENTER, 15, 10));
            userStatsPanel.setBackground(new Color(255, 255, 255, 200));
            userStatsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
            
            JLabel userLabel = new JLabel("Your Stats:");
            userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            userLabel.setForeground(new Color(60, 60, 60));
            
            JLabel levelLabel = new JLabel("Level " + currentUser.getLevel());
            levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            levelLabel.setForeground(new Color(124, 58, 237));
            
            JLabel xpLabel = new JLabel(currentUser.getExperiencePoints() + " XP");
            xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            xpLabel.setForeground(new Color(60, 60, 60));
            
            // XP Progress bar
            int currentLevelXp = currentUser.getXpProgressInCurrentLevel();
            int totalLevelXp = currentUser.getXpNeededForCurrentLevel();
            JProgressBar xpProgress = new JProgressBar(0, totalLevelXp);
            xpProgress.setValue(currentLevelXp);
            xpProgress.setStringPainted(true);
            xpProgress.setString(currentLevelXp + "/" + totalLevelXp + " XP to next level");
            xpProgress.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            xpProgress.setForeground(new Color(124, 58, 237));
            xpProgress.setPreferredSize(new Dimension(250, 20));
            
            userStatsPanel.add(userLabel);
            userStatsPanel.add(levelLabel);
            userStatsPanel.add(xpLabel);
            userStatsPanel.add(xpProgress);
            
            bottomPanel.add(userStatsPanel);
        }
        
        return bottomPanel;
    }
    
    private void loadLeaderboardData() {
        tableModel.setRowCount(0);
        
        // Get all users and sort by XP
        List<UserProfile> allUsers = dbManager.getAllUsers();
        allUsers.sort((u1, u2) -> Integer.compare(u2.getExperiencePoints(), u1.getExperiencePoints()));
        
        for (int i = 0; i < allUsers.size(); i++) {
            UserProfile user = allUsers.get(i);
            int rank = i + 1;
            
            // Rank display (we'll handle images in the cell renderer)
            String rankDisplay = "#" + rank;
            
            // Username with highlight for current user (bold formatting handled in renderer)
            String usernameDisplay = user.getUsername();
            
            // Level display
            String levelDisplay = "Lv. " + user.getLevel();
            
            // XP display
            String xpDisplay = String.format("%,d XP", user.getExperiencePoints());
            
            Object[] rowData = {rankDisplay, usernameDisplay, levelDisplay, xpDisplay};
            tableModel.addRow(rowData);
        }
    }
    
    private String getLevelAvatar(int level) {
        if (level >= 50) return "👑"; // King/Queen
        if (level >= 25) return "🎓"; // Graduate
        if (level >= 15) return "🏆"; // Trophy
        if (level >= 10) return "⭐"; // Star
        if (level >= 5) return "📚"; // Book
        return "🌱"; // Seedling for beginners
    }
    
    public ImageIcon loadMedalIcon(int rank, int size) {
        try {
            String filename;
            switch (rank) {
                case 1: filename = "1st place.png"; break;
                case 2: filename = "2nd place.png"; break;
                case 3: filename = "3rd place.png"; break;
                default: return null;
            }
            
            java.io.File imageFile = new java.io.File(filename);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                if (originalIcon.getIconWidth() > 0 && originalIcon.getIconHeight() > 0) {
                    // Scale to desired size
                    java.awt.Image scaledImage = originalIcon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load medal image: " + e.getMessage());
        }
        return null;
    }
    
    // Custom cell renderer for the leaderboard table
    private class LeaderboardCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                     boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Center align all content
            setHorizontalAlignment(SwingConstants.CENTER);
            
            // Top 3 rows get special background colors
            if (row == 0) { // 1st place
                setBackground(isSelected ? new Color(255, 215, 0, 100) : new Color(255, 215, 0, 30));
            } else if (row == 1) { // 2nd place
                setBackground(isSelected ? new Color(192, 192, 192, 100) : new Color(192, 192, 192, 30));
            } else if (row == 2) { // 3rd place
                setBackground(isSelected ? new Color(205, 127, 50, 100) : new Color(205, 127, 50, 30));
            } else {
                setBackground(isSelected ? new Color(74, 144, 226, 50) : Color.WHITE);
            }
            
            // Current user row highlight
            String username = (String) table.getValueAt(row, 1); // Username is now column 1
            if (currentUser != null && username.equals(currentUser.getUsername())) {
                setBorder(BorderFactory.createLineBorder(new Color(124, 58, 237), 2));
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setBorder(null);
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            
            // Special formatting for rank column - show medal images for top 3
            if (column == 0 && row < 3) { // Rank column, top 3 positions
                ImageIcon medalIcon = LeaderboardPanel.this.loadMedalIcon(row + 1, 20);
                if (medalIcon != null) {
                    setIcon(medalIcon);
                    setText(""); // Remove text when showing image
                } else {
                    setIcon(null);
                    // Keep original text as fallback
                }
            } else {
                setIcon(null); // Clear icon for non-medal cells
            }
            
            
            return this;
        }
    }
}
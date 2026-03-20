package com.peerconnect.ui;

import com.peerconnect.model.UserProfile;
import com.peerconnect.ui.components.RoundedPanels;
import com.peerconnect.ui.IconLoader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AchievementsPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private UserProfile currentUser;
    
    public AchievementsPanel(UserProfile user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("🏆 Your Achievements - PeerConnect+");
        setSize(900, 700);
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
                    0, 0, new Color(45, 52, 54),
                    0, getHeight(), new Color(99, 110, 114)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header section
        mainPanel.add(createHeaderSection(), BorderLayout.NORTH);
        
        // Achievements grid
        JScrollPane scrollPane = new JScrollPane(createAchievementsGrid());
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderSection() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        
        // Main title
        JLabel titleLabel = new JLabel("🏆 Your Achievements");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle with stats
        List<String> achievements = currentUser.getAchievements();
        String subtitle = String.format("You've earned %d out of 5 badges!", achievements.size());
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress indicator
        JPanel progressPanel = createProgressIndicator(achievements.size(), 5);
        progressPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(subtitleLabel);
        header.add(Box.createVerticalStrut(20));
        header.add(progressPanel);
        header.add(Box.createVerticalStrut(30));
        
        return header;
    }
    
    private JPanel createProgressIndicator(int earned, int total) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);
        
        for (int i = 0; i < total; i++) {
            ImageIcon starIcon = IconLoader.loadIcon("star", 24);
            JLabel star = new JLabel(starIcon);
            // Tint the icon based on earned status
            if (i < earned) {
                // Earned stars - keep golden color
                star.setIcon(starIcon);
            } else {
                // Unearned stars - create grayed out version
                star.setIcon(starIcon);
                star.setEnabled(false); // This grays out the icon
            }
            panel.add(star);
        }
        
        return panel;
    }
    
    private JPanel createAchievementsGrid() {
        JPanel grid = new JPanel();
        grid.setOpaque(false);
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        
        List<String> userAchievements = currentUser.getAchievements();
        
        // Create achievement cards
        grid.add(createAchievementCard("First Steps", "🥇", "Complete your first study session", 
                                     "Study for 15 minutes", userAchievements.contains("First Steps"),
                                     currentUser.getTotalStudyMinutes() >= 15));
                                     
        grid.add(Box.createVerticalStrut(15));
        
        grid.add(createAchievementCard("Study Hour", "⏰", "Dedicated to learning", 
                                     "Study for 1 hour total", userAchievements.contains("Study Hour"),
                                     currentUser.getTotalStudyMinutes() >= 60));
                                     
        grid.add(Box.createVerticalStrut(15));
        
        grid.add(createAchievementCard("Dedicated Learner", "📚", "Serious about education", 
                                     "Study for 10 hours total", userAchievements.contains("Dedicated Learner"),
                                     currentUser.getTotalStudyMinutes() >= 600));
                                     
        grid.add(Box.createVerticalStrut(15));
        
        grid.add(createAchievementCard("Streak Starter", "🔥", "Building good habits", 
                                     "Maintain a 3 day streak", userAchievements.contains("Streak Starter"),
                                     currentUser.getStudyStreak() >= 3));
                                     
        grid.add(Box.createVerticalStrut(15));
        
        grid.add(createAchievementCard("Week Warrior", "👑", "Master of consistency", 
                                     "Maintain a 7 day streak", userAchievements.contains("Week Warrior"),
                                     currentUser.getStudyStreak() >= 7));
        
        return grid;
    }
    
    private JPanel createAchievementCard(String name, String emoji, String description, 
                                       String requirement, boolean earned, boolean qualified) {
        RoundedPanels card = new RoundedPanels(15, new BorderLayout());
        
        // Card styling based on status
        if (earned) {
            // Earned - golden glow
            card.setBackground(new Color(255, 248, 220, 200));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                new EmptyBorder(25, 25, 25, 25)
            ));
        } else if (qualified) {
            // Can be earned - green glow
            card.setBackground(new Color(220, 255, 220, 200));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 175, 80), 2),
                new EmptyBorder(25, 25, 25, 25)
            ));
        } else {
            // Not earned - subtle
            card.setBackground(new Color(255, 255, 255, 150));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(25, 25, 25, 25)
            ));
        }
        
        // Left side - emoji and status
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        emojiLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Status indicator with icon
        JLabel statusLabel;
        if (earned) {
            ImageIcon checkIcon = IconLoader.loadIcon("checkmark", 12);
            statusLabel = new JLabel("EARNED", checkIcon, JLabel.CENTER);
            statusLabel.setForeground(new Color(76, 175, 80));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (qualified) {
            ImageIcon targetIcon = IconLoader.loadIcon("target", 12);
            statusLabel = new JLabel("READY TO CLAIM", targetIcon, JLabel.CENTER);
            statusLabel.setForeground(new Color(255, 152, 0));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            ImageIcon lockIcon = IconLoader.loadIcon("lock", 12);
            statusLabel = new JLabel("LOCKED", lockIcon, JLabel.CENTER);
            statusLabel.setForeground(new Color(149, 165, 166));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        
        leftPanel.add(emojiLabel, BorderLayout.CENTER);
        leftPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Right side - details
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(earned ? new Color(184, 134, 11) : new Color(45, 52, 54));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        descLabel.setForeground(new Color(108, 117, 125));
        
        JLabel reqLabel = new JLabel("Requirement: " + requirement);
        reqLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reqLabel.setForeground(new Color(73, 80, 87));
        
        rightPanel.add(nameLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(descLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(reqLabel);
        
        // Progress bar for non-earned achievements
        if (!earned) {
            rightPanel.add(Box.createVerticalStrut(10));
            rightPanel.add(createProgressBar(name));
        }
        
        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);
        
        // Add shine effect for earned achievements
        if (earned) {
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                        new EmptyBorder(24, 24, 24, 24)
                    ));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                        new EmptyBorder(25, 25, 25, 25)
                    ));
                }
            });
        }
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.CENTER);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        return wrapper;
    }
    
    private JPanel createProgressBar(String achievementName) {
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setForeground(new Color(52, 152, 219));
        progressBar.setBackground(new Color(236, 240, 241));
        
        // Calculate progress based on achievement
        int progress = 0;
        String progressText = "";
        
        switch (achievementName) {
            case "First Steps":
                progress = Math.min(100, (currentUser.getTotalStudyMinutes() * 100) / 15);
                progressText = currentUser.getTotalStudyMinutes() + "/15 minutes";
                break;
            case "Study Hour":
                progress = Math.min(100, (currentUser.getTotalStudyMinutes() * 100) / 60);
                progressText = currentUser.getTotalStudyMinutes() + "/60 minutes";
                break;
            case "Dedicated Learner":
                progress = Math.min(100, (currentUser.getTotalStudyMinutes() * 100) / 600);
                progressText = String.format("%.1f/10 hours", currentUser.getTotalStudyMinutes() / 60.0);
                break;
            case "Streak Starter":
                progress = Math.min(100, (currentUser.getStudyStreak() * 100) / 3);
                progressText = currentUser.getStudyStreak() + "/3 days";
                break;
            case "Week Warrior":
                progress = Math.min(100, (currentUser.getStudyStreak() * 100) / 7);
                progressText = currentUser.getStudyStreak() + "/7 days";
                break;
        }
        
        progressBar.setValue(progress);
        progressBar.setString(progressText + " (" + progress + "%)");
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        return progressPanel;
    }
}
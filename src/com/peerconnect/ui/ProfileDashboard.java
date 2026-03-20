package com.peerconnect.ui;

import com.peerconnect.manager.ProfileManager;
import com.peerconnect.model.UserProfile;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;
import com.peerconnect.database.DatabaseManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.Timer;
import java.io.File;
import java.util.List;


public class ProfileDashboard extends JPanel {
    private static final long serialVersionUID = 1L;

    // UI Components
    private JTextField strengthsField;
    private JTextField subjectsNeededField;
    private JTextField majorField;
    private JTextField emailField;
    private JLabel welcomeLabel;
    private JLabel lastUpdatedLabel;
    
    // Study stats labels
    private JLabel streakValueLabel;
    private JLabel studyTimeValueLabel;
    private JLabel achievementsValueLabel;
    
    // Emoji labels for animation
    private JLabel streakEmojiLabel;
    private JLabel achievementsEmojiLabel;
    
    // Rotating icons
    private RotatingEmojiIcon streakIcon;
    private RotatingEmojiIcon trophyIcon;
    
    // Animation timers (rotation only, no layout changes)
    private Timer streakSpinTimer;
    private Timer trophySpinTimer;
    
    // Buttons
    private CustomButtons saveButton;
    private CustomButtons findPartnersButton;
    private CustomButtons viewChecklistButton;
    private CustomButtons peerTasksButton;
    private CustomButtons bookExchangeButton;
    private CustomButtons logoutButton;
    
    // Notification components
    private JButton notificationBell;
    private JLabel notificationDot;

    // Data and dependencies
    private ProfileManager profileManager;
    private MainApplication parentFrame;
    private UserProfile currentUser;
    private DatabaseManager dbManager;

    public ProfileDashboard(ProfileManager profileManager, MainApplication parentFrame) {
        this.profileManager = profileManager;
        this.parentFrame = parentFrame;
        this.dbManager = new DatabaseManager();
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create main scroll pane for the content
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Start emoji animations after UI is built
        initEmojiAnimations();
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Header Section
        mainPanel.add(createHeaderSection());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Profile Information Cards
        mainPanel.add(createProfileInfoSection());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Academic Information Cards
        mainPanel.add(createAcademicSection());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Study Statistics Section
        mainPanel.add(createStudyStatsSection());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Learning Hub Section (replaces Books section)
        mainPanel.add(createLearningHubSection());
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Action Buttons
        mainPanel.add(createButtonSection());
        
        return mainPanel;
    }
    
    private JPanel createHeaderSection() {
        RoundedPanels headerCard = new RoundedPanels(12, new BorderLayout());
        headerCard.setBackground(Theme.PRIMARY_COLOR);
        headerCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        welcomeLabel = new JLabel("Welcome to PeerConnect+");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        lastUpdatedLabel = new JLabel("Complete your profile to find study partners");
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lastUpdatedLabel.setForeground(new Color(240, 248, 255));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lastUpdatedLabel);
        
        // Create notification bell panel
        JPanel notificationPanel = createNotificationPanel();
        
        headerCard.add(textPanel, BorderLayout.WEST);
        headerCard.add(notificationPanel, BorderLayout.EAST);
        
        return headerCard;
    }
    
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new OverlayLayout(panel));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(50, 50));
        
        // Bell button
        notificationBell = new JButton("🔔");
        notificationBell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        notificationBell.setBorderPainted(false);
        notificationBell.setContentAreaFilled(false);
        notificationBell.setFocusPainted(false);
        notificationBell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBell.setForeground(Color.WHITE);
        notificationBell.setPreferredSize(new Dimension(40, 40));
        notificationBell.setAlignmentX(Component.CENTER_ALIGNMENT);
        notificationBell.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // Red notification dot
        notificationDot = new JLabel("●");
        notificationDot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notificationDot.setForeground(new Color(255, 59, 48)); // iOS red
        notificationDot.setHorizontalAlignment(SwingConstants.RIGHT);
        notificationDot.setVerticalAlignment(SwingConstants.TOP);
        notificationDot.setAlignmentX(1.0f);
        notificationDot.setAlignmentY(0.0f);
        notificationDot.setVisible(false); // Hidden by default
        
        // Add hover effect
        notificationBell.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                notificationBell.setForeground(new Color(220, 220, 220));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                notificationBell.setForeground(Color.WHITE);
            }
        });
        
        // Click handler
        notificationBell.addActionListener(e -> openNotificationPanel());
        
        panel.add(notificationBell);
        panel.add(notificationDot);
        
        return panel;
    }
    
    private void updateNotificationStatus() {
        if (currentUser != null) {
            List<DatabaseManager.CollaborationRequest> pendingRequests = dbManager.getPendingRequests(currentUser.getUsername());
            boolean hasNotifications = !pendingRequests.isEmpty();
            notificationDot.setVisible(hasNotifications);
        }
    }
    
    private void openNotificationPanel() {
        JDialog notificationDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Notifications", true);
        notificationDialog.setSize(500, 400);
        notificationDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Notifications");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        
        // Notifications list
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setBackground(Theme.BACKGROUND_COLOR);
        
        if (currentUser != null) {
            List<DatabaseManager.CollaborationRequest> requests = dbManager.getPendingRequests(currentUser.getUsername());
            
            if (requests.isEmpty()) {
                JLabel emptyLabel = new JLabel("<html><center>No new notifications<br><span style='color: gray;'>You're all caught up!</span></center></html>");
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                emptyLabel.setForeground(Theme.TEXT_SECONDARY);
                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                emptyLabel.setBorder(new EmptyBorder(50, 20, 50, 20));
                notificationsPanel.add(emptyLabel);
            } else {
                for (DatabaseManager.CollaborationRequest request : requests) {
                    JPanel requestCard = createCollaborationRequestCard(request, notificationDialog);
                    notificationsPanel.add(requestCard);
                    notificationsPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Close button
        CustomButtons closeButton = new CustomButtons("Close", CustomButtons.ButtonType.SECONDARY);
        closeButton.addActionListener(e -> notificationDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        notificationDialog.add(mainPanel);
        notificationDialog.setVisible(true);
    }
    
    private JPanel createCollaborationRequestCard(DatabaseManager.CollaborationRequest request, JDialog parentDialog) {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Header with username and time
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel usernameLabel = new JLabel("Collaboration Request from " + request.fromUsername);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        usernameLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel timeLabel = new JLabel("Just now"); // You could format the actual date
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(Theme.TEXT_SECONDARY);
        
        headerPanel.add(usernameLabel, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);
        
        // Message
        JLabel messageLabel = new JLabel("<html><div style='width: 300px;'>" + 
            (request.message != null ? request.message : "Would like to collaborate with you!") + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Theme.TEXT_SECONDARY);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        CustomButtons acceptButton = new CustomButtons("Accept", CustomButtons.ButtonType.SUCCESS);
        CustomButtons declineButton = new CustomButtons("Decline", CustomButtons.ButtonType.WARNING);
        
        acceptButton.setPreferredSize(new Dimension(80, 30));
        declineButton.setPreferredSize(new Dimension(80, 30));
        
        acceptButton.addActionListener(e -> {
            dbManager.respondToCollaborationRequest(request.requestId, "ACCEPTED");
            // Set as active peer for quick access
            dbManager.setActivePeer(currentUser.getUsername(), request.fromUsername);
            
            // Ask to start collaborating now
            int choice = JOptionPane.showConfirmDialog(parentDialog,
                    "Collaboration request accepted!\n\nStart collaborating with " + request.fromUsername + " now?",
                    "Collaboration Started",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            
            parentDialog.dispose();
            updateNotificationStatus(); // Refresh notification badge
            
            if (choice == JOptionPane.YES_OPTION) {
                // Open shared checklist with the accepted peer
                com.peerconnect.model.UserProfile peer = dbManager.getUserByUsername(request.fromUsername);
                if (peer != null) {
                    parentFrame.showChecklistPanel(currentUser, peer);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not open collaboration: user not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        declineButton.addActionListener(e -> {
            dbManager.respondToCollaborationRequest(request.requestId, "DECLINED");
            JOptionPane.showMessageDialog(parentDialog, "Collaboration request declined.");
            parentDialog.dispose();
            updateNotificationStatus(); // Refresh notification badge
        });
        
        buttonPanel.add(declineButton);
        buttonPanel.add(acceptButton);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(messageLabel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createProfileInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        // Section title
        JLabel sectionTitle = new JLabel("Personal Information");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(Theme.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(sectionTitle);
        
        // Create a grid for the fields
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        gridPanel.setOpaque(false);
        
        gridPanel.add(createFieldCard("Email Address", emailField = Theme.createTextField()));
        gridPanel.add(createFieldCard("Major", majorField = Theme.createTextField()));
        
        section.add(gridPanel);
        return section;
    }
    
    private JPanel createAcademicSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        // Section title
        JLabel sectionTitle = new JLabel("Academic Profile");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(Theme.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(sectionTitle);
        
        // Create a grid for the fields
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        gridPanel.setOpaque(false);
        
        gridPanel.add(createFieldCard("Academic Strengths", strengthsField = Theme.createTextField()));
        gridPanel.add(createFieldCard("Subjects Needing Support", subjectsNeededField = Theme.createTextField()));
        
        section.add(gridPanel);
        return section;
    }
    
    private JPanel createStudyStatsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        // Section title
        JLabel sectionTitle = new JLabel("Study Statistics");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(Theme.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(sectionTitle);
        
        // Create a grid for the stats cards
        JPanel gridPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        gridPanel.setOpaque(false);
        
        // Study Streak Card
        gridPanel.add(createStatsCard("Study Streak", "🔥", streakValueLabel = new JLabel("0 days", JLabel.CENTER), "Keep it up!"));
        
        // Total Study Time Card (custom with button)
        gridPanel.add(createStudyTimeCard());
        
        // Achievements Card (custom with button)
        gridPanel.add(createAchievementsCard());
        
        section.add(gridPanel);
        return section;
    }
    
    private RoundedPanels createStatsCard(String title, String emoji, JLabel valueLabel, String description) {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 15, 20, 15)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Emoji and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        headerPanel.setOpaque(false);
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        // Keep references for animation based on card title
        if ("Study Streak".equals(title)) {
            streakEmojiLabel = emojiLabel;
            // Try to load animated GIF, fallback to rotating hourglass
            ImageIcon animatedIcon = loadAnimatedGif("patapon-fire-resized.gif", 24, 24);
            if (animatedIcon != null) {
                emojiLabel.setText("");
                emojiLabel.setIcon(animatedIcon);
                // Ensure animation repaints via label
                Icon ic = emojiLabel.getIcon();
                if (ic instanceof ImageIcon) {
                    ((ImageIcon) ic).setImageObserver(emojiLabel);
                }
            } else {
                // Fallback to rotating hourglass icon
                streakIcon = new RotatingEmojiIcon("⏳", 24);
                emojiLabel.setText("");
                emojiLabel.setIcon(streakIcon);
            }
        } else if ("Study Time".equals(title)) {
            // Try to load animated GIF for Study Time, fallback to emoji
            ImageIcon timeAnimatedIcon = loadAnimatedGif("clock-ezgif.com-resize.gif", 24, 24);
            if (timeAnimatedIcon != null) {
                emojiLabel.setText("");
                emojiLabel.setIcon(timeAnimatedIcon);
                // Ensure animation repaints via label
                Icon ic = emojiLabel.getIcon();
                if (ic instanceof ImageIcon) {
                    ((ImageIcon) ic).setImageObserver(emojiLabel);
                }
            } else {
                // Keep original emoji if GIF not found
                emojiLabel.setText(emoji);
            }
        } else if ("Achievements".equals(title)) {
            achievementsEmojiLabel = emojiLabel;
            // Try to load animated GIF for Achievements, fallback to rotating trophy
            ImageIcon achievementAnimatedIcon = loadAnimatedGif("trophy-ezgif.com-speed.gif", 24, 24);
            if (achievementAnimatedIcon != null) {
                emojiLabel.setText("");
                emojiLabel.setIcon(achievementAnimatedIcon);
                // Ensure animation repaints via label
                Icon ic = emojiLabel.getIcon();
                if (ic instanceof ImageIcon) {
                    ((ImageIcon) ic).setImageObserver(emojiLabel);
                }
            } else {
                // Fallback to rotating trophy icon
                trophyIcon = new RotatingEmojiIcon("🏆", 24);
                emojiLabel.setText("");
                emojiLabel.setIcon(trophyIcon);
            }
        }
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        headerPanel.add(emojiLabel);
        headerPanel.add(titleLabel);
        
        // Value (using passed label)
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Theme.PRIMARY_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel(description, JLabel.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Theme.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(descLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private RoundedPanels createStudyTimeCard() {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 15, 20, 15)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Emoji and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        headerPanel.setOpaque(false);
        
        JLabel emojiLabel = new JLabel("⏱️");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        // Try to load animated GIF for Study Time, fallback to emoji
        ImageIcon timeAnimatedIcon = loadAnimatedGif("clock-ezgif.com-resize.gif", 24, 24);
        if (timeAnimatedIcon != null) {
            emojiLabel.setText("");
            emojiLabel.setIcon(timeAnimatedIcon);
            // Ensure animation repaints via label
            Icon ic = emojiLabel.getIcon();
            if (ic instanceof ImageIcon) {
                ((ImageIcon) ic).setImageObserver(emojiLabel);
            }
        } else {
            // Keep original emoji if GIF not found
            emojiLabel.setText("⏱️");
        }
        
        JLabel titleLabel = new JLabel("Study Time");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        headerPanel.add(emojiLabel);
        headerPanel.add(titleLabel);
        
        // Value
        studyTimeValueLabel = new JLabel("0 hours", JLabel.CENTER);
        studyTimeValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        studyTimeValueLabel.setForeground(Theme.PRIMARY_COLOR);
        studyTimeValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Log Study button instead of description
        JButton logStudyBtn = new JButton("Log Study Time");
        logStudyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logStudyBtn.setForeground(Color.WHITE);
        logStudyBtn.setBackground(Theme.SUCCESS_COLOR);
        logStudyBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        logStudyBtn.setFocusPainted(false);
        logStudyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logStudyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logStudyBtn.addActionListener(e -> logStudyTime());
        
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(studyTimeValueLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(logStudyBtn);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private RoundedPanels createAchievementsCard() {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 15, 20, 15)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Emoji and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        headerPanel.setOpaque(false);
        
        JLabel emojiLabel = new JLabel("🏆");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        achievementsEmojiLabel = emojiLabel;
        
        // Try to load animated GIF for Achievements, fallback to rotating trophy
        ImageIcon achievementAnimatedIcon = loadAnimatedGif("trophy-ezgif.com-speed.gif", 24, 24);
        if (achievementAnimatedIcon != null) {
            emojiLabel.setText("");
            emojiLabel.setIcon(achievementAnimatedIcon);
            // Ensure animation repaints via label
            Icon ic = emojiLabel.getIcon();
            if (ic instanceof ImageIcon) {
                ((ImageIcon) ic).setImageObserver(emojiLabel);
            }
        } else {
            // Fallback to rotating trophy icon
            trophyIcon = new RotatingEmojiIcon("🏆", 24);
            emojiLabel.setText("");
            emojiLabel.setIcon(trophyIcon);
        }
        
        JLabel titleLabel = new JLabel("Achievements");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        headerPanel.add(emojiLabel);
        headerPanel.add(titleLabel);
        
        // Value
        achievementsValueLabel = new JLabel("0 earned", JLabel.CENTER);
        achievementsValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        achievementsValueLabel.setForeground(Theme.PRIMARY_COLOR);
        achievementsValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // View Achievements button instead of description
        JButton viewAchievementsBtn = new JButton("View Achievements");
        viewAchievementsBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewAchievementsBtn.setForeground(Color.WHITE);
        viewAchievementsBtn.setBackground(new Color(255, 193, 7)); // Warning color (yellow/orange)
        viewAchievementsBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        viewAchievementsBtn.setFocusPainted(false);
        viewAchievementsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAchievementsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewAchievementsBtn.addActionListener(e -> showAchievements());
        
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(achievementsValueLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(viewAchievementsBtn);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createLearningHubSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);

        // Section title
        JLabel sectionTitle = new JLabel("Learning Hub");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(Theme.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(sectionTitle);

        // Two-column grid for creative, education-related content
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        gridPanel.setOpaque(false);

        // Top 3 Leaderboard card
        gridPanel.add(createTop3LeaderboardCard());

        // Resources card with buttons that open links
        JPanel resourcesCard = new RoundedPanels(8, new BorderLayout());
        resourcesCard.setBackground(Theme.CARD_BACKGROUND);
        resourcesCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel resourcesLabel = new JLabel("Learning Resources");
        resourcesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resourcesLabel.setForeground(Theme.TEXT_PRIMARY);
        resourcesCard.add(resourcesLabel, BorderLayout.NORTH);

        JPanel linksPanel = new JPanel();
        linksPanel.setOpaque(false);
        linksPanel.setLayout(new GridLayout(4, 1, 8, 8));

        linksPanel.add(createLinkButton("Khan Academy", "https://www.khanacademy.org"));
        linksPanel.add(createLinkButton("MIT OpenCourseWare", "https://ocw.mit.edu"));
        linksPanel.add(createLinkButton("Coursera", "https://www.coursera.org"));
        linksPanel.add(createLinkButton("Brilliant", "https://brilliant.org"));

        resourcesCard.add(linksPanel, BorderLayout.CENTER);
        gridPanel.add(resourcesCard);

        section.add(gridPanel);
        return section;
    }

    private JButton createLinkButton(String label, String url) {
        JButton btn = new JButton(label);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unable to open link.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return btn;
    }
    
    private JPanel createButtonSection() {
        RoundedPanels buttonCard = new RoundedPanels(12, new BorderLayout());
        buttonCard.setBackground(Theme.CARD_BACKGROUND);
        buttonCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        buttonCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        saveButton = new CustomButtons("Save Profile", CustomButtons.ButtonType.PRIMARY);
        // Widen Save Profile button so text fits comfortably
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setMinimumSize(new Dimension(150, 40));
        saveButton.setMaximumSize(new Dimension(170, 40));
        findPartnersButton = new CustomButtons("Find Peers", CustomButtons.ButtonType.SUCCESS);
        viewChecklistButton = new CustomButtons("Personal Tasks", CustomButtons.ButtonType.SECONDARY);
        peerTasksButton = new CustomButtons("Peer Tasks", CustomButtons.ButtonType.SECONDARY);
        bookExchangeButton = new CustomButtons("Book Exchange", CustomButtons.ButtonType.WARNING);
        // Widen Find Peers button
        findPartnersButton.setPreferredSize(new Dimension(130, 40));
        findPartnersButton.setMinimumSize(new Dimension(130, 40));
        findPartnersButton.setMaximumSize(new Dimension(150, 40));
        // Widen Personal Tasks button
        viewChecklistButton.setPreferredSize(new Dimension(140, 40));
        viewChecklistButton.setMinimumSize(new Dimension(140, 40));
        viewChecklistButton.setMaximumSize(new Dimension(160, 40));
        // Widen Peer Tasks button
        peerTasksButton.setPreferredSize(new Dimension(120, 40));
        peerTasksButton.setMinimumSize(new Dimension(120, 40));
        peerTasksButton.setMaximumSize(new Dimension(140, 40));
        // Widen Book Exchange button
        bookExchangeButton.setPreferredSize(new Dimension(150, 40));
        bookExchangeButton.setMinimumSize(new Dimension(150, 40));
        bookExchangeButton.setMaximumSize(new Dimension(170, 40));
        logoutButton = new CustomButtons("Logout", CustomButtons.ButtonType.DANGER);
        
        // Buttons will auto-size to fit text content
        
        buttonPanel.add(saveButton);
        buttonPanel.add(findPartnersButton);
        buttonPanel.add(viewChecklistButton);
        buttonPanel.add(peerTasksButton);
        buttonPanel.add(bookExchangeButton);
        
        // Add leaderboard button
        CustomButtons leaderboardButton = new CustomButtons("Leaderboard", CustomButtons.ButtonType.WARNING);
        leaderboardButton.setPreferredSize(new Dimension(130, 40));
        leaderboardButton.addActionListener(e -> showLeaderboard());
        buttonPanel.add(leaderboardButton);
        
        buttonPanel.add(logoutButton);
        
        buttonCard.add(buttonPanel, BorderLayout.CENTER);
        
        return buttonCard;
    }

    private RoundedPanels createFieldCard(String labelText, JTextField field) {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.TEXT_PRIMARY);
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus listeners for enhanced UX
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        card.add(label, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.add(field, BorderLayout.CENTER);
        card.add(fieldPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private RoundedPanels createTextAreaCard(String labelText, JTextArea area) {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.TEXT_PRIMARY);
        
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus listeners for enhanced UX
        area.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                area.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                area.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        card.add(label, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        
        JPanel areaPanel = new JPanel(new BorderLayout());
        areaPanel.setOpaque(false);
        areaPanel.add(scrollPane, BorderLayout.CENTER);
        card.add(areaPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveCurrentProfile());
        
        findPartnersButton.addActionListener(e -> {
            if (isProfileComplete()) {
                parentFrame.showPeerMatchingPanel(currentUser);
            } else {
                showProfileIncompleteDialog();
            }
        });
        
        viewChecklistButton.addActionListener(e -> {
            // Show personal checklist (no collaboration partner)
            parentFrame.showChecklistPanel(currentUser, null);
        });
        
        peerTasksButton.addActionListener(e -> {
            // Get all accepted collaboration requests using the new method
            java.util.List<DatabaseManager.CollaborationRequest> accepted = dbManager.getAcceptedCollaborations(currentUser.getUsername());

            if (accepted.isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "No active collaborations found. Find peers now?", "Peer Tasks", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) parentFrame.showPeerMatchingPanel(currentUser);
            } else if (accepted.size() == 1) {
                // Single collaboration - open directly
                String peer = accepted.get(0).fromUsername.equals(currentUser.getUsername()) ?
                    accepted.get(0).toUsername : accepted.get(0).fromUsername;
                com.peerconnect.model.UserProfile peerUser = dbManager.getUserByUsername(peer);
                if (peerUser != null) parentFrame.showChecklistPanel(currentUser, peerUser);
            } else {
                // Multiple collaborations - show chooser
                showCollaborationChooser(accepted);
            }
        });
        
        bookExchangeButton.addActionListener(e -> {
            parentFrame.showBookechangePanel(currentUser);
        });
        
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                parentFrame.showPanel("Login");
            }
        });
    }

    private boolean isProfileComplete() {
        String strengths = strengthsField.getText().trim();
        String subjectsNeeded = subjectsNeededField.getText().trim();
        return !strengths.isEmpty() || !subjectsNeeded.isEmpty();
    }
    
    private void showProfileIncompleteDialog() {
        JOptionPane.showMessageDialog(
            this,
            "Please fill in your Academic Strengths or Subjects Needing Support to find study partners.",
            "Profile Incomplete",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    private void saveCurrentProfile() {
        try {
            String strengths = strengthsField.getText().trim();
            String subjectsNeeded = subjectsNeededField.getText().trim();
            String email = emailField.getText().trim();
            String major = majorField.getText().trim();
            
            // Update user profile
            currentUser.setEmail(email);
            currentUser.setMajor(major);
            currentUser.setAcademicStrengths(strengths.isEmpty() ? "Not set" : strengths);
            currentUser.setSubjectsNeedingSupport(subjectsNeeded.isEmpty() ? "Not set" : subjectsNeeded);
            
            // Books handled in dedicated Book Exchange — nothing to save here
            
            // Save to profile manager
            profileManager.saveProfile(currentUser);
            
            // Update welcome message to show last saved time
            updateLastSavedLabel();
            
            // Show success message
            JOptionPane.showMessageDialog(
                this, 
                "Profile saved successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "An error occurred while saving your profile. Please try again.",
                "Save Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.err.println("Profile save error: " + e.getMessage());
        }
    }
    
    private void updateLastSavedLabel() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        lastUpdatedLabel.setText("Profile last updated: " + now.format(formatter));
    }

    public void setUser(UserProfile user) {
        this.currentUser = user;
        
        // Update welcome message with user's name
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        
        // Populate form fields
        strengthsField.setText(user.getAcademicStrengths().equals("Not set") ? "" : user.getAcademicStrengths());
        subjectsNeededField.setText(user.getSubjectsNeedingSupport().equals("Not set") ? "" : user.getSubjectsNeedingSupport());
        
        // Set email and major if available
        emailField.setText(user.getEmail() != null ? user.getEmail() : "");
        majorField.setText(user.getMajor() != null ? user.getMajor() : "");
        
        // Books are managed in the Book Exchange panel, not shown here
        
        // Update study statistics
        updateStudyStats();
        
        // Update notification status
        updateNotificationStatus();
        
        // Update the status message based on profile completeness
        if (isProfileComplete()) {
            lastUpdatedLabel.setText("Profile ready - you can now find study partners!");
        } else {
            lastUpdatedLabel.setText("Complete your profile to find study partners");
        }
    }
    
    private void updateStudyStats() {
        if (currentUser == null) return;
        
        // Update study streak
        int streak = currentUser.getStudyStreak();
        if (streak == 0) {
            streakValueLabel.setText("0 days");
        } else if (streak == 1) {
            streakValueLabel.setText("1 day");
        } else {
            streakValueLabel.setText(streak + " days");
        }
        
        // Update total study time (convert minutes to hours)
        int totalMinutes = currentUser.getTotalStudyMinutes();
        if (totalMinutes == 0) {
            studyTimeValueLabel.setText("0 hours");
        } else if (totalMinutes < 60) {
            studyTimeValueLabel.setText(totalMinutes + "m");
        } else {
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            if (minutes == 0) {
                studyTimeValueLabel.setText(hours + " hours");
            } else {
                studyTimeValueLabel.setText(hours + "h " + minutes + "m");
            }
        }
        
        // Update achievements count
        int achievementCount = currentUser.getAchievements().size();
        if (achievementCount == 0) {
            achievementsValueLabel.setText("0 earned");
        } else if (achievementCount == 1) {
            achievementsValueLabel.setText("1 earned");
        } else {
            achievementsValueLabel.setText(achievementCount + " earned");
        }
        
        // Update animations based on current stats
        updateAnimations();
    }
    
    private void logStudyTime() {
        if (currentUser == null) return;
        
        String[] options = {"15 minutes", "30 minutes", "45 minutes", "1 hour", "2 hours"};
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "How much time did you study?",
            "Log Study Time",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[1] // Default to 30 minutes
        );
        
        if (choice != null) {
            int minutes = 0;
            switch (choice) {
                case "15 minutes": minutes = 15; break;
                case "30 minutes": minutes = 30; break;
                case "45 minutes": minutes = 45; break;
                case "1 hour": minutes = 60; break;
                case "2 hours": minutes = 120; break;
            }
            
            if (minutes > 0) {
                // Update user's study time
                currentUser.addStudyMinutes(minutes);
                
                // Award XP for studying (1 XP per minute + bonus)
                int xpReward = minutes; // Base XP
                if (minutes >= 60) xpReward += 20; // Bonus for 1+ hour sessions
                if (minutes >= 120) xpReward += 30; // Extra bonus for 2+ hour sessions
                currentUser.addExperience(xpReward);
                
                // Check if we should increment streak (simplified logic)
                java.time.LocalDate today = java.time.LocalDate.now();
                boolean newStreak = false;
                if (currentUser.getLastStudyDate() == null || !currentUser.getLastStudyDate().equals(today)) {
                    currentUser.incrementStudyStreak();
                    newStreak = true;
                    // Award XP for streak milestone
                    if (currentUser.getStudyStreak() % 3 == 0) {
                        currentUser.addExperience(50); // Bonus XP for every 3-day streak
                    }
                }
                
                // Check for achievements
                int oldLevel = currentUser.getLevel();
                checkAndAwardAchievements();
                
                // Save updated profile
                profileManager.saveProfile(currentUser);
                
                // Refresh stats display
                updateStudyStats();
                
                // Create success message with XP info
                StringBuilder message = new StringBuilder();
                message.append("Great job! ").append(minutes).append(" minutes logged.\n");
                message.append("+").append(xpReward).append(" XP earned!\n");
                
                // Check for level up
                int newLevel = currentUser.getLevel();
                if (newLevel > oldLevel) {
                    message.append("\nLEVEL UP! You are now level ").append(newLevel).append("!\n");
                    currentUser.addExperience(100); // Bonus XP for leveling up
                }
                
                if (newStreak && currentUser.getStudyStreak() > 1) {
                    message.append("Streak: ").append(currentUser.getStudyStreak()).append(" days!\n");
                }
                
                message.append("\nXP: ").append(currentUser.getExperiencePoints());
                message.append(" | Level: ").append(currentUser.getLevel());
                
                JOptionPane.showMessageDialog(
                    this,
                    message.toString(),
                    newLevel > oldLevel ? "Level Up!" : "Study Logged",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
    
    private void initEmojiAnimations() {
        // Initial animation setup - will be controlled by updateAnimations()
    }
    
    private void updateAnimations() {
        if (currentUser == null) return;
        
        // Stop existing animations
        cleanup();
        
        // Rotate hourglass for streak (fixed position, no layout change)
        if (streakIcon != null && currentUser.getStudyStreak() > 0) {
            streakSpinTimer = new Timer(100, e -> {
                streakIcon.incrementAngle(10f);
                if (streakEmojiLabel != null) streakEmojiLabel.repaint();
            });
            streakSpinTimer.start();
        }
        
        // Rotate trophy for achievements (only if any achievements)
        if (trophyIcon != null && currentUser.getAchievements().size() > 0) {
            trophySpinTimer = new Timer(120, e -> {
                trophyIcon.incrementAngle(8f);
                if (achievementsEmojiLabel != null) achievementsEmojiLabel.repaint();
            });
            trophySpinTimer.start();
        }
    }
    
    private void checkAndAwardAchievements() {
        if (currentUser == null) return;
        
        // Achievement: First study session
        if (currentUser.getTotalStudyMinutes() >= 15) {
            currentUser.addAchievement("First Steps");
        }
        
        // Achievement: 1 hour total
        if (currentUser.getTotalStudyMinutes() >= 60) {
            currentUser.addAchievement("Study Hour");
        }
        
        // Achievement: 10 hours total
        if (currentUser.getTotalStudyMinutes() >= 600) {
            currentUser.addAchievement("Dedicated Learner");
        }
        
        // Achievement: 3 day streak
        if (currentUser.getStudyStreak() >= 3) {
            currentUser.addAchievement("Streak Starter");
        }
        
        // Achievement: 7 day streak
        if (currentUser.getStudyStreak() >= 7) {
            currentUser.addAchievement("Week Warrior");
        }
    }
    
    private void showAchievements() {
        if (currentUser == null) return;
        
        // Open the beautiful achievements window
        AchievementsPanel achievementsWindow = new AchievementsPanel(currentUser);
        achievementsWindow.setVisible(true);
    }
    
    private void showLeaderboard() {
        if (currentUser == null) return;
        
        // Open the leaderboard window
        LeaderboardPanel leaderboardWindow = new LeaderboardPanel(currentUser);
        leaderboardWindow.setVisible(true);
    }
    
    private RoundedPanels createTop3LeaderboardCard() {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Header with title and view all button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Top Players");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JButton viewAllButton = new JButton("View All »");
        viewAllButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        viewAllButton.setForeground(Theme.PRIMARY_COLOR);
        viewAllButton.setBackground(new Color(74, 144, 226, 20));
        viewAllButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        viewAllButton.setFocusPainted(false);
        viewAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllButton.addActionListener(e -> showLeaderboard());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(viewAllButton, BorderLayout.EAST);
        
        // Content panel for top 3 users
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Get top 3 users
        java.util.List<UserProfile> topUsers = getTop3Users();
        
        if (topUsers.isEmpty()) {
            JLabel noDataLabel = new JLabel("Start studying to see rankings!", JLabel.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noDataLabel.setForeground(Theme.TEXT_SECONDARY);
            contentPanel.add(noDataLabel);
        } else {
            for (int i = 0; i < topUsers.size() && i < 3; i++) {
                UserProfile user = topUsers.get(i);
                contentPanel.add(createLeaderboardEntry(i + 1, user));
                if (i < topUsers.size() - 1 && i < 2) {
                    contentPanel.add(Box.createVerticalStrut(8));
                }
            }
        }
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createLeaderboardEntry(int rank, UserProfile user) {
        JPanel entry = new JPanel(new BorderLayout());
        entry.setOpaque(false);
        entry.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Rank and avatar
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);
        
        JLabel rankLabel;
        if (rank <= 3) {
            // Try to load medal image
            ImageIcon medalIcon = loadMedalIcon(rank, 16);
            if (medalIcon != null) {
                rankLabel = new JLabel(medalIcon);
            } else {
                // Fallback to text
                String rankText = "#" + rank;
                rankLabel = new JLabel(rankText);
                rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                rankLabel.setForeground(Theme.TEXT_PRIMARY);
            }
        } else {
            rankLabel = new JLabel("#" + rank);
            rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rankLabel.setForeground(Theme.TEXT_PRIMARY);
        }
        
        leftPanel.add(rankLabel);
        
        // Username and level
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        centerPanel.setOpaque(false);
        
        String displayName = user.getUsername();
        if (currentUser != null && user.getUsername().equals(currentUser.getUsername())) {
            displayName = displayName + " (You)";
        }
        
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(currentUser != null && user.getUsername().equals(currentUser.getUsername()) ? 
                                Theme.PRIMARY_COLOR : Theme.TEXT_PRIMARY);
        
        JLabel levelLabel = new JLabel("Lv." + user.getLevel());
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        levelLabel.setForeground(Theme.TEXT_SECONDARY);
        
        centerPanel.add(nameLabel);
        centerPanel.add(levelLabel);
        
        // XP
        JLabel xpLabel = new JLabel(String.format("%,d XP", user.getExperiencePoints()));
        xpLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        xpLabel.setForeground(new Color(124, 58, 237));
        
        entry.add(leftPanel, BorderLayout.WEST);
        entry.add(centerPanel, BorderLayout.CENTER);
        entry.add(xpLabel, BorderLayout.EAST);
        
        return entry;
    }
    
    private java.util.List<UserProfile> getTop3Users() {
        try {
            com.peerconnect.database.DatabaseManager dbManager = new com.peerconnect.database.DatabaseManager();
            java.util.List<UserProfile> allUsers = dbManager.getAllUsers();
            allUsers.sort((u1, u2) -> Integer.compare(u2.getExperiencePoints(), u1.getExperiencePoints()));
            return allUsers.stream().limit(3).collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching leaderboard data: " + e.getMessage());
            return new java.util.ArrayList<>();
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
    
    private ImageIcon loadMedalIcon(int rank, int size) {
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
    
    
    private void showCollaborationChooser(java.util.List<DatabaseManager.CollaborationRequest> requests) {
        String[] options = new String[requests.size()];
        for (int i = 0; i < requests.size(); i++) {
            DatabaseManager.CollaborationRequest req = requests.get(i);
            String peer = req.fromUsername.equals(currentUser.getUsername()) ? req.toUsername : req.fromUsername;
            options[i] = "Collaborate with " + peer;
        }
        
        String choice = (String) JOptionPane.showInputDialog(this, "Choose collaboration:", "Peer Tasks", 
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            
        if (choice != null) {
            int index = java.util.Arrays.asList(options).indexOf(choice);
            if (index >= 0) {
                DatabaseManager.CollaborationRequest req = requests.get(index);
                String peer = req.fromUsername.equals(currentUser.getUsername()) ? req.toUsername : req.fromUsername;
                com.peerconnect.model.UserProfile peerUser = dbManager.getUserByUsername(peer);
                if (peerUser != null) parentFrame.showChecklistPanel(currentUser, peerUser);
            }
        }
    }
    
    // Cleanup method to stop animations
    public void cleanup() {
        if (streakSpinTimer != null && streakSpinTimer.isRunning()) {
            streakSpinTimer.stop();
        }
        if (trophySpinTimer != null && trophySpinTimer.isRunning()) {
            trophySpinTimer.stop();
        }
    }
    
    // Load animated GIF via embedded resources and let Swing animate it (no blocking)
    private ImageIcon loadAnimatedGif(String filename, int width, int height) {
        try {
            // First try to load from resources (embedded in JAR)
            java.net.URL resourceUrl = getClass().getClassLoader().getResource("resources/" + filename);
            if (resourceUrl != null) {
                System.out.println("Loading GIF from resources: " + filename);
                ImageIcon icon = new ImageIcon(resourceUrl);
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    System.out.println("Resource GIF size: " + icon.getIconWidth() + "x" + icon.getIconHeight());
                    return icon;
                }
            }
            
            // Fallback: try to load from filesystem (for development)
            File gifFile = new File(filename);
            System.out.println("Fallback: Looking for GIF at: " + gifFile.getAbsolutePath());
            if (gifFile.exists()) {
                // Prefer URL-based ImageIcon to avoid Toolkit aborts
                java.net.URL url = gifFile.toURI().toURL();
                ImageIcon icon = new ImageIcon(url);
                System.out.println("Fallback URL GIF size: " + icon.getIconWidth() + "x" + icon.getIconHeight());
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return icon;
                }
            }
            
            System.err.println("GIF file not found in resources or filesystem: " + filename);
            return null;
        } catch (Exception e) {
            System.err.println("Could not load animated GIF: " + filename + " - " + e.getMessage());
            return null;
        }
    }
    
    // Fixed-size rotating emoji icon that paints text rotated without changing layout
    private static class RotatingEmojiIcon implements Icon {
        private final String emoji;
        private final int size;
        private float angleDeg = 0f;
        
        RotatingEmojiIcon(String emoji, int size) {
            this.emoji = emoji;
            this.size = size;
        }
        
        void incrementAngle(float delta) {
            this.angleDeg = (this.angleDeg + delta) % 360f;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getIconWidth();
                int h = getIconHeight();
                int cx = x + w / 2;
                int cy = y + h / 2;
                g2.translate(cx, cy);
                g2.rotate(Math.toRadians(angleDeg));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(emoji);
                int ta = fm.getAscent();
                int td = fm.getDescent();
                int th = ta + td;
                // Draw centered at origin
                g2.drawString(emoji, -tw / 2, ta - th / 2);
            } finally {
                g2.dispose();
            }
        }
        
        @Override
        public int getIconWidth() { return size + 4; }
        @Override
        public int getIconHeight() { return size + 4; }
    }
}

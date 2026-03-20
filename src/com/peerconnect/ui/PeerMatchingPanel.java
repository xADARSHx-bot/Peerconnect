package com.peerconnect.ui;

import com.peerconnect.matching.PeerMatchingManager;
import com.peerconnect.database.DatabaseManager;
import com.peerconnect.model.PeerMatch;
import com.peerconnect.model.UserProfile;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PeerMatchingPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private PeerMatchingManager matchingManager;
    private MainApplication parentFrame;
    private UserProfile currentUser;
    private DatabaseManager dbManager;
    private JPanel matchesPanel;
    private JLabel statusLabel;

    public PeerMatchingPanel(MainApplication parentFrame) {
        this.parentFrame = parentFrame;
        this.matchingManager = new PeerMatchingManager();
        this.dbManager = new DatabaseManager();
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content area
        matchesPanel = new JPanel();
        matchesPanel.setLayout(new BoxLayout(matchesPanel, BoxLayout.Y_AXIS));
        matchesPanel.setBackground(Theme.BACKGROUND_COLOR);
        matchesPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        // Ensure child cards expand to full width under BoxLayout (Y_AXIS)
        matchesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(matchesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);

        // Initial status
        statusLabel = new JLabel("<html><center>Click 'Find Study Partners' to discover compatible peers!<br/>Make sure your profile is complete for better matches.</center></html>", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(Theme.TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(40, 20, 40, 20));
        matchesPanel.add(statusLabel);
    }

    private JPanel createHeaderPanel() {
        RoundedPanels headerPanel = new RoundedPanels(12, new BorderLayout());
        headerPanel.setBackground(Theme.CARD_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Title section
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Find Study Partners");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Connect with peers who share your academic interests");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        
        titleSection.add(titleLabel);
        titleSection.add(Box.createVerticalStrut(2));
        titleSection.add(subtitleLabel);

        // Button section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        CustomButtons findButton = new CustomButtons("Find Peers", CustomButtons.ButtonType.SUCCESS);
        CustomButtons backButton = new CustomButtons("← Dashboard", CustomButtons.ButtonType.SECONDARY);
        // Widen Find Peers button
        findButton.setPreferredSize(new Dimension(130, 40));
        findButton.setMinimumSize(new Dimension(130, 40));
        findButton.setMaximumSize(new Dimension(150, 40));
        // Widen Dashboard back button
        backButton.setPreferredSize(new Dimension(140, 40));
        backButton.setMinimumSize(new Dimension(140, 40));
        backButton.setMaximumSize(new Dimension(160, 40));
        
        // Buttons will auto-size to fit text content

        findButton.addActionListener(e -> findAndDisplayMatches());
        backButton.addActionListener(e -> parentFrame.showPanel("Dashboard"));

        buttonPanel.add(backButton);
        buttonPanel.add(findButton);

        headerPanel.add(titleSection, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create a wrapper to add spacing
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(headerPanel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        return wrapper;
    }

    private void findAndDisplayMatches() {
        if (currentUser == null) return;

        statusLabel.setText("Searching for peers...");
        statusLabel.setForeground(Color.BLUE);

        SwingWorker<List<PeerMatch>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PeerMatch> doInBackground() {
                return matchingManager.findPeerMatches(currentUser);
            }

            @Override
            protected void done() {
                try {
                    List<PeerMatch> matches = get();
                    displayMatches(matches);
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("An error occurred during matching.");
                }
            }
        };
        worker.execute();
    }

    private void displayMatches(List<PeerMatch> matches) {
        matchesPanel.removeAll();

        if (matches.isEmpty()) {
            statusLabel.setText("No compatible peers found. Try updating your profile!");
            matchesPanel.add(statusLabel);
        } else {
            for (PeerMatch match : matches) {
                JPanel card = createMatchCard(match);
                // Ensure each card uses the full width of the scroll viewport
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                matchesPanel.add(card);
                matchesPanel.add(Box.createVerticalStrut(10));
            }
        }

        matchesPanel.revalidate();
        matchesPanel.repaint();
    }

    private JPanel createMatchCard(PeerMatch match) {
        RoundedPanels card = new RoundedPanels(10, new BorderLayout(15, 15));
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        // Allow the card to take the full available width and give a bit more height
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Header section
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel usernameLabel = new JLabel(match.getMatchedUser().getUsername());
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        usernameLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel matchTypeLabel = new JLabel();
        matchTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        if ("MUTUAL".equals(match.getMatchType())) {
            matchTypeLabel.setText("MUTUAL MATCH");
            matchTypeLabel.setForeground(Theme.SUCCESS_COLOR);
        } else {
            matchTypeLabel.setText("ONE-WAY MATCH");
            matchTypeLabel.setForeground(Theme.WARNING_COLOR.darker());
        }
        
        header.add(usernameLabel, BorderLayout.WEST);
        header.add(matchTypeLabel, BorderLayout.EAST);

        // Reasons section
        JTextArea reasonsArea = new JTextArea("Match Reasons:\n• " + String.join("\n• ", match.getMatchReasons()));
        reasonsArea.setEditable(false);
        reasonsArea.setOpaque(false);
        reasonsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        reasonsArea.setForeground(Theme.TEXT_PRIMARY);
        reasonsArea.setLineWrap(true);
        reasonsArea.setWrapStyleWord(true);
        reasonsArea.setRows(3); // Give more vertical space for match reasons

        // Bottom section with progress bar and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);
        
        JProgressBar scoreBar = new JProgressBar(0, 100);
        int score = (int) (match.getCompatibilityScore() * 100);
        scoreBar.setValue(score);
        scoreBar.setStringPainted(true);
        scoreBar.setString(score + "% Compatible");
        scoreBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        scoreBar.setPreferredSize(new Dimension(0, 25));
        
        // Color the progress bar based on compatibility score
        if (score >= 80) {
            scoreBar.setForeground(Theme.SUCCESS_COLOR);
        } else if (score >= 60) {
            scoreBar.setForeground(new Color(255, 193, 7)); // Warning color
        } else {
            scoreBar.setForeground(Theme.SECONDARY_COLOR);
        }

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonGroup.setOpaque(false);
        // Ensure button group has proper width for buttons
        buttonGroup.setPreferredSize(new Dimension(300, 40));
        
        CustomButtons viewProfileButton = new CustomButtons("View Profile", CustomButtons.ButtonType.SECONDARY);
        CustomButtons collaborateButton = new CustomButtons("Collaborate", CustomButtons.ButtonType.PRIMARY);
        
        // Widen both buttons
        viewProfileButton.setPreferredSize(new Dimension(130, 40));
        viewProfileButton.setMinimumSize(new Dimension(130, 40));
        viewProfileButton.setMaximumSize(new Dimension(150, 40));
        
        collaborateButton.setPreferredSize(new Dimension(140, 40));
        collaborateButton.setMinimumSize(new Dimension(140, 40));
        collaborateButton.setMaximumSize(new Dimension(160, 40));
        
        // Buttons will auto-size to fit text content

        buttonGroup.add(viewProfileButton);
        buttonGroup.add(collaborateButton);

        bottomPanel.add(scoreBar, BorderLayout.CENTER);
        bottomPanel.add(buttonGroup, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(reasonsArea, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Event listeners
        viewProfileButton.addActionListener(e -> showProfileDialog(match.getMatchedUser()));
        collaborateButton.addActionListener(e -> sendCollaborationRequest(match.getMatchedUser()));

        return card;
    }

    private void showProfileDialog(UserProfile user) {
        JDialog profileDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Profile - " + user.getUsername(), true);
        profileDialog.setSize(450, 550);
        profileDialog.setLocationRelativeTo(this);
        
        // Main panel with modern styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header section
        RoundedPanels headerPanel = new RoundedPanels(12, new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel levelLabel = new JLabel("Level " + user.getLevel() + " • " + user.getExperiencePoints() + " XP");
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        levelLabel.setForeground(new Color(240, 248, 255));
        
        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(nameLabel);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(levelLabel);
        
        headerPanel.add(headerText, BorderLayout.CENTER);
        
        // Profile details section
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Theme.BACKGROUND_COLOR);
        detailsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Add profile information cards
        detailsPanel.add(createInfoCard("Academic Information", 
            "Major: " + (user.getMajor() != null ? user.getMajor() : "Not specified") + "\n" +
            "College Year: " + user.getCollegeYear() + "\n" +
            "Strengths: " + (user.getAcademicStrengths() != null ? user.getAcademicStrengths() : "Not specified") + "\n" +
            "Needs Help: " + (user.getSubjectsNeedingSupport() != null ? user.getSubjectsNeedingSupport() : "Not specified")));
        
        detailsPanel.add(Box.createVerticalStrut(15));
        
        detailsPanel.add(createInfoCard("Study Statistics", 
            "Study Streak: " + user.getStudyStreak() + " days\n" +
            "Total Study Time: " + (user.getTotalStudyMinutes() / 60) + " hours " + (user.getTotalStudyMinutes() % 60) + " minutes\n" +
            "Achievements: " + user.getAchievements().size() + " earned"));
        
        // Close button
        CustomButtons closeButton = new CustomButtons("Close", CustomButtons.ButtonType.SECONDARY);
        closeButton.setPreferredSize(new Dimension(100, 40));
        closeButton.addActionListener(e -> profileDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        profileDialog.add(mainPanel);
        profileDialog.setVisible(true);
    }
    
    private JPanel createInfoCard(String title, String content) {
        RoundedPanels card = new RoundedPanels(8, new BorderLayout());
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setForeground(Theme.TEXT_SECONDARY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        card.add(contentArea, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void sendCollaborationRequest(UserProfile targetUser) {
        JDialog requestDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Send Collaboration Request", true);
        requestDialog.setSize(450, 300);
        requestDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Send collaboration request to " + targetUser.getUsername());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        
        // Message area
        JLabel messageLabel = new JLabel("Message (optional):");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JTextArea messageArea = new JTextArea(4, 30);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        messageArea.setText("Hi! I'd like to collaborate with you on studying. Let's help each other succeed!");
        
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setBorder(null);
        
        // Buttons
        CustomButtons sendButton = new CustomButtons("Send Request", CustomButtons.ButtonType.PRIMARY);
        CustomButtons cancelButton = new CustomButtons("Cancel", CustomButtons.ButtonType.SECONDARY);
        
        sendButton.addActionListener(e -> {
            String message = messageArea.getText().trim();
            if (message.isEmpty()) {
                message = "Let's collaborate on studying together!";
            }
            
            boolean success = dbManager.sendCollaborationRequest(currentUser.getUsername(), targetUser.getUsername(), message);
            requestDialog.dispose();
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Collaboration request sent to " + targetUser.getUsername() + "!\nThey will receive a notification to accept or decline.", 
                    "Request Sent", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Could not send request. You may have already sent a request to this user.", 
                    "Request Failed", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> requestDialog.dispose());
        
        // Layout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(messageScroll);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(sendButton);
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        requestDialog.add(mainPanel);
        requestDialog.setVisible(true);
    }
    
    public void setUser(UserProfile user) {
        this.currentUser = user;
        matchesPanel.removeAll();
        statusLabel.setText("Click 'Find Study Partners' to discover compatible peers!");
        statusLabel.setForeground(Color.GRAY);
        matchesPanel.add(statusLabel);
        matchesPanel.revalidate();
        matchesPanel.repaint();
    }
}

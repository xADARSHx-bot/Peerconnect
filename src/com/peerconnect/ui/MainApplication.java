package com.peerconnect.ui;

import com.peerconnect.manager.ProfileManager;
import com.peerconnect.model.UserProfile;
import javax.swing.*;
import java.awt.*;

public class MainApplication {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private ProfileManager profileManager;

    // Panel references
    private ProfileDashboard profileDashboard;
    private PeerMatchingPanel peerMatchingPanel;
    private ChecklistPanel checklistPanel;
    private BookechangePanel bookechangePanel;

    public MainApplication() {
        profileManager = new ProfileManager();
        
        // Initialize frame with modern settings
        frame = new JFrame("PeerConnect+");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null);
        
        // Set up card layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Initialize all panels
        LoginPanel loginPanel = new LoginPanel(this);
        CreateAccountPanel createAccountPanel = new CreateAccountPanel(this);
        profileDashboard = new ProfileDashboard(profileManager, this);
        peerMatchingPanel = new PeerMatchingPanel(this);
        checklistPanel = new ChecklistPanel(this);
        bookechangePanel = new BookechangePanel(this);

        // Add panels to card layout
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(createAccountPanel, "CreateAccount");
        cardPanel.add(profileDashboard, "Dashboard");
        cardPanel.add(peerMatchingPanel, "PeerMatching");
        cardPanel.add(checklistPanel, "Checklist");
        cardPanel.add(bookechangePanel, "BookExchange");

        // Add content to frame
        frame.add(cardPanel);
        frame.getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        
        // Show the frame
        frame.setVisible(true);
    }

    // --- Navigation Methods ---
    public void showProfileDashboard(UserProfile user) {
        profileDashboard.setUser(user);
        cardLayout.show(cardPanel, "Dashboard");
    }

    public void showPeerMatchingPanel(UserProfile user) {
        peerMatchingPanel.setUser(user);
        cardLayout.show(cardPanel, "PeerMatching");
    }

    public void showChecklistPanel(UserProfile currentUser, UserProfile matchedUser) {
        checklistPanel.setCollaboration(currentUser, matchedUser);
        cardLayout.show(cardPanel, "Checklist");
    }

    public void showBookechangePanel(UserProfile user) {
        bookechangePanel.setUser(user);
        cardLayout.show(cardPanel, "BookExchange");
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public static void main(String[] args) {
        // Initialize FlatLaf theme before creating any UI components
        Theme.apply();
        
        // Create and show the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new MainApplication();
            } catch (Exception e) {
                System.err.println("Error starting PeerConnect+: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

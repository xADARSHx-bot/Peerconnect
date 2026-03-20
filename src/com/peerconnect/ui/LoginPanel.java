package com.peerconnect.ui;

import com.peerconnect.manager.ProfileManager;
import com.peerconnect.model.UserProfile;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainApplication parentFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private CustomButtons loginButton;
    private JButton createAccountButton;

    public LoginPanel(MainApplication parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        
        // Create the main login container with rounded corners
        RoundedPanels loginContainer = new RoundedPanels(15, new BorderLayout(10, 10));
        loginContainer.setBackground(Theme.CARD_BACKGROUND);
        loginContainer.setBorder(new EmptyBorder(40, 40, 40, 40));
        loginContainer.setPreferredSize(new Dimension(400, 480));

        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("PeerConnect+");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Theme.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(subtitleLabel);
        loginContainer.add(headerPanel, BorderLayout.NORTH);

        // --- Form Fields Section ---
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // Username field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Theme.TEXT_PRIMARY);
        
        usernameField = Theme.createTextField();
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Theme.TEXT_PRIMARY);
        
        passwordField = Theme.createPasswordField();
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Add focus listeners for border color changes
        addFocusListeners(usernameField);
        addFocusListeners(passwordField);
        
        fieldsPanel.add(userLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldsPanel.add(usernameField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        fieldsPanel.add(passLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldsPanel.add(passwordField);
        
        loginContainer.add(fieldsPanel, BorderLayout.CENTER);

        // --- Buttons Section ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setOpaque(false);
        
        loginButton = new CustomButtons("Login", CustomButtons.ButtonType.PRIMARY);
        loginButton.setPreferredSize(new Dimension(0, 45));
        
        createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        createAccountButton.setForeground(Theme.TEXT_SECONDARY);
        createAccountButton.setBorderPainted(false);
        createAccountButton.setContentAreaFilled(false);
        createAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to the create account button
        createAccountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                createAccountButton.setForeground(Theme.PRIMARY_COLOR);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                createAccountButton.setForeground(Theme.TEXT_SECONDARY);
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        loginContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Add the container to the main panel
        add(loginContainer, new GridBagConstraints());
        
        // Set up event listeners
        setupEventListeners();
    }
    
    private void addFocusListeners(JTextField field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(e -> handleLogin());
        createAccountButton.addActionListener(e -> parentFrame.showPanel("CreateAccount"));
        
        // Allow Enter key to trigger login
        ActionListener loginAction = e -> handleLogin();
        usernameField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password.");
            return;
        }

        try {
            // Use the correct login method that requires both username and password
            UserProfile user = parentFrame.getProfileManager().login(username, password);
            if (user != null) {
                parentFrame.showProfileDashboard(user);
                clearFields();
            } else {
                showErrorMessage("Invalid username or password. Please try again.");
            }
        } catch (Exception e) {
            showErrorMessage("An error occurred during login. Please try again.");
            System.err.println("Login error: " + e.getMessage());
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Login Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
package com.peerconnect.ui;

import com.peerconnect.manager.ProfileManager;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateAccountPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JComboBox<String> yearComboBox;
    private MainApplication parentFrame;
    private ProfileManager profileManager;
    
    private CustomButtons createButton;
    private JButton backButton;

    public CreateAccountPanel(MainApplication parentFrame) {
        this.parentFrame = parentFrame;
        this.profileManager = parentFrame.getProfileManager();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        
        // Create the main signup container with rounded corners
        RoundedPanels signupContainer = new RoundedPanels(15, new BorderLayout(10, 10));
        signupContainer.setBackground(Theme.CARD_BACKGROUND);
        signupContainer.setBorder(new EmptyBorder(40, 40, 40, 40));
        signupContainer.setPreferredSize(new Dimension(450, 700));

        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Theme.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join PeerConnect+ to get started");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        signupContainer.add(headerPanel, BorderLayout.NORTH);

        // --- Form Fields Section ---
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Full Name field
        fieldsPanel.add(createFieldSection("Full Name", fullNameField = Theme.createTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
     // Email field
        fieldsPanel.add(createFieldSection("Email", emailField = Theme.createTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Username field
        fieldsPanel.add(createFieldSection("Username", usernameField = Theme.createTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Password field
        fieldsPanel.add(createFieldSection("Password", passwordField = Theme.createPasswordField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // College Year field
        String[] years = {"1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate Student"};
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearComboBox.setPreferredSize(new Dimension(0, 40));
        yearComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        yearComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        yearComboBox.setBackground(Color.WHITE);
        yearComboBox.setForeground(Theme.TEXT_PRIMARY);
        yearComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        fieldsPanel.add(createComboBoxFieldSection("College Year", yearComboBox));
        
        signupContainer.add(fieldsPanel, BorderLayout.CENTER);

        // --- Buttons Section ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setOpaque(false);
        
        createButton = new CustomButtons("Create Account", CustomButtons.ButtonType.SUCCESS);
        createButton.setPreferredSize(new Dimension(0, 45));
        
        backButton = new JButton("Sign In");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setForeground(Theme.TEXT_SECONDARY);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to the back button
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setForeground(Theme.PRIMARY_COLOR);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setForeground(Theme.TEXT_SECONDARY);
            }
        });
        
        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
        signupContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Add the container to the main panel
        add(signupContainer, new GridBagConstraints());
        
        // Set up event listeners
        setupEventListeners();
    }
    
    private JPanel createFieldSection(String labelText, JTextField field) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.TEXT_PRIMARY);
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Add focus listeners for border color changes
        addFocusListeners(field);
        
        // No special handling needed for single password field
        
        section.add(label);
        section.add(Box.createRigidArea(new Dimension(0, 5)));
        section.add(field);
        
        return section;
    }
    
    private JPanel createComboBoxFieldSection(String labelText, JComboBox<?> comboBox) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(label);
        section.add(Box.createRigidArea(new Dimension(0, 5)));
        section.add(comboBox);
        
        return section;
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
    
    // Password matching methods removed - using single password field
    
    private void setupEventListeners() {
        createButton.addActionListener(e -> handleCreateAccount());
        backButton.addActionListener(e -> parentFrame.showPanel("Login"));
    }
    
    private void handleCreateAccount() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars).trim();
        
        // Clear the char array for security
        java.util.Arrays.fill(passwordChars, ' ');
        
        int year = yearComboBox.getSelectedIndex() + 1;

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please fill in all fields.");
            return;
        }
        
        if (password.length() < 6) {
            showErrorMessage("Password must be at least 6 characters long.");
            return;
        }
        
        // No password confirmation needed - single password field
        
        if (!isValidEmail(email)) {
            showErrorMessage("Please enter a valid email address.");
            return;
        }

        try {
            if (profileManager.createAccount(username, password, email, fullName, year)) {
                showSuccessMessage("Account created successfully! Please log in with your new credentials.");
                clearFields();
                parentFrame.showPanel("Login");
            } else {
                showErrorMessage("Username already exists. Please choose a different username.");
            }
        } catch (Exception e) {
            showErrorMessage("An error occurred while creating the account. Please try again.");
            System.err.println("Account creation error: " + e.getMessage());
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Registration Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Registration Successful",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void clearFields() {
        fullNameField.setText("");
        emailField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        yearComboBox.setSelectedIndex(0);
    }
}
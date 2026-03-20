
package com.peerconnect.ui;

import com.peerconnect.model.UserProfile;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskCreationDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField taskNameField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> dueDateCombo;
    private JTextArea descriptionArea;
    private JPanel templatesPanel;
    private TaskData createdTask;
    private UserProfile currentUser;
    
    private static final String[] CATEGORIES = {"Study", "Collaboration", "Personal", "Goals"};
    private static final String[] PRIORITIES = {"High", "Medium", "Low"};
    private static final String[] DUE_DATES = {"No Due Date", "Today", "Tomorrow", "This Week", "Next Week"};
    
    public TaskCreationDialog(Window parent, UserProfile user) {
        super(parent, "✨ Create New Task", ModalityType.APPLICATION_MODAL);
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(700, 620);
        setLocationRelativeTo(getParent());
        
        // Main panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(74, 144, 226),
                    0, getHeight(), new Color(80, 170, 240)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel("Create a New Task", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Content
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        // Buttons
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createContentPanel() {
        RoundedPanels contentPanel = new RoundedPanels(15, new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Task Name
        formPanel.add(createFieldSection("📝 Task Name", 
            taskNameField = new JTextField()));
        
        // Category and Priority row
        JPanel categoryPriorityPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        categoryPriorityPanel.setOpaque(false);
        
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryPriorityPanel.add(createFieldSection("📂 Category", categoryCombo));
        
        priorityCombo = new JComboBox<>(PRIORITIES);
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryPriorityPanel.add(createFieldSection("⚡ Priority", priorityCombo));
        
        formPanel.add(categoryPriorityPanel);
        
        // Due Date
        dueDateCombo = new JComboBox<>(DUE_DATES);
        dueDateCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(createFieldSection("📅 Due Date", dueDateCombo));
        
        // Description
        descriptionArea = new JTextArea(6, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(0, 150));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        formPanel.add(createFieldSection("📄 Description (Optional)", descScroll));
        
        // Quick Templates
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createTemplatesSection());
        
        contentPanel.add(formPanel, BorderLayout.CENTER);
        return contentPanel;
    }
    
    private JPanel createFieldSection(String labelText, JComponent field) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(label);
        section.add(Box.createVerticalStrut(5));
        section.add(field);
        
        return section;
    }
    
    private JPanel createTemplatesSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel templatesLabel = new JLabel("⚡ Quick Templates");
        templatesLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        templatesLabel.setForeground(new Color(60, 60, 60));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonsPanel.setOpaque(false);
        
        // Template buttons
        String[] templates = {
            "📚 Study Session", "🤝 Group Meeting", "📝 Assignment Due",
            "📖 Read Chapter", "💯 Practice Test", "🎯 Weekly Goal"
        };
        
        for (String template : templates) {
            JButton templateBtn = new JButton(template);
            templateBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            templateBtn.setForeground(new Color(74, 144, 226));
            templateBtn.setBackground(new Color(240, 248, 255));
            templateBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            templateBtn.setFocusPainted(false);
            templateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            templateBtn.addActionListener(e -> applyTemplate(template));
            buttonsPanel.add(templateBtn);
        }
        
        section.add(templatesLabel);
        section.add(Box.createVerticalStrut(8));
        section.add(buttonsPanel);
        
        return section;
    }
    
    private void applyTemplate(String template) {
        switch (template) {
            case "📚 Study Session":
                taskNameField.setText("Study Session");
                categoryCombo.setSelectedItem("Study");
                priorityCombo.setSelectedItem("Medium");
                dueDateCombo.setSelectedItem("Today");
                break;
            case "🤝 Group Meeting":
                taskNameField.setText("Group Study Meeting");
                categoryCombo.setSelectedItem("Collaboration");
                priorityCombo.setSelectedItem("High");
                dueDateCombo.setSelectedItem("This Week");
                break;
            case "📝 Assignment Due":
                taskNameField.setText("Complete Assignment");
                categoryCombo.setSelectedItem("Study");
                priorityCombo.setSelectedItem("High");
                dueDateCombo.setSelectedItem("This Week");
                break;
            case "📖 Read Chapter":
                taskNameField.setText("Read Chapter");
                categoryCombo.setSelectedItem("Study");
                priorityCombo.setSelectedItem("Medium");
                dueDateCombo.setSelectedItem("Tomorrow");
                break;
            case "💯 Practice Test":
                taskNameField.setText("Take Practice Test");
                categoryCombo.setSelectedItem("Study");
                priorityCombo.setSelectedItem("Medium");
                dueDateCombo.setSelectedItem("This Week");
                break;
            case "🎯 Weekly Goal":
                taskNameField.setText("Weekly Study Goal");
                categoryCombo.setSelectedItem("Goals");
                priorityCombo.setSelectedItem("High");
                dueDateCombo.setSelectedItem("Next Week");
                break;
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        CustomButtons createButton = new CustomButtons("Create Task", CustomButtons.ButtonType.SUCCESS);
        createButton.setPreferredSize(new Dimension(140, 40));
        
        CustomButtons cancelButton = new CustomButtons("Cancel", CustomButtons.ButtonType.SECONDARY);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        
        createButton.addActionListener(e -> createTask());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        return buttonPanel;
    }
    
    private void createTask() {
        String taskName = taskNameField.getText().trim();
        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a task name.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String category = (String) categoryCombo.getSelectedItem();
        String priority = (String) priorityCombo.getSelectedItem();
        String dueDateSelection = (String) dueDateCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        
        String formattedDueDate = formatDueDate(dueDateSelection);
        
        createdTask = new TaskData(taskName, category, priority, formattedDueDate, description);
        dispose();
    }
    
    private String formatDueDate(String selection) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        switch (selection) {
            case "Today":
                return today.format(formatter);
            case "Tomorrow":
                return today.plusDays(1).format(formatter);
            case "This Week":
                return today.plusDays(7 - today.getDayOfWeek().getValue()).format(formatter);
            case "Next Week":
                return today.plusDays(14 - today.getDayOfWeek().getValue()).format(formatter);
            default:
                return "";
        }
    }
    
    public TaskData getCreatedTask() {
        return createdTask;
    }
}

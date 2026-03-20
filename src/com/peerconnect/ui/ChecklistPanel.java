package com.peerconnect.ui;

import com.peerconnect.model.UserProfile;
import com.peerconnect.model.ChecklistItem;
import com.peerconnect.database.DatabaseManager;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;
import com.peerconnect.ui.IconLoader;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChecklistPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private UserProfile currentUser;
    private UserProfile matchedUser;
    private JPanel checklistItemsPanel;
    private MainApplication parentFrame;
    private JLabel headerLabel;
    private DatabaseManager dbManager;
    private String currentSpaceId;
    
    // Active category filter: "All", "Study", "Collaboration", "Personal", "Goals"
    private String activeFilter = "All";

    public ChecklistPanel(MainApplication parentFrame) {
        this.parentFrame = parentFrame;
        this.dbManager = new DatabaseManager();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content
        checklistItemsPanel = new JPanel();
        checklistItemsPanel.setLayout(new BoxLayout(checklistItemsPanel, BoxLayout.Y_AXIS));
        checklistItemsPanel.setBackground(Theme.BACKGROUND_COLOR);
        checklistItemsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JScrollPane scrollPane = new JScrollPane(checklistItemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_COLOR);
        
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        RoundedPanels headerPanel = new RoundedPanels(12, new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        headerLabel = new JLabel("Collaboration Checklist");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Track your study goals and progress");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(240, 248, 255));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(headerLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        headerPanel.add(textPanel, BorderLayout.WEST);
        
        // Create wrapper for spacing
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(headerPanel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        return wrapper;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomSection = new JPanel();
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Category filter buttons
        JPanel filterPanel = createCategoryFilterPanel();
        bottomSection.add(filterPanel);
        bottomSection.add(Box.createVerticalStrut(15));
        
        // Task statistics
        JPanel statsPanel = createStatsPanel();
        bottomSection.add(statsPanel);
        bottomSection.add(Box.createVerticalStrut(15));
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        CustomButtons backButton = new CustomButtons("← Dashboard", CustomButtons.ButtonType.SECONDARY);
        backButton.setPreferredSize(new Dimension(140, 40));
        
        CustomButtons addTaskButton = new CustomButtons("Add Task", CustomButtons.ButtonType.PRIMARY);
        addTaskButton.setPreferredSize(new Dimension(130, 40));
        
        CustomButtons clearButton = new CustomButtons("Clear Done", CustomButtons.ButtonType.WARNING);
        clearButton.setPreferredSize(new Dimension(140, 40));
        
        backButton.addActionListener(e -> {
            if (currentUser != null) {
                parentFrame.showProfileDashboard(currentUser);
            } else {
                parentFrame.showPanel("Dashboard");
            }
        });
        
        addTaskButton.addActionListener(e -> addCustomTask());
        clearButton.addActionListener(e -> clearCompletedTasks());
        
        buttonPanel.add(backButton);
        buttonPanel.add(addTaskButton);
        buttonPanel.add(clearButton);
        
        bottomSection.add(buttonPanel);
        return bottomSection;
    }
    
    private JPanel createCategoryFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        filterPanel.setOpaque(false);
        
        JLabel filterLabel = new JLabel("Filter: ");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(Theme.TEXT_PRIMARY);
        filterPanel.add(filterLabel);
        
        String[] filters = {"All", "Study", "Collaboration", "Personal", "Goals"};
        ButtonGroup filterGroup = new ButtonGroup();
        
        for (String filter : filters) {
            JToggleButton filterBtn = new JToggleButton(filter);
            filterBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            filterBtn.setForeground(Theme.TEXT_PRIMARY);
            filterBtn.setBackground(new Color(248, 249, 250));
            filterBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            filterBtn.setFocusPainted(false);
            filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (filter.equals("All")) {
                filterBtn.setSelected(true);
                filterBtn.setBackground(Theme.PRIMARY_COLOR);
                filterBtn.setForeground(Color.WHITE);
            }
            
            filterBtn.addActionListener(e -> {
                // Update button states
                for (AbstractButton btn : java.util.Collections.list(filterGroup.getElements())) {
                    if (btn == filterBtn) {
                        btn.setBackground(Theme.PRIMARY_COLOR);
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(new Color(248, 249, 250));
                        btn.setForeground(Theme.TEXT_PRIMARY);
                    }
                }
                // Set active filter and reload list
                activeFilter = filterBtn.getText();
                loadChecklistItems();
            });
            
            filterGroup.add(filterBtn);
            filterPanel.add(filterBtn);
        }
        
        return filterPanel;
    }
    
    private JPanel createStatsPanel() {
        RoundedPanels statsPanel = new RoundedPanels(8, new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.setBackground(new Color(248, 250, 252));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        if (currentSpaceId != null) {
            java.util.List<ChecklistItem> allTasks = dbManager.getChecklistItems(currentSpaceId);
            int totalTasks = allTasks.size();
            int completedTasks = (int) allTasks.stream().filter(ChecklistItem::isCompleted).count();
            int pendingTasks = totalTasks - completedTasks;
            
            // Progress percentage
            int progressPercent = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
            
            ImageIcon documentIcon = com.peerconnect.ui.IconLoader.loadIcon("document", 13);
            JLabel totalLabel = new JLabel("Total: " + totalTasks, documentIcon, JLabel.LEFT);
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            totalLabel.setForeground(new Color(73, 80, 87));
            
            ImageIcon checkIcon = com.peerconnect.ui.IconLoader.loadIcon("checkmark", 13);
            JLabel completedLabel = new JLabel("Done: " + completedTasks, checkIcon, JLabel.LEFT);
            completedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            completedLabel.setForeground(new Color(40, 167, 69));
            
            ImageIcon hourglassIcon = com.peerconnect.ui.IconLoader.loadIcon("hourglass", 13);
            JLabel pendingLabel = new JLabel("Pending: " + pendingTasks, hourglassIcon, JLabel.LEFT);
            pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            pendingLabel.setForeground(new Color(255, 193, 7));
            
            ImageIcon statsIcon = IconLoader.loadIcon("statistics", 13);
            JLabel progressLabel = new JLabel("Progress: " + progressPercent + "%", statsIcon, JLabel.LEFT);
            progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            progressLabel.setForeground(Theme.PRIMARY_COLOR);
            
            statsPanel.add(totalLabel);
            statsPanel.add(completedLabel);
            statsPanel.add(pendingLabel);
            statsPanel.add(progressLabel);
        }
        
        return statsPanel;
    }
    
    private void addCustomTask() {
        TaskCreationDialog dialog = new TaskCreationDialog(SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
        
        if (dialog.getCreatedTask() != null) {
            TaskData taskData = dialog.getCreatedTask();
            String formattedTask = formatTaskWithDetails(taskData);
            
            ChecklistItem newItem = dbManager.addChecklistItem(currentSpaceId, formattedTask, currentUser.getUsername());
            if (newItem != null) {
                // Rebuild list so filter applies
                loadChecklistItems();
            }
        }
    }
    
    private String formatTaskWithDetails(TaskData taskData) {
        StringBuilder formatted = new StringBuilder();
        
        // Priority prefix
        switch (taskData.priority) {
            case "High":
                formatted.append("[HIGH] ");
                break;
            case "Medium":
                formatted.append("[MED] ");
                break;
            case "Low":
                formatted.append("[LOW] ");
                break;
        }
        
        // Category prefix
        switch (taskData.category) {
            case "Study":
                formatted.append("[STUDY] ");
                break;
            case "Collaboration":
                formatted.append("[COLLAB] ");
                break;
            case "Personal":
                formatted.append("[PERSONAL] ");
                break;
            case "Goals":
                formatted.append("[GOALS] ");
                break;
        }
        
        formatted.append(taskData.taskName);
        
        // Due date
        if (taskData.dueDate != null && !taskData.dueDate.isEmpty()) {
            formatted.append(" (Due: ").append(taskData.dueDate).append(")");
        }
        
        return formatted.toString();
    }
    
    private void clearCompletedTasks() {
        if (currentSpaceId != null) {
            dbManager.deleteCompletedChecklistItems(currentSpaceId);
            loadChecklistItems();
        }
    }
    
    private void addChecklistItemToUI(ChecklistItem item) {
        RoundedPanels taskCard = new RoundedPanels(12, new BorderLayout());
        
        // Determine priority color from task text
        Color priorityColor = getPriorityColor(item.getTaskDescription());
        Color backgroundColor = item.isCompleted() ? new Color(248, 249, 250) : Theme.CARD_BACKGROUND;
        
        taskCard.setBackground(backgroundColor);
        taskCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(priorityColor, 2),
            new EmptyBorder(18, 20, 18, 20)
        ));
        taskCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Add hover effect
        taskCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!item.isCompleted()) {
                    taskCard.setBackground(new Color(250, 252, 255));
                    taskCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(priorityColor, 3),
                        new EmptyBorder(17, 19, 17, 19)
                    ));
                    taskCard.repaint();
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!item.isCompleted()) {
                    taskCard.setBackground(Theme.CARD_BACKGROUND);
                    taskCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(priorityColor, 2),
                        new EmptyBorder(18, 20, 18, 20)
                    ));
                    taskCard.repaint();
                }
            }
        });
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Checkbox with better styling
        JCheckBox checkBox = new JCheckBox();
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        checkBox.setForeground(item.isCompleted() ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY);
        checkBox.setOpaque(false);
        checkBox.setSelected(item.isCompleted());
        checkBox.setFocusPainted(false);
        
        // Task text with better formatting
        JLabel taskLabel = new JLabel("<html>" + formatTaskText(item.getTaskDescription()) + "</html>");
        taskLabel.setFont(new Font("Segoe UI", item.isCompleted() ? Font.ITALIC : Font.PLAIN, 14));
        taskLabel.setForeground(item.isCompleted() ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY);
        
        // Update visuals instantly on completion to avoid flicker
        checkBox.addChangeListener(e -> {
            boolean completed = checkBox.isSelected();
            
            if (completed) {
                taskLabel.setForeground(Theme.TEXT_SECONDARY);
                taskLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                taskCard.setBackground(new Color(248, 249, 250));
            } else {
                taskLabel.setForeground(Theme.TEXT_PRIMARY);
                taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                taskCard.setBackground(Theme.CARD_BACKGROUND);
            }
            
            taskCard.revalidate();
            taskCard.repaint();
            
            // Update database
            dbManager.updateChecklistItemStatus(item.getId(), completed);
        });
        
        // Add delete button for custom tasks
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        
        if (!item.getTaskDescription().startsWith("✓")) { // Custom tasks don't have ✓ prefix
            ImageIcon trashIcon = IconLoader.loadIcon("trash", 12);
            JButton deleteBtn = new JButton(trashIcon);
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            deleteBtn.setForeground(new Color(220, 53, 69));
            deleteBtn.setBackground(new Color(255, 240, 240));
            deleteBtn.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            deleteBtn.setFocusPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.setToolTipText("Delete task");
            
            deleteBtn.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this task?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    dbManager.deleteChecklistItem(item.getId());
                    loadChecklistItems();
                }
            });
            
            rightPanel.add(deleteBtn);
        }
        
        contentPanel.add(checkBox, BorderLayout.WEST);
        contentPanel.add(taskLabel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
        
        taskCard.add(contentPanel, BorderLayout.CENTER);
        
        checklistItemsPanel.add(taskCard);
        checklistItemsPanel.add(Box.createVerticalStrut(12));
        
        checklistItemsPanel.revalidate();
        checklistItemsPanel.repaint();
    }
    
    private Color getPriorityColor(String taskText) {
        if (taskText.contains("[HIGH]")) {
            return new Color(220, 53, 69); // High priority - red
        } else if (taskText.contains("[MED]")) {
            return new Color(255, 193, 7); // Medium priority - yellow
        } else if (taskText.contains("[LOW]")) {
            return new Color(40, 167, 69); // Low priority - green
        }
        return new Color(108, 117, 125); // Default - gray
    }
    
    private String formatTaskText(String taskText) {
        // Add some HTML formatting for better display
        String formatted = taskText;
        
        // Make due dates stand out
        formatted = formatted.replaceAll("\\(Due: ([^)]+)\\)", "<span style='color: #dc3545; font-weight: bold;'>(Due: $1)</span>");
        
        return formatted;
    }
    
    private void loadChecklistItems() {
        checklistItemsPanel.removeAll();
        
        if (currentSpaceId != null) {
            java.util.List<ChecklistItem> items = dbManager.getChecklistItems(currentSpaceId);
            for (ChecklistItem item : items) {
                if (matchesActiveFilter(item.getTaskDescription())) {
                    addChecklistItemToUI(item);
                }
            }
        }
        
        checklistItemsPanel.revalidate();
        checklistItemsPanel.repaint();
    }
    
    private boolean matchesActiveFilter(String taskText) {
        if (activeFilter == null || activeFilter.equalsIgnoreCase("All")) return true;
        String t = taskText == null ? "" : taskText;
        switch (activeFilter) {
            case "Study":
                return t.contains("[STUDY]") || t.contains("📚");
            case "Collaboration":
                return t.contains("[COLLAB]") || t.contains("🤝");
            case "Personal":
                return t.contains("[PERSONAL]") || t.contains("👤");
            case "Goals":
                return t.contains("[GOALS]") || t.contains("🎯");
            default:
                return true;
        }
    }

    public void setCollaboration(UserProfile currentUser, UserProfile matchedUser) {
        this.currentUser = currentUser;
        this.matchedUser = matchedUser;
        
        // Set up space ID for collaboration or personal use
        if (matchedUser != null) {
            this.currentSpaceId = dbManager.getCollaborationSpaceId(currentUser.getUsername(), matchedUser.getUsername());
            headerLabel.setText("Collaboration with " + matchedUser.getUsername());
        } else {
            this.currentSpaceId = currentUser.getUsername() + "_personal";
            headerLabel.setText("Personal Study Checklist");
        }
        
        // Load existing items from database
        loadChecklistItems();
        
        // Add default tasks only if no items exist yet
        if (dbManager.getChecklistItems(currentSpaceId).isEmpty()) {
            addDefaultTasks(matchedUser);
        }
    }
    
    private void addDefaultTasks(UserProfile matchedUser) {
        String creatorUsername = currentUser.getUsername();
        
        dbManager.addChecklistItem(currentSpaceId, "✓ Exchange contact information", creatorUsername);
        dbManager.addChecklistItem(currentSpaceId, "✓ Set up study schedule", creatorUsername);
        dbManager.addChecklistItem(currentSpaceId, "✓ Share study materials", creatorUsername);
        dbManager.addChecklistItem(currentSpaceId, "✓ Define learning objectives", creatorUsername);
        dbManager.addChecklistItem(currentSpaceId, "✓ Plan first study session", creatorUsername);
        
        if (matchedUser != null) {
            dbManager.addChecklistItem(currentSpaceId, "✓ Review " + matchedUser.getUsername() + "'s strengths", creatorUsername);
        }
        
        // Reload to show the new items
        loadChecklistItems();
    }
}

package com.peerconnect.ui;

import com.peerconnect.model.Books;
import com.peerconnect.model.UserProfile;
import com.peerconnect.ui.components.CustomButtons;
import com.peerconnect.ui.components.RoundedPanels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Book Exchange Panel for PeerConnect - allows users to list, browse, and exchange books
 */
public class BookechangePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainApplication parentFrame;
    private UserProfile currentUser;
    private JPanel booksListPanel;
    private List<Books> availableBooks;
    private JLabel statusLabel;

    public BookechangePanel(MainApplication parentFrame) {
        this.parentFrame = parentFrame;
        this.availableBooks = new ArrayList<>();
        initializeUI();
        loadSampleBooks(); // Add some sample books for demonstration
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content area
        booksListPanel = new JPanel();
        booksListPanel.setLayout(new BoxLayout(booksListPanel, BoxLayout.Y_AXIS));
        booksListPanel.setBackground(Theme.BACKGROUND_COLOR);
        booksListPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JScrollPane scrollPane = new JScrollPane(booksListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);

        // Initial status
        statusLabel = new JLabel("<html><center>Browse available books or list your own for exchange!</center></html>", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(Theme.TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(40, 20, 40, 20));
        booksListPanel.add(statusLabel);
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
        
        JLabel titleLabel = new JLabel("Book Exchange");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Share and discover textbooks with your peers");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        
        titleSection.add(titleLabel);
        titleSection.add(Box.createVerticalStrut(2));
        titleSection.add(subtitleLabel);

        // Button section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        CustomButtons addBookButton = new CustomButtons("List Book", CustomButtons.ButtonType.SUCCESS);
        CustomButtons searchButton = new CustomButtons("Search", CustomButtons.ButtonType.PRIMARY);
        CustomButtons refreshButton = new CustomButtons("Refresh", CustomButtons.ButtonType.SECONDARY);
        CustomButtons backButton = new CustomButtons("← Dashboard", CustomButtons.ButtonType.SECONDARY);
        
        // Widen buttons for better text fit
        addBookButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.setPreferredSize(new Dimension(100, 40));
        backButton.setPreferredSize(new Dimension(140, 40));

        addBookButton.addActionListener(e -> showAddBookDialog());
        searchButton.addActionListener(e -> showSearchDialog());
        refreshButton.addActionListener(e -> refreshBooksList());
        backButton.addActionListener(e -> {
            if (currentUser != null) {
                parentFrame.showProfileDashboard(currentUser);
            } else {
                parentFrame.showPanel("Dashboard");
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(addBookButton);

        headerPanel.add(titleSection, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create wrapper for spacing
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(headerPanel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        return wrapper;
    }

    private void loadSampleBooks() {
        // Add some sample books for demonstration
        availableBooks.add(new Books("Introduction to Algorithms", "Thomas Cormen", "978-0262033848", 
                                  "Computer Science", "Good", "alice_student", "alice@university.edu"));
        availableBooks.add(new Books("Calculus: Early Transcendentals", "James Stewart", "978-1285741550", 
                                  "Mathematics", "Fair", "bob_math", "bob@university.edu"));
        availableBooks.add(new Books("Clean Code", "Robert Martin", "978-0132350884", 
                                  "Computer Science", "Excellent", "charlie_dev", "charlie@university.edu"));
        availableBooks.add(new Books("Physics for Scientists", "Raymond Serway", "978-1133947271", 
                                  "Physics", "Good", "diana_physics", "diana@university.edu"));
    }

    private void refreshBooksList() {
        booksListPanel.removeAll();
        
        if (availableBooks.isEmpty()) {
            statusLabel.setText("No books available for exchange. Be the first to list one!");
            booksListPanel.add(statusLabel);
        } else {
            for (Books book : availableBooks) {
                if (book.isAvailable()) {
                    booksListPanel.add(createBookCard(book));
                    booksListPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        
        booksListPanel.revalidate();
        booksListPanel.repaint();
    }

    private JPanel createBookCard(Books book) {
        RoundedPanels card = new RoundedPanels(10, new BorderLayout(15, 15));
        card.setBackground(Theme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Book info section
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel authorLabel = new JLabel("by " + book.getAuthor());
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        authorLabel.setForeground(Theme.TEXT_SECONDARY);
        
        JLabel categoryLabel = new JLabel("📚 " + book.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(Theme.PRIMARY_COLOR);
        
        JLabel conditionLabel = new JLabel("Condition: " + book.getCondition());
        conditionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        conditionLabel.setForeground(Theme.TEXT_SECONDARY);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(authorLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(categoryLabel);
        infoPanel.add(conditionLabel);

        // Owner and action section
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        
        JLabel ownerLabel = new JLabel("Owner: " + book.getOwnerUsername());
        ownerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ownerLabel.setForeground(Theme.TEXT_PRIMARY);
        
        CustomButtons contactButton = new CustomButtons("Contact", CustomButtons.ButtonType.PRIMARY);
        contactButton.setPreferredSize(new Dimension(100, 35));
        contactButton.addActionListener(e -> showContactDialog(book));
        
        rightPanel.add(ownerLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(contactButton);
        rightPanel.add(Box.createVerticalGlue());

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private void showAddBookDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to list books.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "List a New Book", true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JTextField titleField = Theme.createTextField();
        JTextField authorField = Theme.createTextField();
        JTextField isbnField = Theme.createTextField();
        String[] categories = {"Computer Science", "Mathematics", "Physics", "Chemistry", "Biology", "Literature", "History", "Other"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        String[] conditions = {"Excellent", "Good", "Fair", "Poor"};
        JComboBox<String> conditionBox = new JComboBox<>(conditions);
        JTextField contactField = Theme.createTextField();
        contactField.setText(currentUser.getEmail() != null && !currentUser.getEmail().isEmpty() ? 
                            currentUser.getEmail() : currentUser.getUsername() + "@university.edu"); // Use real email or fallback
        
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("ISBN (optional):"));
        panel.add(isbnField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Condition:"));
        panel.add(conditionBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactField);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        CustomButtons addButton = new CustomButtons("List Book", CustomButtons.ButtonType.SUCCESS);
        addButton.setPreferredSize(new Dimension(150, 40));
        CustomButtons cancelButton = new CustomButtons("Cancel", CustomButtons.ButtonType.SECONDARY);
        
        addButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in title and author.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Books newBook = new Books(title, author, isbnField.getText().trim(),
                                  (String) categoryBox.getSelectedItem(),
                                  (String) conditionBox.getSelectedItem(),
                                  currentUser.getUsername(), contactField.getText().trim());
            
            availableBooks.add(newBook);
            refreshBooksList();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Book listed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showContactDialog(Books book) {
        String message = String.format(
            "Book: %s\nAuthor: %s\nOwner: %s\nContact: %s\n\nClick OK to copy contact info to clipboard.",
            book.getTitle(), book.getAuthor(), book.getOwnerUsername(), book.getContactInfo()
        );
        
        int result = JOptionPane.showConfirmDialog(this, message, "Contact Owner", 
                                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // Copy to clipboard
            java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(book.getContactInfo());
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            
            JOptionPane.showMessageDialog(this, "Contact info copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showSearchDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Search a Book", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search fields (excluding condition and contact info)
        JTextField titleField = Theme.createTextField();
        JTextField authorField = Theme.createTextField();
        JTextField isbnField = Theme.createTextField();
        String[] categories = {"All Categories", "Computer Science", "Mathematics", "Physics", "Chemistry", "Biology", "Literature", "History", "Other"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("ISBN (optional):"));
        panel.add(isbnField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        CustomButtons searchButton = new CustomButtons("Search Books", CustomButtons.ButtonType.PRIMARY);
        searchButton.setPreferredSize(new Dimension(150, 40));
        CustomButtons cancelButton = new CustomButtons("Cancel", CustomButtons.ButtonType.SECONDARY);
        
        searchButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            
            performSearch(title, author, isbn, category);
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void performSearch(String title, String author, String isbn, String category) {
        boolean hasCriteria = !(title.isEmpty() && author.isEmpty() && isbn.isEmpty() && (category == null || category.equals("All Categories")));
        if (!hasCriteria) {
            JOptionPane.showMessageDialog(this, "Please enter at least one search criteria.", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String t = title.toLowerCase();
        String a = author.toLowerCase();
        String i = isbn.toLowerCase();
        String c = category;

        java.util.List<Books> filtered = new java.util.ArrayList<>();
        for (Books b : availableBooks) {
            if (!b.isAvailable()) continue;
            boolean match = true;
            if (!t.isEmpty()) {
                match &= b.getTitle() != null && b.getTitle().toLowerCase().contains(t);
            }
            if (!a.isEmpty()) {
                match &= b.getAuthor() != null && b.getAuthor().toLowerCase().contains(a);
            }
            if (!i.isEmpty()) {
                String bookIsbn = b.getIsbn() == null ? "" : b.getIsbn();
                match &= bookIsbn.toLowerCase().contains(i);
            }
            if (c != null && !c.equals("All Categories")) {
                match &= b.getCategory() != null && b.getCategory().equalsIgnoreCase(c);
            }
            if (match) filtered.add(b);
        }

        // Render filtered results
        booksListPanel.removeAll();
        if (filtered.isEmpty()) {
            JLabel noResults = new JLabel("No books found matching your search.", SwingConstants.CENTER);
            noResults.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noResults.setForeground(Theme.TEXT_SECONDARY);
            noResults.setBorder(new EmptyBorder(40, 20, 40, 20));
            booksListPanel.add(noResults);
        } else {
            for (Books book : filtered) {
                booksListPanel.add(createBookCard(book));
                booksListPanel.add(Box.createVerticalStrut(10));
            }
        }
        booksListPanel.revalidate();
        booksListPanel.repaint();
    }

    public void setUser(UserProfile user) {
        this.currentUser = user;
        refreshBooksList();
    }
}
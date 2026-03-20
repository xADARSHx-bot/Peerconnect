package com.peerconnect.model;

/**
 * Represents a book in the PeerConnect exchange system.
 * Contains all relevant book details and owner information.
 */
public class Books {

    private String title;
    private String author;
    private String isbn;
    private String category;
    private String condition; // e.g., "New", "Good", "Fair"
    private String ownerUsername;
    private String contactInfo;
    private boolean isAvailable;
    private String description;

    /**
     * Constructs a new Books object.
     * @param title The title of the book
     * @param author The author of the book
     * @param isbn The ISBN of the book (optional)
     * @param category The category or subject (e.g., "Computer Science", "Mathematics")
     * @param condition The condition of the book
     * @param ownerUsername The username of the book's owner
     * @param contactInfo How to contact the owner
     */
    public Books(String title, String author, String isbn, String category, String condition,
                 String ownerUsername, String contactInfo) {
        this.title = title;
        this.author = author;
        this.isbn = isbn != null ? isbn : "";
        this.category = category;
        this.condition = condition;
        this.ownerUsername = ownerUsername;
        this.contactInfo = contactInfo;
        this.isAvailable = true;
        this.description = "";
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return title + " by " + author + " (" + condition + ")";
    }
}

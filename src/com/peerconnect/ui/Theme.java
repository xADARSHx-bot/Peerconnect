package com.peerconnect.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

/**
 * Comprehensive theme configuration for PeerConnect+ application using FlatLaf.
 * This class provides a modern, consistent look and feel across all UI components.
 */
public class Theme {

    // Application color palette
    public static final Color PRIMARY_COLOR = new Color(0, 123, 255);         // Main blue
    public static final Color SECONDARY_COLOR = new Color(108, 117, 125);     // Gray
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);         // Green
    public static final Color DANGER_COLOR = new Color(220, 53, 69);          // Red
    public static final Color WARNING_COLOR = new Color(255, 193, 7);         // Yellow
    public static final Color INFO_COLOR = new Color(23, 162, 184);           // Cyan
    
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250);    // Light background
    public static final Color CARD_BACKGROUND = Color.WHITE;                   // White cards
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);           // Dark text
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);      // Gray text
    public static final Color BORDER_COLOR = new Color(222, 226, 230);        // Light borders

    public static void apply() {
        try {
            // Initialize FlatLaf
            FlatLightLaf.setup();

            // --- Global Application Settings ---
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Viewport.background", BACKGROUND_COLOR);
            UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
            
            // --- Text Components ---
            UIManager.put("Label.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.background", CARD_BACKGROUND);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            
            // --- Buttons ---
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.arc", 12);
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));

            // --- Tables ---
            UIManager.put("Table.background", CARD_BACKGROUND);
            UIManager.put("Table.selectionBackground", PRIMARY_COLOR.brighter());
            UIManager.put("Table.selectionForeground", Color.WHITE);

            // Set global font
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            setUIFont(defaultFont);

        } catch (Exception ex) {
            System.err.println("Error applying theme: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sets the default font for all UI components.
     */
    private static void setUIFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }

    /**
     * Creates a styled panel with modern appearance.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    /**
     * Creates a styled text field with modern appearance.
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40));
        return field;
    }

    /**
     * Creates a styled password field with modern appearance.
     */
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40));
        return field;
    }
}

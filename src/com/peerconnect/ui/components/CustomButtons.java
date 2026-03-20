package com.peerconnect.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JButton that is pre-styled with the application's theme colors,
 * a modern font, and rounded corners. This ensures all buttons in the
 * application have a consistent look and feel.
 */
public class CustomButtons extends JButton {

    /**
     * Defines the visual style of the button, typically for primary or secondary actions.
     */
    public enum ButtonType {
        PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING
    }

    private Color primaryColor = new Color(0, 123, 255); // The main blue color
    private Color secondaryColor = new Color(108, 117, 125); // Gray color
    private Color successColor = new Color(40, 167, 69); // Green color
    private Color dangerColor = new Color(220, 53, 69); // Red color
    private Color warningColor = new Color(255, 193, 7); // Yellow color

    /**
     * Constructs a new CustomButtons.
     * @param text The text to display on the button.
     * @param type The visual style (PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING).
     */
    public CustomButtons(String text, ButtonType type) {
        super(text);

        // --- Basic Styling ---
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Auto-size button to fit text with padding
        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int preferredWidth = Math.max(textWidth + 40, 90); // 40px padding, minimum 90px
        setPreferredSize(new Dimension(preferredWidth, 40));
        setMinimumSize(new Dimension(preferredWidth, 40));
        setMaximumSize(new Dimension(preferredWidth + 20, 40)); // Allow slight expansion

        // Remove default button painting and borders
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false); // We will handle the painting ourselves

        // Set the background color based on the button type
        switch (type) {
            case PRIMARY:
                setBackground(primaryColor);
                break;
            case SECONDARY:
                setBackground(secondaryColor);
                break;
            case SUCCESS:
                setBackground(successColor);
                break;
            case DANGER:
                setBackground(dangerColor);
                break;
            case WARNING:
                setBackground(warningColor);
                setForeground(new Color(33, 33, 33)); // Dark text for yellow background
                break;
            default:
                setBackground(primaryColor);
                break;
        }

        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(originalColor.darker());
                repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(originalColor);
                repaint();
            }
        });
    }
    
    /**
     * Overrides the default paintComponent to draw a rounded rectangle background
     * for the button.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smooth corners
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set the color to this button's background color
        g2.setColor(getBackground());
        
        // Paint a rounded rectangle
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        
        g2.dispose();
        
        // Let the parent class (JButton) handle painting the text
        super.paintComponent(g);
    }
}
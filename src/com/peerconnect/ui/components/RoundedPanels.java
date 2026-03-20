package com.peerconnect.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel that has rounded corners, providing a more modern aesthetic
 * than the default square-cornered JPanel.
 */
public class RoundedPanels extends JPanel {
    private int cornerRadius;
    private Color shadowColor;
    private boolean hasShadow;

    /**
     * Constructs a new RoundedPAnels with a specified corner radius.
     * @param radius The radius of the corner arcs.
     */
    public RoundedPanels(int radius) {
        this(radius, null, false);
    }
    
    /**
     * Constructs a new RoundedPAnels with a specified corner radius and layout manager.
     * @param radius The radius of the corner arcs.
     * @param layout The LayoutManager to use for this panel.
     */
    public RoundedPanels(int radius, LayoutManager layout) {
        this(radius, layout, false);
    }
    
    /**
     * Constructs a new RoundedPAnels with a specified corner radius, layout manager, and shadow option.
     * @param radius The radius of the corner arcs.
     * @param layout The LayoutManager to use for this panel.
     * @param hasShadow Whether to draw a shadow effect.
     */
    public RoundedPanels(int radius, LayoutManager layout, boolean hasShadow) {
        super(layout);
        this.cornerRadius = radius;
        this.hasShadow = hasShadow;
        this.shadowColor = new Color(0, 0, 0, 30); // Semi-transparent black
        // Make the panel transparent so we can draw our own rounded background
        setOpaque(false);
    }

    /**
     * Sets whether this panel should have a shadow effect.
     * @param hasShadow true to enable shadow, false to disable
     */
    public void setShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        repaint();
    }

    /**
     * Sets the shadow color.
     * @param shadowColor the color to use for the shadow
     */
    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        if (hasShadow) {
            repaint();
        }
    }

    /**
     * Sets the corner radius.
     * @param radius the new corner radius
     */
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    /**
     * Overrides the default paintComponent method to draw a rounded rectangle
     * instead of a square one.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        // Enable anti-aliasing for smooth corners
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw shadow if enabled
        if (hasShadow) {
            graphics.setColor(shadowColor);
            graphics.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);
        }
        
        // Set the color to the panel's background color
        graphics.setColor(getBackground());
        
        // Draw the rounded rectangle background
        graphics.fillRoundRect(0, 0, getWidth() - (hasShadow ? 3 : 1), getHeight() - (hasShadow ? 3 : 1), cornerRadius, cornerRadius);
        
        // Optional: Draw border
        if (getBorder() != null || hasShadow) {
            graphics.setColor(new Color(200, 200, 200, 150));
            graphics.setStroke(new BasicStroke(1f));
            graphics.drawRoundRect(0, 0, getWidth() - (hasShadow ? 4 : 2), getHeight() - (hasShadow ? 4 : 2), cornerRadius, cornerRadius);
        }
    }
}
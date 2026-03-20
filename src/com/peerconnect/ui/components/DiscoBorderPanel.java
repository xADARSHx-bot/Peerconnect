package com.peerconnect.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JPanel with animated disco color-changing borders for fun visual effects
 */
public class DiscoBorderPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private Timer animationTimer;
    private float hue = 0.0f;
    private int borderThickness = 4;
    private int cornerRadius = 15;
    private Color currentBorderColor;

    public DiscoBorderPanel(LayoutManager layout) {
        super(layout);
        initializeAnimation();
        setOpaque(false); // Make background transparent so we can draw custom border
    }

    public DiscoBorderPanel(LayoutManager layout, int cornerRadius) {
        super(layout);
        this.cornerRadius = cornerRadius;
        initializeAnimation();
        setOpaque(false);
    }

    private void initializeAnimation() {
        // Create timer to update colors every 25ms (40 FPS) for faster animation
        animationTimer = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Increment hue faster for rapid rainbow effect
                hue += 0.06f;
                if (hue > 1.0f) {
                    hue = 0.0f;
                }

                // Create brighter, more vibrant disco colors
                currentBorderColor = Color.getHSBColor(hue, 1.0f, 1.0f);

                // Repaint the border
                repaint();
            }
        });

        // Start the animation
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth borders
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the entire background with an animated gradient (disco effect)
        Color bg1 = (currentBorderColor != null) ? currentBorderColor : Color.getHSBColor(hue, 1.0f, 1.0f);
        Color bg2 = Color.getHSBColor((hue + 0.3f) % 1.0f, 1.0f, 1.0f);
        GradientPaint bgGradient = new GradientPaint(0, 0, bg1, getWidth(), getHeight(), bg2);
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw the animated border with gradient effect
        if (currentBorderColor != null) {
            // Create a gradient for extra disco effect with brighter colors
            Color color1 = currentBorderColor;
            Color color2 = Color.getHSBColor((hue + 0.3f) % 1.0f, 1.0f, 1.0f);

            GradientPaint gradient = new GradientPaint(
                0, 0, color1,
                getWidth(), getHeight(), color2
            );

            g2d.setPaint(gradient);
            g2d.setStroke(new BasicStroke(borderThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Draw rounded rectangle border
            g2d.drawRoundRect(borderThickness/2, borderThickness/2,
                             getWidth() - borderThickness,
                             getHeight() - borderThickness,
                             cornerRadius, cornerRadius);

            // Add inner glow effect
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Color glowColor = new Color(currentBorderColor.getRed(),
                                       currentBorderColor.getGreen(),
                                       currentBorderColor.getBlue(), 60);
            g2d.setColor(glowColor);
            g2d.drawRoundRect(borderThickness + 1, borderThickness + 1,
                             getWidth() - 2 * (borderThickness + 1),
                             getHeight() - 2 * (borderThickness + 1),
                             cornerRadius, cornerRadius);
        }

        g2d.dispose();
        super.paintComponent(g);
    }

    @Override
    public Insets getInsets() {
        // Add padding for the border
        return new Insets(borderThickness + 5, borderThickness + 5,
                         borderThickness + 5, borderThickness + 5);
    }

    /**
     * Stop the disco animation (call this when panel is no longer visible)
     */
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    /**
     * Start the disco animation
     */
    public void startAnimation() {
        if (animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    /**
     * Set the speed of the color animation
     * @param speed higher values = faster animation (default is 0.02f)
     */
    public void setAnimationSpeed(float speed) {
        // Stop current timer
        if (animationTimer != null) {
            animationTimer.stop();
        }

        // Create new timer with updated speed (faster refresh rate)
        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hue += speed;
                if (hue > 1.0f) {
                    hue = 0.0f;
                }
                currentBorderColor = Color.getHSBColor(hue, 1.0f, 1.0f);
                repaint();
            }
        });

        animationTimer.start();
    }
}
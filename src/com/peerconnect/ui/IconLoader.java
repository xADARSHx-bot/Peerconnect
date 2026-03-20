package com.peerconnect.ui;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IconLoader {
    private static final Map<String, ImageIcon> cache = new HashMap<>();
    
    // Icon file mappings - using absolute paths
    private static final Map<String, String> iconFiles = new HashMap<String, String>() {{
        String projectRoot = System.getProperty("user.dir");
        
        // Status icons
        put("checkmark", projectRoot + java.io.File.separator + "checkmark.png");
        put("lock", projectRoot + java.io.File.separator + "lock.png");
        put("target", projectRoot + java.io.File.separator + "target mark.png");
        put("trash", projectRoot + java.io.File.separator + "trash-bin.png");
        
        // Stats icons
        put("document", projectRoot + java.io.File.separator + "document.png");
        put("hourglass", projectRoot + java.io.File.separator + "hour-glass.png");
        put("statistics", projectRoot + java.io.File.separator + "statistics (1).png");
        
        // UI elements
        put("star", projectRoot + java.io.File.separator + "star-removebg-preview.png");
        
        // Medals
        put("medal1", projectRoot + java.io.File.separator + "1st place.png");
        put("medal2", projectRoot + java.io.File.separator + "2nd place.png");
        put("medal3", projectRoot + java.io.File.separator + "3rd place.png");
    }};
    
    /**
     * Load an icon and scale it to the specified size
     * @param iconName The icon name (e.g., "checkmark", "medal1")
     * @param size The desired size in pixels (will be square)
     * @return ImageIcon or null if loading fails
     */
    public static ImageIcon loadIcon(String iconName, int size) {
        String cacheKey = iconName + "_" + size;
        
        // Check cache first
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        // Get filename
        String filename = iconFiles.get(iconName);
        if (filename == null) {
            System.err.println("Unknown icon name: " + iconName);
            return null;
        }
        
        try {
            File imageFile = new File(filename);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                if (originalIcon.getIconWidth() > 0 && originalIcon.getIconHeight() > 0) {
                    // Scale to desired size
                    Image scaledImage = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    
                    // Cache the result
                    cache.put(cacheKey, scaledIcon);
                    return scaledIcon;
                } else {
                    System.err.println("Invalid image dimensions for: " + filename);
                }
            } else {
                System.err.println("Image file not found: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconName + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Load an icon without scaling (original size)
     * @param iconName The icon name
     * @return ImageIcon or null if loading fails
     */
    public static ImageIcon loadIcon(String iconName) {
        String filename = iconFiles.get(iconName);
        if (filename == null) {
            System.err.println("Unknown icon name: " + iconName);
            return null;
        }
        
        try {
            File imageFile = new File(filename);
            if (imageFile.exists()) {
                return new ImageIcon(imageFile.getAbsolutePath());
            } else {
                System.err.println("Image file not found: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + iconName + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Preload commonly used icons to improve performance
     */
    public static void preloadIcons() {
        // Preload common sizes
        String[] commonIcons = {"checkmark", "lock", "target", "medal1", "medal2", "medal3"};
        int[] commonSizes = {16, 20, 24};
        
        for (String icon : commonIcons) {
            for (int size : commonSizes) {
                loadIcon(icon, size);
            }
        }
    }
}
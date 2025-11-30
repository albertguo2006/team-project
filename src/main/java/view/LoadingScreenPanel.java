package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * LoadingScreenPanel displays a warning screen with text and audio.
 * 
 * Responsibilities:
 * - Display California Prop. 65 warning text
 * - Play warning siren audio
 * - Automatically transition to main menu after a set duration
 * - Provide visual feedback during loading
 * 
 * This panel serves as an initial warning screen before the main menu.
 */
public class LoadingScreenPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 50, 50);  // Red for warning
    private static final int DISPLAY_DURATION_MS = 5000;  // 5 seconds

    private final AudioManager audioManager;
    private Runnable onComplete;
    private Timer transitionTimer;
    
    /**
     * Constructs the loading screen panel.
     */
    public LoadingScreenPanel() {
        setBackground(BACKGROUND_COLOR);
        this.audioManager = AudioManager.getInstance();
    }
    
    /**
     * Sets the callback to run when the loading screen completes.
     * @param onComplete callback to execute when loading is done
     */
    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        System.out.println("LoadingScreenPanel.setOnComplete() called, callback is: " +
                          (onComplete != null ? "NOT NULL" : "NULL"));
    }
    
    /**
     * Starts the loading screen sequence (play audio and show warning).
     * Uses invokeLater to ensure it starts after the panel is fully displayed.
     */
    public void start() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("LoadingScreenPanel.start() called at: " + System.currentTimeMillis());
            playWarningAudio();
            startTransitionTimer();
        });
    }
    
    /**
     * Plays the warning siren audio.
     * Audio playback is optional and will fail gracefully if not available.
     */
    private void playWarningAudio() {
        // Use AudioManager to play the siren sound effect
        // Try MP3 first (JavaFX supports it natively), fall back to WAV
        audioManager.playSoundEffect("/audio/siren_cropped.mp3");
        System.out.println("Warning audio playback requested");
    }
    
    /**
     * Starts a timer to transition to the main menu after the display duration.
     */
    private void startTransitionTimer() {
        long startTime = System.currentTimeMillis();
        System.out.println("Timer starting at: " + startTime + " for " + DISPLAY_DURATION_MS + "ms");
        
        transitionTimer = new Timer(DISPLAY_DURATION_MS, e -> {
            long endTime = System.currentTimeMillis();
            long actualDuration = endTime - startTime;
            System.out.println("Timer fired at: " + endTime + " (actual duration: " + actualDuration + "ms)");
            System.out.println("onComplete is: " + (onComplete != null ? "NOT NULL" : "NULL"));

            // Audio will stop on its own when the sound effect ends
            if (onComplete != null) {
                System.out.println("Calling onComplete callback");
                SwingUtilities.invokeLater(() -> {
                    onComplete.run();
                });
            } else {
                System.err.println("ERROR: onComplete callback is null! Cannot transition from loading screen.");
            }
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }
    
    /**
     * Cleans up resources when panel is no longer needed.
     */
    public void cleanup() {
        if (transitionTimer != null) {
            transitionTimer.stop();
        }
        // AudioManager handles its own cleanup
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable anti-aliasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Calculate center position
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Draw warning icon (triangle)
        drawWarningTriangle(g2d, centerX, centerY - 150);
        
        // Draw warning text
        drawWarningText(g2d, centerX, centerY);
    }
    
    /**
     * Draws a warning triangle icon.
     * @param g2d the Graphics2D context
     * @param centerX the x-coordinate of center
     * @param centerY the y-coordinate of center
     */
    private void drawWarningTriangle(Graphics2D g2d, int centerX, int centerY) {
        // Draw yellow triangle with exclamation mark
        g2d.setColor(new Color(255, 215, 0));
        int[] xPoints = {centerX, centerX - 40, centerX + 40};
        int[] yPoints = {centerY - 40, centerY + 30, centerY + 30};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Draw exclamation mark
        g2d.setColor(Color.BLACK);
        g2d.fillRect(centerX - 5, centerY - 20, 10, 30);
        g2d.fillOval(centerX - 5, centerY + 15, 10, 10);
    }
    
    /**
     * Draws the warning text with proper formatting.
     * @param g2d the Graphics2D context
     * @param centerX the x-coordinate of center
     * @param centerY the y-coordinate of center
     */
    private void drawWarningText(Graphics2D g2d, int centerX, int centerY) {
        // Set up fonts
        Font normalFont = new Font("Arial", Font.PLAIN, 22);
        Font boldFont = new Font("Arial", Font.BOLD, 22);
        
        // Text positioning
        int yOffset = centerY + 50;
        
        // Build complete lines to measure them properly
        String line2 = "which is known to the State of California to cause";
        String line3 = "cancer or birth defects or other reproductive harm";
        
        // Line 1: "This game can expose you to " + "Chronic Stress"
        g2d.setFont(normalFont);
        String line1Part1 = "This game can expose you to ";
        String line1Part2 = "Chronic Stress,";
        
        // Calculate total width for centering
        int line1Width = g2d.getFontMetrics(normalFont).stringWidth(line1Part1) +
                         g2d.getFontMetrics(boldFont).stringWidth(line1Part2);
        int x1Start = centerX - line1Width / 2;
        
        // Draw first part
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(line1Part1, x1Start, yOffset);
        
        // Draw bold part
        g2d.setFont(boldFont);
        g2d.setColor(HIGHLIGHT_COLOR);
        int x1Bold = x1Start + g2d.getFontMetrics(normalFont).stringWidth(line1Part1);
        g2d.drawString(line1Part2, x1Bold, yOffset);
        
        // Line 2: ", which is known to the State of California to cause"
        yOffset += 35;
        g2d.setFont(normalFont);
        g2d.setColor(TEXT_COLOR);
        int x2 = centerX - g2d.getFontMetrics().stringWidth(line2) / 2;
        g2d.drawString(line2, x2, yOffset);
        
        // Line 3: Bold part: "cancer or birth defects or other reproductive harm"
        yOffset += 35;
        g2d.setFont(boldFont);
        g2d.setColor(HIGHLIGHT_COLOR);
        int x3 = centerX - g2d.getFontMetrics().stringWidth(line3) / 2;
        g2d.drawString(line3, x3, yOffset);
    }
}
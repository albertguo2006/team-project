package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import entity.GameEnding;
import interface_adapter.ViewManagerModel;
import interface_adapter.sleep.SleepViewModel;

/**
 * View for displaying the game ending after completing the week.
 * Shows the ending type, message, and final balance.
 */
public class EndGameView extends JPanel implements PropertyChangeListener {
    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1200;
    
    private final SleepViewModel sleepViewModel;
    private final ViewManagerModel viewManagerModel;
    private final JPanel mainPanel;
    
    // Fade animation
    private float fadeAlpha = 0.0f;
    private Timer fadeTimer;
    private boolean fadingIn = true;
    
    /**
     * Constructs an EndGameView.
     * 
     * @param sleepViewModel the sleep view model
     * @param viewManagerModel the view manager model
     * @param mainPanel the main panel with CardLayout
     */
    public EndGameView(SleepViewModel sleepViewModel,
                       ViewManagerModel viewManagerModel,
                       JPanel mainPanel) {
        this.sleepViewModel = sleepViewModel;
        this.viewManagerModel = viewManagerModel;
        this.mainPanel = mainPanel;
        
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Listen for property changes
        sleepViewModel.addPropertyChangeListener(this);
        
        // Handle ENTER key to return to main menu
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && fadeAlpha >= 1.0f) {
                    fadeOutAndReturnToMenu();
                }
            }
        });
        
        // Start fade-in animation when view becomes visible
        startFadeIn();
    }
    
    /**
     * Starts the fade-in animation.
     */
    private void startFadeIn() {
        fadingIn = true;
        fadeAlpha = 0.0f;
        
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        
        fadeTimer = new Timer(16, e -> {
            fadeAlpha += 0.02f;
            if (fadeAlpha >= 1.0f) {
                fadeAlpha = 1.0f;
                fadeTimer.stop();
            }
            repaint();
        });
        fadeTimer.start();
    }
    
    /**
     * Fades out and returns to main menu.
     */
    private void fadeOutAndReturnToMenu() {
        fadingIn = false;
        
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        
        fadeTimer = new Timer(16, e -> {
            fadeAlpha -= 0.02f;
            if (fadeAlpha <= 0.0f) {
                fadeAlpha = 0.0f;
                fadeTimer.stop();
                returnToMainMenu();
            }
            repaint();
        });
        fadeTimer.start();
    }
    
    /**
     * Returns to the main menu.
     */
    private void returnToMainMenu() {
        viewManagerModel.setState("menu");
        viewManagerModel.firePropertyChange();
        
        // Switch to main menu
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "menu");
        
        // Request focus on menu panel
        for (java.awt.Component comp : mainPanel.getComponents()) {
            if (comp instanceof MainMenuPanel) {
                comp.requestFocusInWindow();
                break;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        GameEnding ending = sleepViewModel.getEnding();
        if (ending == null) {
            return;
        }
        
        // Apply fade alpha to all colors
        int alpha = (int) (255 * fadeAlpha);
        
        // Draw centered content
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Title: "Week Complete!"
        g2d.setColor(new Color(255, 215, 0, alpha)); // Gold color
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        String weekComplete = "Week Complete!";
        int weekWidth = g2d.getFontMetrics().stringWidth(weekComplete);
        g2d.drawString(weekComplete, centerX - weekWidth / 2, centerY - 250);
        
        // Ending title
        Color endingColor = getEndingColor(ending.getType(), alpha);
        g2d.setColor(endingColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        String title = ending.getTitle();
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, centerX - titleWidth / 2, centerY - 120);
        
        // Ending message (wrapped if too long)
        g2d.setColor(new Color(255, 255, 255, alpha));
        g2d.setFont(new Font("Arial", Font.PLAIN, 32));
        String message = ending.getMessage();
        drawWrappedText(g2d, message, centerX, centerY, 800);
        
        // Final balance
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        String balance = String.format("Final Balance: $%.2f", ending.getFinalBalance());
        int balanceWidth = g2d.getFontMetrics().stringWidth(balance);
        g2d.drawString(balance, centerX - balanceWidth / 2, centerY + 150);
        
        // Return prompt (only show when fully faded in)
        if (fadeAlpha >= 1.0f && fadingIn) {
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            String prompt = "Press ENTER to return to Main Menu";
            int promptWidth = g2d.getFontMetrics().stringWidth(prompt);
            g2d.drawString(prompt, centerX - promptWidth / 2, centerY + 250);
        }
    }
    
    /**
     * Gets the color for the ending type.
     */
    private Color getEndingColor(GameEnding.EndingType type, int alpha) {
        switch (type) {
            case WEALTHY:
                return new Color(255, 215, 0, alpha); // Gold
            case COMFORTABLE:
                return new Color(100, 200, 100, alpha); // Light green
            case STRUGGLING:
                return new Color(255, 200, 100, alpha); // Orange
            case BROKE:
                return new Color(255, 100, 100, alpha); // Red
            default:
                return new Color(255, 255, 255, alpha);
        }
    }
    
    /**
     * Draws wrapped text centered at the given position.
     */
    private void drawWrappedText(Graphics2D g2d, String text, int centerX, int centerY, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int y = centerY;
        int lineHeight = g2d.getFontMetrics().getHeight();
        
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int testWidth = g2d.getFontMetrics().stringWidth(testLine);
            
            if (testWidth > maxWidth && line.length() > 0) {
                // Draw current line
                int lineWidth = g2d.getFontMetrics().stringWidth(line.toString());
                g2d.drawString(line.toString(), centerX - lineWidth / 2, y);
                y += lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        // Draw last line
        if (line.length() > 0) {
            int lineWidth = g2d.getFontMetrics().stringWidth(line.toString());
            g2d.drawString(line.toString(), centerX - lineWidth / 2, y);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // When sleep view model changes, repaint
        if (fadeTimer == null || !fadeTimer.isRunning()) {
            startFadeIn();
        }
        repaint();
    }
}
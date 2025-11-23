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

import entity.DaySummary;
import interface_adapter.ViewManagerModel;
import interface_adapter.sleep.SleepViewModel;

/**
 * View for displaying the day summary after sleeping.
 * Shows earnings, spending, and prompts user to continue to next day.
 */
public class DaySummaryView extends JPanel implements PropertyChangeListener {
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
     * Constructs a DaySummaryView.
     * 
     * @param sleepViewModel the sleep view model
     * @param viewManagerModel the view manager model
     * @param mainPanel the main panel with CardLayout
     */
    public DaySummaryView(SleepViewModel sleepViewModel, 
                          ViewManagerModel viewManagerModel,
                          JPanel mainPanel) {
        this.sleepViewModel = sleepViewModel;
        this.viewManagerModel = viewManagerModel;
        this.mainPanel = mainPanel;
        
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Listen for property changes
        sleepViewModel.addPropertyChangeListener(this);
        
        // Handle ENTER key to continue
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && fadeAlpha >= 1.0f) {
                    fadeOutAndReturn();
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
     * Fades out and returns to game.
     */
    private void fadeOutAndReturn() {
        fadingIn = false;
        
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        
        fadeTimer = new Timer(16, e -> {
            fadeAlpha -= 0.02f;
            if (fadeAlpha <= 0.0f) {
                fadeAlpha = 0.0f;
                fadeTimer.stop();
                returnToGame();
            }
            repaint();
        });
        fadeTimer.start();
    }
    
    /**
     * Returns to the game view.
     */
    private void returnToGame() {
        viewManagerModel.setState("game");
        viewManagerModel.firePropertyChange();
        
        // Switch to game view
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "game");
        
        // Request focus on game panel
        for (java.awt.Component comp : mainPanel.getComponents()) {
            if (comp instanceof GamePanel) {
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
        
        DaySummary summary = sleepViewModel.getCurrentSummary();
        if (summary == null) {
            return;
        }
        
        // Apply fade alpha to all colors
        int alpha = (int) (255 * fadeAlpha);
        
        // Draw centered content
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Title
        g2d.setColor(new Color(255, 255, 255, alpha));
        g2d.setFont(new Font("Arial", Font.BOLD, 64));
        String title = "Day Complete: " + summary.getCompletedDay().getDisplayName();
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, centerX - titleWidth / 2, centerY - 200);
        
        // Financial summary
        g2d.setFont(new Font("Arial", Font.PLAIN, 40));
        
        // Earnings (green)
        g2d.setColor(new Color(0, 255, 0, alpha));
        String earnings = String.format("Earnings: $%.2f", summary.getEarnings());
        int earningsWidth = g2d.getFontMetrics().stringWidth(earnings);
        g2d.drawString(earnings, centerX - earningsWidth / 2, centerY - 80);
        
        // Spending (red)
        g2d.setColor(new Color(255, 100, 100, alpha));
        String spending = String.format("Spending: $%.2f", summary.getSpending());
        int spendingWidth = g2d.getFontMetrics().stringWidth(spending);
        g2d.drawString(spending, centerX - spendingWidth / 2, centerY - 20);
        
        // Net change (color depends on positive/negative)
        double netChange = summary.getNetChange();
        if (netChange >= 0) {
            g2d.setColor(new Color(0, 255, 0, alpha));
        } else {
            g2d.setColor(new Color(255, 100, 100, alpha));
        }
        String net = String.format("Net Change: %s$%.2f", netChange >= 0 ? "+" : "", netChange);
        int netWidth = g2d.getFontMetrics().stringWidth(net);
        g2d.drawString(net, centerX - netWidth / 2, centerY + 40);
        
        // New balance
        g2d.setColor(new Color(255, 255, 255, alpha));
        String balance = String.format("New Balance: $%.2f", summary.getNewBalance());
        int balanceWidth = g2d.getFontMetrics().stringWidth(balance);
        g2d.drawString(balance, centerX - balanceWidth / 2, centerY + 100);
        
        // Continue prompt (only show when fully faded in)
        if (fadeAlpha >= 1.0f && fadingIn) {
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            String nextDay = sleepViewModel.getNewDay() != null ? 
                           sleepViewModel.getNewDay().getDisplayName() : "Next Day";
            String prompt = "Press ENTER to continue to " + nextDay;
            int promptWidth = g2d.getFontMetrics().stringWidth(prompt);
            g2d.drawString(prompt, centerX - promptWidth / 2, centerY + 200);
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
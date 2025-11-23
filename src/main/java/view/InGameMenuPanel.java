package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * InGameMenuPanel is the pause menu displayed during gameplay.
 * 
 * Responsibilities:
 * - Display pause menu title
 * - Provide menu options: Resume, Save, Settings, Save & Exit
 * - Handle button clicks through action listeners
 * - Semi-transparent overlay effect
 * 
 * This panel appears as an overlay when the player presses ESC during gameplay.
 */
public class InGameMenuPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 220);  // Semi-transparent
    private static final Color TITLE_COLOR = new Color(255, 215, 0);  // Gold
    private static final Color BUTTON_COLOR = new Color(60, 60, 80);
    private static final Color BUTTON_HOVER_COLOR = new Color(80, 80, 120);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    private final JButton resumeButton;
    private final JButton saveButton;
    private final JButton settingsButton;
    private final JButton saveAndExitButton;
    private final JButton payBillsButton; // New bills button
    
    /**
     * Constructs the in-game menu panel with all menu buttons.
     */
    public InGameMenuPanel() {
        setOpaque(false);  // Allow transparency
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Add vertical glue to center content
        add(Box.createVerticalGlue());
        
        // Title
        JLabel titleLabel = createTitleLabel();
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        
        add(Box.createRigidArea(new Dimension(0, 60)));
        
        // Menu buttons
        resumeButton = createMenuButton("Resume Game");
        saveButton = createMenuButton("Save Game");
        settingsButton = createMenuButton("Settings");
        saveAndExitButton = createMenuButton("Save & Exit");
        payBillsButton = createMenuButton("Pay Bills");
        
        add(resumeButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(saveButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(settingsButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(saveAndExitButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(payBillsButton);
        
        // Add vertical glue at bottom
        add(Box.createVerticalGlue());
    }
    
    /**
     * Creates the pause menu title label.
     * @return configured title JLabel
     */
    private JLabel createTitleLabel() {
        JLabel label = new JLabel("PAUSED");
        label.setFont(new Font("Arial", Font.BOLD, 64));
        label.setForeground(TITLE_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Creates a styled menu button.
     * @param text the button text
     * @return configured JButton
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set fixed size for all buttons
        Dimension buttonSize = new Dimension(300, 60);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        
        button.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Adds an action listener to the Resume button.
     * @param listener the action listener to add
     */
    public void addResumeListener(ActionListener listener) {
        resumeButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Save button.
     * @param listener the action listener to add
     */
    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Settings button.
     * @param listener the action listener to add
     */
    public void addSettingsListener(ActionListener listener) {
        settingsButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Save & Exit button.
     * @param listener the action listener to add
     */
    public void addSaveAndExitListener(ActionListener listener) {
        saveAndExitButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the Pay Bills button
     * @param listener the action listener to add
     */
    public void addPayBillsListener(ActionListener listener) {
        payBillsButton.addActionListener(listener);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Draw semi-transparent background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        super.paintComponent(g);
    }
}
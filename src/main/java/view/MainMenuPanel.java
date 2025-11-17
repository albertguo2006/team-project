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
 * MainMenuPanel is the initial screen displayed when the application starts.
 * 
 * Responsibilities:
 * - Display game title and branding
 * - Provide menu options: New Game, Load Game, Settings, Exit
 * - Handle button clicks through action listeners
 * - Maintain visual consistency with the game's aesthetic
 * 
 * This panel uses a vertical BoxLayout for menu items and provides
 * callbacks for each menu action.
 */
public class MainMenuPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    private static final Color TITLE_COLOR = new Color(255, 215, 0);  // Gold
    private static final Color BUTTON_COLOR = new Color(60, 60, 80);
    private static final Color BUTTON_HOVER_COLOR = new Color(80, 80, 120);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    private final JButton newGameButton;
    private final JButton loadGameButton;
    private final JButton settingsButton;
    private final JButton exitButton;
    
    /**
     * Constructs the main menu panel with all menu buttons.
     * Buttons are created but not wired to actions - use addActionListeners to connect them.
     */
    public MainMenuPanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Add vertical glue to center content
        add(Box.createVerticalGlue());
        
        // Title
        JLabel titleLabel = createTitleLabel();
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        
        add(Box.createRigidArea(new Dimension(0, 60)));
        
        // Menu buttons
        newGameButton = createMenuButton("New Game");
        loadGameButton = createMenuButton("Load Game");
        settingsButton = createMenuButton("Settings");
        exitButton = createMenuButton("Exit");
        
        add(newGameButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(loadGameButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(settingsButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(exitButton);
        
        // Add vertical glue at bottom
        add(Box.createVerticalGlue());
        
        // Add version label at bottom
        JLabel versionLabel = createVersionLabel();
        versionLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(versionLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    /**
     * Creates the game title label.
     * @return configured title JLabel
     */
    private JLabel createTitleLabel() {
        JLabel label = new JLabel("California Prop. 65");
        label.setFont(new Font("Arial", Font.BOLD, 64));
        label.setForeground(TITLE_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Creates a version label for the bottom of the menu.
     * @return configured version JLabel
     */
    private JLabel createVersionLabel() {
        JLabel label = new JLabel("Version 1.0.0");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(new Color(150, 150, 150));
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
     * Adds an action listener to the New Game button.
     * @param listener the action listener to add
     */
    public void addNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Load Game button.
     * @param listener the action listener to add
     */
    public void addLoadGameListener(ActionListener listener) {
        loadGameButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Settings button.
     * @param listener the action listener to add
     */
    public void addSettingsListener(ActionListener listener) {
        settingsButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the Exit button.
     * @param listener the action listener to add
     */
    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable anti-aliasing for smoother rendering
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw decorative elements (optional)
        drawDecorations(g2d);
    }
    
    /**
     * Draws decorative elements on the menu background.
     * @param g2d the Graphics2D context
     */
    private void drawDecorations(Graphics2D g2d) {
        // Draw subtle grid pattern
        g2d.setColor(new Color(40, 40, 60, 50));
        int gridSize = 50;
        
        for (int x = 0; x < getWidth(); x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }
}
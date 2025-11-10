package view;

import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;
import use_case.Direction;
import entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GamePanel is the main 2D game view and contains the real-time game loop.
 * 
 * Responsibilities:
 * - Renders the game state (player, background, UI)
 * - Maintains the javax.swing.Timer game loop (~60 FPS)
 * - Orchestrates the update/render cycle
 * - Acts as the focal point for the game window
 * 
 * Architecture:
 * - Extends JPanel for rendering capability
 * - Implements ActionListener to handle Timer ticks
 * - Receives PlayerMovementUseCase as dependency injection
 * - Attaches PlayerInputController as KeyListener
 * 
 * The Game Loop Flow:
 * 1. Timer fires every 16ms (approximately 60 FPS)
 * 2. actionPerformed() is called
 * 3. UPDATE PHASE: playerMovementUseCase.updatePosition(deltaTime)
 * 4. RENDER PHASE: this.repaint() schedules paintComponent()
 * 5. On EDT: paintComponent(Graphics g) renders the frame
 */
public class GamePanel extends JPanel implements ActionListener {
    
    private final PlayerMovementUseCase playerMovementUseCase;
    private final Timer gameTimer;
    
    // Game loop timing
    private static final int FRAME_DELAY_MS = 16;  // ~60 FPS (1000ms / 60 ≈ 16.67ms)
    private long lastUpdateTime;
    
    // Rendering constants
    private static final Color BACKGROUND_COLOR = new Color(34, 139, 34);  // Forest green
    private static final Color PLAYER_COLOR = Color.BLUE;
    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 32;
    
    /**
     * Constructs a GamePanel with the given use case and input controller.
     * Initializes the game loop timer and sets up the panel.
     * 
     * @param playerMovementUseCase the use case managing player movement
     * @param playerInputController the controller handling keyboard input
     */
    public GamePanel(PlayerMovementUseCase playerMovementUseCase, 
                     PlayerInputController playerInputController) {
        this.playerMovementUseCase = playerMovementUseCase;
        
        // Set panel properties
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(BACKGROUND_COLOR);
        this.setFocusable(true);
        
        // Attach the input controller as a KeyListener
        this.addKeyListener(playerInputController);
        
        // Initialize the game loop timer
        // ActionListener is 'this', so actionPerformed() will be called each tick
        this.gameTimer = new Timer(FRAME_DELAY_MS, this);
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Starts the game loop.
     * Called after the frame is made visible.
     */
    public void startGameLoop() {
        gameTimer.start();
    }
    
    /**
     * Stops the game loop.
     * Useful for pausing or closing the game.
     */
    public void stopGameLoop() {
        gameTimer.stop();
    }
    
    /**
     * Called by the Timer on each game loop tick (~every 16ms).
     * 
     * This method orchestrates the game loop:
     * 1. Calculate elapsed time since last update
     * 2. UPDATE PHASE: Update game state
     * 3. RENDER PHASE: Request a repaint
     * 
     * @param e the ActionEvent from the Timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Calculate delta time in seconds
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        
        // Cap delta time to prevent huge jumps if frame is delayed
        if (deltaTime > 0.05) {  // More than 50ms = cap at 50ms
            deltaTime = 0.05;
        }
        
        // === UPDATE PHASE ===
        playerMovementUseCase.updatePosition(deltaTime);
        
        // === RENDER PHASE ===
        // Schedule a repaint on the Event Dispatch Thread
        // This will eventually call paintComponent()
        this.repaint();
    }
    
    /**
     * Renders the current game state.
     * Called by the Swing EDT after repaint() is called.
     * 
     * Rendering order:
     * 1. Clear background (handled by Swing)
     * 2. Draw game objects
     * 3. Draw UI elements
     * 
     * @param g the Graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Call parent to handle background and borders
        super.paintComponent(g);
        
        // Cast to Graphics2D for more features (optional)
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // === Draw Game Objects ===
        drawPlayer(g2d);
        
        // === Draw UI ===
        drawUI(g2d);
    }
    
    /**
     * Draws the player character at the current position.
     * 
     * @param g the Graphics2D context
     */
    private void drawPlayer(Graphics2D g) {
        Player player = playerMovementUseCase.getPlayer();
        
        // Get player position from entity
        int playerX = (int) player.getX();
        int playerY = (int) player.getY();
        
        // Draw player as a filled rectangle with gradient effect
        g.setColor(PLAYER_COLOR);
        g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
        
        // Add a highlight on the top-left to give depth
        g.setColor(new Color(100, 149, 237));  // Cornflower blue
        g.fillRect(playerX, playerY, PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2);
        
        // Draw a black border for clarity
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
        
        // Optional: Draw direction indicator if moving
        if (playerMovementUseCase.isMoving()) {
            drawDirectionIndicator(g, playerX, playerY);
        }
    }
    
    /**
     * Draws a small indicator showing the direction the player is moving.
     * 
     * @param g the Graphics2D context
     * @param playerX the player's screen X coordinate
     * @param playerY the player's screen Y coordinate
     */
    private void drawDirectionIndicator(Graphics2D g, int playerX, int playerY) {
        Direction direction = playerMovementUseCase.getCurrentDirection();
        if (direction == null) return;
        
        g.setColor(new Color(255, 255, 0, 150));  // Semi-transparent yellow
        int centerX = playerX + PLAYER_WIDTH / 2;
        int centerY = playerY + PLAYER_HEIGHT / 2;
        int arrowLength = 15;
        
        switch (direction) {
            case UP:
                g.drawLine(centerX, centerY, centerX, centerY - arrowLength);
                break;
            case DOWN:
                g.drawLine(centerX, centerY, centerX, centerY + arrowLength);
                break;
            case LEFT:
                g.drawLine(centerX, centerY, centerX - arrowLength, centerY);
                break;
            case RIGHT:
                g.drawLine(centerX, centerY, centerX + arrowLength, centerY);
                break;
        }
    }
    
    /**
     * Draws UI elements (HUD, status, debug info).
     * 
     * @param g the Graphics2D context
     */
    private void drawUI(Graphics2D g) {
        Player player = playerMovementUseCase.getPlayer();
        
        // Draw semi-transparent background panel for HUD
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(5, 5, 250, 60);
        
        // Draw position and balance text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        
        String positionText = String.format("Pos: (%.0f, %.0f)", player.getX(), player.getY());
        String balanceText = String.format("Balance: $%.2f", player.getBalance());
        
        g.drawString(positionText, 15, 25);
        g.drawString(balanceText, 15, 45);
        
        // Draw instructions at bottom
        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String instructionsText = "WASD: Move | ESC: Exit";
        int instructionWidth = g.getFontMetrics().stringWidth(instructionsText);
        g.drawString(instructionsText, this.getWidth() - instructionWidth - 10, this.getHeight() - 10);
        
        // Draw movement state indicator
        if (playerMovementUseCase.isMoving()) {
            g.setColor(new Color(0, 255, 0));
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("MOVING", this.getWidth() - 100, 25);
        }
    }
}

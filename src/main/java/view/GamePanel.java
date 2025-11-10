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
import entity.GameMap;
import entity.Zone;
import entity.Transition;

public class GamePanel extends JPanel implements ActionListener {
    private final PlayerMovementUseCase playerMovementUseCase;
    private final Timer gameTimer;
    private final GameMap gameMap;

    // Game loop timing
    private static final int FRAME_DELAY_MS = 16;  // ~60 FPS (1000ms / 60 ≈ 16.67ms)
    private long lastUpdateTime;

    // Rendering constants
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
        this.gameMap = new GameMap();

        // Set panel properties
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(gameMap.getCurrentZone().getBackgroundColor());
        this.setFocusable(true);

        // Attach the input controller as a KeyListener
        this.addKeyListener(playerInputController);

        // Initialize the game loop timer
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
        checkZoneTransition();

        // === RENDER PHASE ===
        this.repaint();
    }

    /**
     * Checks if the player has reached the edge of the screen and transitions to the next zone if needed.
     * First checks for special transitions (tunnels, elevators), then normal zone connections.
     */
    private void checkZoneTransition() {
        Player player = playerMovementUseCase.getPlayer();
        Zone currentZone = gameMap.getCurrentZone();
        double x = player.getX();
        double y = player.getY();
        boolean transitioned = false;

        // Determine which edge the player crossed
        Zone.Edge edgeCrossed = null;
        if (y >= getHeight()) {
            edgeCrossed = Zone.Edge.DOWN;
        } else if (y <= -PLAYER_HEIGHT) {
            edgeCrossed = Zone.Edge.UP;
        } else if (x >= getWidth()) {
            edgeCrossed = Zone.Edge.RIGHT;
        } else if (x <= -PLAYER_WIDTH) {
            edgeCrossed = Zone.Edge.LEFT;
        }

        if (edgeCrossed != null) {
            // First check for special transitions (tunnels, elevators, etc.)
            Transition specialTransition = gameMap.getSpecialTransition(edgeCrossed);
            if (specialTransition != null) {
                // Use special transition
                gameMap.setCurrentZone(specialTransition.getToZone());
                repositionPlayerFromTransition(player, specialTransition.getToEdge());
                transitioned = true;
            } else {
                // Fall back to normal zone connection
                String nextZone = currentZone.getNeighbor(edgeCrossed);
                if (nextZone != null) {
                    gameMap.setCurrentZone(nextZone);
                    repositionPlayerFromEdge(player, edgeCrossed);
                    transitioned = true;
                } else {
                    // Block movement if no connection
                    blockPlayerMovement(player, edgeCrossed);
                }
            }

            // Update background color if zone changed
            if (transitioned) {
                setBackground(gameMap.getCurrentZone().getBackgroundColor());
            }
        }
    }

    /**
     * Repositions the player when entering a zone via a normal edge connection.
     */
    private void repositionPlayerFromEdge(Player player, Zone.Edge edgeExited) {
        switch (edgeExited) {
            case DOWN:
                player.setY(0); // Enter from top
                break;
            case UP:
                player.setY(getHeight() - PLAYER_HEIGHT); // Enter from bottom
                break;
            case RIGHT:
                player.setX(0); // Enter from left
                break;
            case LEFT:
                player.setX(getWidth() - PLAYER_WIDTH); // Enter from right
                break;
        }
    }

    /**
     * Repositions the player when entering a zone via a special transition.
     * The toEdge indicates which edge of the destination zone the player enters from.
     */
    private void repositionPlayerFromTransition(Player player, Zone.Edge toEdge) {
        switch (toEdge) {
            case DOWN:
                player.setY(0); // Enter from top
                break;
            case UP:
                player.setY(getHeight() - PLAYER_HEIGHT); // Enter from bottom
                break;
            case RIGHT:
                player.setX(0); // Enter from left
                break;
            case LEFT:
                player.setX(getWidth() - PLAYER_WIDTH); // Enter from right
                break;
        }
    }

    /**
     * Blocks the player from moving beyond the edge when there's no valid connection.
     */
    private void blockPlayerMovement(Player player, Zone.Edge edgeTouched) {
        switch (edgeTouched) {
            case DOWN:
                player.setY(getHeight() - PLAYER_HEIGHT);
                break;
            case UP:
                player.setY(0);
                break;
            case RIGHT:
                player.setX(getWidth() - PLAYER_WIDTH);
                break;
            case LEFT:
                player.setX(0);
                break;
        }
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
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw zone name at top center
        Zone zone = gameMap.getCurrentZone();
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String zoneName = zone.getName();
        int zoneNameWidth = g2d.getFontMetrics().stringWidth(zoneName);
        g2d.drawString(zoneName, (getWidth() - zoneNameWidth) / 2, 40);

        // Draw player
        drawPlayer(g2d);
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

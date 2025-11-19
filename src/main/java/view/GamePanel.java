package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

import entity.GameMap;
import entity.Player;
import entity.Transition;
import entity.Zone;
import interface_adapter.events.PlayerInputController;
import use_case.Direction;
import use_case.PlayerMovementUseCase;

public class GamePanel extends JPanel implements ActionListener {
    private final PlayerMovementUseCase playerMovementUseCase;
    private final Timer gameTimer;
    private final GameMap gameMap;
    
    // Background image cache
    private final Map<String, BufferedImage> backgroundImageCache = new HashMap<>();
    
    // Background music
    private volatile Clip backgroundMusicClip;
    private volatile String currentMusicPath;
    private final Object musicLock = new Object();

    // Virtual (internal) resolution - game logic operates in this space
    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1200;
    private static final double ASPECT_RATIO = 16.0 / 10.0;
    
    // Viewport dimensions (scaled to fit window with letterboxing/pillarboxing)
    private int viewportWidth;
    private int viewportHeight;
    private int viewportX;  // Offset for centering (black bars)
    private int viewportY;
    private double scale;   // Current scale factor

    // Game loop timing
    private static final int FRAME_DELAY_MS = 16;  // ~60 FPS (1000ms / 60 ≈ 16.67ms)
    private long lastUpdateTime;

    // Rendering constants (scaled for 1920x1200 virtual resolution)
    private static final Color PLAYER_COLOR = Color.BLUE;
    private static final int PLAYER_WIDTH = 64;   // Scaled up from 32 for 1920x1200
    private static final int PLAYER_HEIGHT = 64;  // Scaled up from 32 for 1920x1200
    
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

        // Set panel properties - no preferred size since we're resizable
        this.setBackground(Color.BLACK);  // Black for letterbox bars
        this.setFocusable(true);

        // Attach the input controller as a KeyListener
        this.addKeyListener(playerInputController);

        // Initialize the game loop timer
        this.gameTimer = new Timer(FRAME_DELAY_MS, this);
        this.lastUpdateTime = System.currentTimeMillis();
        
        // Initialize viewport
        calculateViewport();
        
        // Start background music for initial zone
        playBackgroundMusic(gameMap.getCurrentZone().getBackgroundMusicPath());
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
     * Useful for closing the game.
     */
    public void stopGameLoop() {
        gameTimer.stop();
        stopBackgroundMusic();
    }
    
    /**
     * Pauses the game loop.
     * Stops the timer but maintains game state.
     */
    public void pauseGame() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }
    
    /**
     * Resumes the game loop.
     * Restarts the timer and updates lastUpdateTime to prevent huge delta.
     */
    public void resumeGame() {
        if (!gameTimer.isRunning()) {
            lastUpdateTime = System.currentTimeMillis();  // Reset time to prevent delta jump
            gameTimer.start();
        }
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
     * Calculates the viewport dimensions to maintain 16:10 aspect ratio.
     * Adds letterboxing (horizontal bars) or pillarboxing (vertical bars) as needed.
     */
    private void calculateViewport() {
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        
        // Avoid division by zero
        if (windowWidth <= 0 || windowHeight <= 0) {
            viewportWidth = VIRTUAL_WIDTH;
            viewportHeight = VIRTUAL_HEIGHT;
            viewportX = 0;
            viewportY = 0;
            scale = 1.0;
            return;
        }
        
        double windowAspect = (double) windowWidth / windowHeight;
        
        if (windowAspect > ASPECT_RATIO) {
            // Window is wider than 16:10 - add pillarbox (vertical black bars)
            viewportHeight = windowHeight;
            viewportWidth = (int) (windowHeight * ASPECT_RATIO);
            viewportX = (windowWidth - viewportWidth) / 2;
            viewportY = 0;
        } else {
            // Window is taller than 16:10 - add letterbox (horizontal black bars)
            viewportWidth = windowWidth;
            viewportHeight = (int) (windowWidth / ASPECT_RATIO);
            viewportX = 0;
            viewportY = (windowHeight - viewportHeight) / 2;
        }
        
        scale = (double) viewportWidth / VIRTUAL_WIDTH;
    }
    
    /**
     * Converts screen coordinates to virtual coordinates.
     * Used for mouse input translation.
     *
     * @param screenX X coordinate in screen space
     * @param screenY Y coordinate in screen space
     * @return Point in virtual coordinate space (1920x1200)
     */
    public Point screenToVirtual(int screenX, int screenY) {
        int virtualX = (int)((screenX - viewportX) / scale);
        int virtualY = (int)((screenY - viewportY) / scale);
        return new Point(virtualX, virtualY);
    }
    
    /**
     * Gets the current scale factor.
     * @return the scale factor from virtual to screen coordinates
     */
    public double getScale() {
        return scale;
    }

    /**
     * Checks if the player has reached the edge of the screen and transitions to the next zone if needed.
     * Special handling for subway zones with 300-pixel trigger areas.
     * Then checks for special transitions (tunnels, elevators), and finally normal zone connections.
     * Transitions trigger when player reaches the visible edge of the screen.
     */
    private void checkZoneTransition() {
        Player player = playerMovementUseCase.getPlayer();
        Zone currentZone = gameMap.getCurrentZone();
        String currentZoneName = currentZone.getName();
        double x = player.getX();
        double y = player.getY();
        boolean transitioned = false;

        // Special handling for subway station transitions (300-pixel trigger zones)
        final int SUBWAY_TRANSITION_ZONE = 300;
        final int SUBWAY_SPAWN_OFFSET = 50; // Offset to prevent immediate re-triggering
        
        if ("Subway Station 1".equals(currentZoneName)) {
            // Check if player is in bottom 300 pixels
            if (y >= VIRTUAL_HEIGHT - SUBWAY_TRANSITION_ZONE) {
                gameMap.setCurrentZone("Subway Station 2");
                player.setY(SUBWAY_TRANSITION_ZONE + SUBWAY_SPAWN_OFFSET); // Spawn safely away from transition zone
                transitioned = true;
            }
        } else if ("Subway Station 2".equals(currentZoneName)) {
            // Check if player is in top 300 pixels
            if (y <= SUBWAY_TRANSITION_ZONE) {
                gameMap.setCurrentZone("Subway Station 1");
                player.setY(VIRTUAL_HEIGHT - SUBWAY_TRANSITION_ZONE - SUBWAY_SPAWN_OFFSET); // Spawn safely away from transition zone
                transitioned = true;
            }
        }

        // If no subway transition occurred, check normal edge-based transitions
        if (!transitioned) {
            // Determine which edge the player has reached (at the visible screen bounds)
            Zone.Edge edgeReached = null;
            if (y >= VIRTUAL_HEIGHT - PLAYER_HEIGHT) {
                edgeReached = Zone.Edge.DOWN;
            } else if (y <= 0) {
                edgeReached = Zone.Edge.UP;
            } else if (x >= VIRTUAL_WIDTH - PLAYER_WIDTH) {
                edgeReached = Zone.Edge.RIGHT;
            } else if (x <= 0) {
                edgeReached = Zone.Edge.LEFT;
            }

            if (edgeReached != null) {
                // First check for special transitions (tunnels, elevators, etc.)
                Transition specialTransition = gameMap.getSpecialTransition(edgeReached);
                if (specialTransition != null) {
                    // Use special transition
                    gameMap.setCurrentZone(specialTransition.getToZone());
                    repositionPlayerFromTransition(player, specialTransition.getToEdge());
                    transitioned = true;
                } else {
                    // Fall back to normal zone connection
                    String nextZone = currentZone.getNeighbor(edgeReached);
                    if (nextZone != null) {
                        gameMap.setCurrentZone(nextZone);
                        repositionPlayerFromEdge(player, edgeReached);
                        transitioned = true;
                    }
                    // Note: No need to block movement here since PlayerMovementUseCase
                    // will clamp the position within screen bounds
                }
            }
        }

        // Update background color and music if zone changed
        if (transitioned) {
            Zone newZone = gameMap.getCurrentZone();
            setBackground(newZone.getBackgroundColor());
            playBackgroundMusic(newZone.getBackgroundMusicPath());
        }
    }

    /**
     * Repositions the player when entering a zone via a normal edge connection.
     * Places player slightly away from edge to prevent immediate re-triggering of transition.
     */
    private void repositionPlayerFromEdge(Player player, Zone.Edge edgeExited) {
        final int EDGE_OFFSET = 5; // Pixels away from edge
        switch (edgeExited) {
            case DOWN:
                player.setY(EDGE_OFFSET); // Enter from top, slightly below edge
                break;
            case UP:
                player.setY(VIRTUAL_HEIGHT - PLAYER_HEIGHT - EDGE_OFFSET); // Enter from bottom, slightly above edge
                break;
            case RIGHT:
                player.setX(EDGE_OFFSET); // Enter from left, slightly right of edge
                break;
            case LEFT:
                player.setX(VIRTUAL_WIDTH - PLAYER_WIDTH - EDGE_OFFSET); // Enter from right, slightly left of edge
                break;
        }
    }

    /**
     * Repositions the player when entering a zone via a special transition.
     * The toEdge indicates which edge of the destination zone the player enters from.
     * Places player slightly away from edge to prevent immediate re-triggering of transition.
     */
    private void repositionPlayerFromTransition(Player player, Zone.Edge toEdge) {
        final int EDGE_OFFSET = 5; // Pixels away from edge
        switch (toEdge) {
            case DOWN:
                player.setY(EDGE_OFFSET); // Enter from top, slightly below edge
                break;
            case UP:
                player.setY(VIRTUAL_HEIGHT - PLAYER_HEIGHT - EDGE_OFFSET); // Enter from bottom, slightly above edge
                break;
            case RIGHT:
                player.setX(EDGE_OFFSET); // Enter from left, slightly right of edge
                break;
            case LEFT:
                player.setX(VIRTUAL_WIDTH - PLAYER_WIDTH - EDGE_OFFSET); // Enter from right, slightly left of edge
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
        
        // Recalculate viewport in case window was resized
        calculateViewport();
        
        // Fill entire panel with black (letterbox/pillarbox background)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw the game viewport background (image or color fallback)
        Zone currentZone = gameMap.getCurrentZone();
        BufferedImage backgroundImage = loadBackgroundImage(currentZone.getBackgroundImagePath());
        
        if (backgroundImage != null) {
            // Draw the background image scaled to viewport
            g2d.drawImage(backgroundImage, viewportX, viewportY, viewportWidth, viewportHeight, null);
        } else {
            // Fallback to solid color if image not available
            g2d.setColor(currentZone.getBackgroundColor());
            g2d.fillRect(viewportX, viewportY, viewportWidth, viewportHeight);
        }
        
        // Create clipped viewport for game content
        g2d.setClip(viewportX, viewportY, viewportWidth, viewportHeight);
        
        // Save original transform
        java.awt.geom.AffineTransform originalTransform = g2d.getTransform();
        
        // Translate to viewport position and scale to virtual coordinates
        g2d.translate(viewportX, viewportY);
        g2d.scale(scale, scale);
        
        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Now draw everything in virtual coordinates (1920x1200)
        drawGame(g2d);
        
        // Restore original transform
        g2d.setTransform(originalTransform);
    }
    
    /**
     * Draws all game elements in virtual coordinate space (1920x1200).
     *
     * @param g the Graphics2D context (already scaled and translated)
     */
    private void drawGame(Graphics2D g) {
        // Draw zone name at top center
        Zone zone = gameMap.getCurrentZone();
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 48));  // Scaled up for 1920x1200
        String zoneName = zone.getName();
        int zoneNameWidth = g.getFontMetrics().stringWidth(zoneName);
        g.drawString(zoneName, (VIRTUAL_WIDTH - zoneNameWidth) / 2, 80);

        // Draw player
        drawPlayer(g);
        drawUI(g);
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
        int arrowLength = 30;  // Scaled up for virtual resolution
        
        g.setStroke(new BasicStroke(4));  // Thicker line for virtual resolution
        
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
        
        // Draw semi-transparent background panel for HUD (scaled for 1920x1200)
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(10, 10, 500, 120);
        
        // Draw position and balance text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));  // Scaled up
        
        String positionText = String.format("Pos: (%.0f, %.0f)", player.getX(), player.getY());
        String balanceText = String.format("Balance: $%.2f", player.getBalance());
        
        g.drawString(positionText, 30, 50);
        g.drawString(balanceText, 30, 90);
        
        // Draw instructions at bottom (using virtual dimensions)
        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("Arial", Font.PLAIN, 24));  // Scaled up
        String instructionsText = "WASD: Move | ESC: Exit";
        int instructionWidth = g.getFontMetrics().stringWidth(instructionsText);
        g.drawString(instructionsText, VIRTUAL_WIDTH - instructionWidth - 20, VIRTUAL_HEIGHT - 20);
        
        // Draw movement state indicator
        if (playerMovementUseCase.isMoving()) {
            g.setColor(new Color(0, 255, 0));
            g.setFont(new Font("Arial", Font.BOLD, 24));  // Scaled up
            g.drawString("MOVING", VIRTUAL_WIDTH - 200, 50);
        }
    }
    
    /**
     * Loads a background image from resources with caching.
     * Returns null if the image cannot be loaded.
     *
     * @param imagePath the path to the image resource
     * @return the loaded BufferedImage, or null if loading fails
     */
    private BufferedImage loadBackgroundImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        // Check cache first
        if (backgroundImageCache.containsKey(imagePath)) {
            return backgroundImageCache.get(imagePath);
        }
        
        // Try to load the image
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imagePath));
            if (image != null) {
                backgroundImageCache.put(imagePath, image);
                System.out.println("Loaded background image: " + imagePath);
            }
            return image;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to load background image: " + imagePath);
            System.err.println("Error: " + e.getMessage());
            // Cache null to avoid repeated load attempts
            backgroundImageCache.put(imagePath, null);
            return null;
        }
    }
    
    /**
     * Plays background music for the current zone.
     * If music is already playing and matches the requested path, does nothing.
     * Otherwise, stops current music and starts the new track.
     *
     * @param musicPath the path to the music file (WAV format)
     */
    private void playBackgroundMusic(String musicPath) {
        if (musicPath == null || musicPath.isEmpty()) {
            stopBackgroundMusic();
            return;
        }
        
        synchronized (musicLock) {
            // If the same music is already playing, don't restart it
            if (musicPath.equals(currentMusicPath) && backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
                return;
            }
            
            // Stop current music if any
            stopBackgroundMusic();
            
            // Store the path immediately to prevent race conditions
            final String pathToLoad = musicPath;
            
            // Load and play new music in a separate thread
            new Thread(() -> {
                try {
                    InputStream musicStream = getClass().getResourceAsStream(pathToLoad);
                    
                    if (musicStream != null) {
                        BufferedInputStream bufferedStream = new BufferedInputStream(musicStream);
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
                        
                        synchronized (musicLock) {
                            // Check if we should still load this music
                            if (!pathToLoad.equals(currentMusicPath) && currentMusicPath != null) {
                                audioInputStream.close();
                                return;
                            }
                            
                            if (AudioSystem.getMixer(null) != null) {
                                Clip newClip = AudioSystem.getClip();
                                newClip.open(audioInputStream);
                                
                                // Set the clip and path atomically
                                backgroundMusicClip = newClip;
                                currentMusicPath = pathToLoad;
                                
                                // Loop the music continuously
                                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                                backgroundMusicClip.start();
                                
                                System.out.println("Background music started: " + pathToLoad);
                            } else {
                                System.err.println("Warning: Audio mixer not available");
                            }
                        }
                    } else {
                        System.err.println("Warning: Music file not found: " + pathToLoad);
                    }
                } catch (Exception e) {
                    System.err.println("Note: Background music unavailable: " + e.getMessage());
                    e.printStackTrace();
                }
            }, "MusicLoader").start();
        }
    }
    
    /**
     * Stops the currently playing background music.
     */
    private void stopBackgroundMusic() {
        synchronized (musicLock) {
            if (backgroundMusicClip != null) {
                try {
                    if (backgroundMusicClip.isRunning()) {
                        backgroundMusicClip.stop();
                    }
                    backgroundMusicClip.close();
                } catch (Exception e) {
                    System.err.println("Error stopping music: " + e.getMessage());
                }
                backgroundMusicClip = null;
            }
            currentMusicPath = null;
        }
    }
}

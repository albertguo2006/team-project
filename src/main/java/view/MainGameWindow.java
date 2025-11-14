package view;

import java.awt.Dimension;

import javax.swing.JFrame;

import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;

/**
 * MainGameWindow is the top-level JFrame for the application.
 * 
 * Responsibilities:
 * - Creates and hosts the main game view (GamePanel)
 * - Manages window properties (size, title, close behavior)
 * - Coordinates setup of dependencies and components
 * - Serves as the main application window
 * 
 * This window follows clean architecture by being a simple container
 * that delegates game logic to the appropriate use cases and views.
 */
public class MainGameWindow extends JFrame {
    
    private final GamePanel gamePanel;
    
    /**
     * Constructs the MainGameWindow with the given use case and controller.
     * Sets up the window frame, adds the game panel, and prepares for display.
     * 
     * @param playerMovementUseCase the use case managing player movement
     * @param playerInputController the controller handling input
     */
    public MainGameWindow(PlayerMovementUseCase playerMovementUseCase,
                          PlayerInputController playerInputController) {
        // Window setup
        this.setTitle("California Prop. 65 - Environment Interaction");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);  // Enable resizing
        
        // Set minimum size to maintain playability (maintains 16:10 aspect ratio)
        this.setMinimumSize(new Dimension(640, 400));  // 16:10 ratio minimum
        
        // Set initial preferred size (half of virtual 1920x1200)
        this.setPreferredSize(new Dimension(1280, 800));  // 16:10 ratio
        
        // Create and add the game panel
        this.gamePanel = new GamePanel(playerMovementUseCase, playerInputController);
        this.add(gamePanel);
        
        // Pack the frame to fit the preferred size
        this.pack();
        
        // Center on screen after packing
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Starts the game loop when the window is displayed.
     * Should be called after setVisible(true).
     */
    public void startGame() {
        gamePanel.startGameLoop();
        // Request focus for the game panel so it receives keyboard input
        gamePanel.requestFocus();
    }
    
    /**
     * Stops the game loop (e.g., for pausing).
     */
    public void stopGame() {
        gamePanel.stopGameLoop();
    }
}

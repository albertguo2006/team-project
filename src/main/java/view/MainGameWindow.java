package view;

import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;

import javax.swing.*;

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
        this.setResizable(false);
        this.setLocationRelativeTo(null);  // Center on screen
        
        // Create and add the game panel
        this.gamePanel = new GamePanel(playerMovementUseCase, playerInputController);
        this.add(gamePanel);
        
        // Pack the frame to fit the panel's preferred size
        this.pack();
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

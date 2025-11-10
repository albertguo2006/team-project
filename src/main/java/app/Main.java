package app;

import entity.Player;
import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;
import view.MainGameWindow;

import javax.swing.*;

/**
 * Main is the entry point for the California Prop. 65 application.
 * 
 * Responsibilities:
 * - Instantiate domain entities
 * - Create use cases
 * - Set up presentation layer (controllers)
 * - Initialize and display the main window
 * - Start the game
 * 
 * This follows Clean Architecture by orchestrating the creation
 * of all layers and then letting the presentation layer take control.
 */
public class Main {
    
    public static void main(String[] args) {
        // Run on the Event Dispatch Thread (Swing requirement)
        SwingUtilities.invokeLater(() -> {
            // === LAYER 1: DOMAIN ===
            // Create the core game entity
            Player player = new Player("Player");
            
            // === LAYER 2: APPLICATION ===
            // Create the use case that manages movement logic
            PlayerMovementUseCase playerMovementUseCase = new PlayerMovementUseCase(player);
            
            // === LAYER 3: PRESENTATION ===
            // Create the input controller that translates keyboard input
            PlayerInputController playerInputController = new PlayerInputController(playerMovementUseCase);
            
            // === LAYER 4: INFRASTRUCTURE ===
            // Create the main window (contains the game panel and loop)
            MainGameWindow mainWindow = new MainGameWindow(playerMovementUseCase, playerInputController);
            
            // Set parent frame for ESC key handling
            playerInputController.setParentFrame(mainWindow);
            
            // Display the window
            mainWindow.setVisible(true);
            
            // Start the game loop
            mainWindow.startGame();
        });
    }
}

package app;

import javax.swing.SwingUtilities;

import entity.Player;
import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;
import view.MainGameWindow;

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
        // Enable HiDPI/scaling support for modern displays
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        
        // Enable Wayland support on Linux (Java 17+)
        // This will automatically use Wayland when available, fallback to X11 otherwise
        String sessionType = System.getenv("XDG_SESSION_TYPE");
        if ("wayland".equalsIgnoreCase(sessionType)) {
            System.out.println("Detected Wayland session - native support enabled");
            // Wayland is automatically detected in Java 17+, but we can force it if needed:
            // System.setProperty("awt.toolkit.name", "WLToolkit");
        } else if ("x11".equalsIgnoreCase(sessionType)) {
            System.out.println("Detected X11 session");
        }
        
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

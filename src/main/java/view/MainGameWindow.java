package view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import entity.GameSettings;
import interface_adapter.events.PlayerInputController;
import use_case.PlayerMovementUseCase;

/**
 * MainGameWindow is the top-level JFrame for the application.
 *
 * Responsibilities:
 * - Creates and hosts the main game view (GamePanel)
 * - Manages menu screens (MainMenuPanel, SettingsPanel)
 * - Switches between different views using CardLayout
 * - Manages window properties (size, title, close behavior)
 * - Coordinates setup of dependencies and components
 * - Serves as the main application window
 *
 * This window follows clean architecture by being a simple container
 * that delegates game logic to the appropriate use cases and views.
 */
public class MainGameWindow extends JFrame {
    
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    
    private final LoadingScreenPanel loadingScreenPanel;
    private final MainMenuPanel mainMenuPanel;
    private final SettingsPanel settingsPanel;
    private InGameMenuPanel inGameMenuPanel;
    private GamePanel gamePanel;
    private PlayerInputController playerInputController;
    
    private final GameSettings gameSettings;
    
    // Card names for CardLayout
    private static final String LOADING_CARD = "loading";
    private static final String MENU_CARD = "menu";
    private static final String SETTINGS_CARD = "settings";
    private static final String GAME_CARD = "game";
    private static final String IN_GAME_MENU_CARD = "inGameMenu";
    
    /**
     * Constructs the MainGameWindow.
     * Sets up the window frame with menu system and prepares for display.
     */
    public MainGameWindow() {
        // Window setup
        this.setTitle("California Prop. 65");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        
        // Set minimum size to maintain playability (maintains 16:10 aspect ratio)
        this.setMinimumSize(new Dimension(640, 400));  // 16:10 ratio minimum
        
        // Set initial preferred size
        this.setPreferredSize(new Dimension(1280, 800));  // 16:10 ratio
        
        // Initialize game settings
        this.gameSettings = new GameSettings();
        
        // Set up CardLayout for switching between views
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        
        // Create loading screen panel
        this.loadingScreenPanel = new LoadingScreenPanel();
        this.loadingScreenPanel.setOnComplete(() -> showMainMenu());
        
        // Create menu and settings panels
        this.mainMenuPanel = new MainMenuPanel();
        this.settingsPanel = new SettingsPanel(gameSettings);
        
        // Add menu action listeners
        setupMenuListeners();
        
        // Add panels to card layout
        cardPanel.add(loadingScreenPanel, LOADING_CARD);
        cardPanel.add(mainMenuPanel, MENU_CARD);
        cardPanel.add(settingsPanel, SETTINGS_CARD);
        
        // Add card panel to frame
        this.add(cardPanel);
        
        // Pack the frame to fit the preferred size
        this.pack();
        
        // Center on screen after packing
        this.setLocationRelativeTo(null);
        
        // Show loading screen initially (will be started when window becomes visible)
        cardLayout.show(cardPanel, LOADING_CARD);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // Start the loading screen only after the window is actually visible
        if (visible) {
            System.out.println("Window is now visible, starting loading screen");
            loadingScreenPanel.start();
        }
    }
    
    /**
     * Sets up action listeners for menu buttons.
     */
    private void setupMenuListeners() {
        // Main Menu listeners
        mainMenuPanel.addNewGameListener(e -> startNewGame());
        mainMenuPanel.addLoadGameListener(e -> loadGame());
        mainMenuPanel.addSettingsListener(e -> showSettings());
        mainMenuPanel.addExitListener(e -> exitGame());
        
        // Settings listeners
        settingsPanel.addSaveListener(e -> saveSettings());
        settingsPanel.addCancelListener(e -> cancelSettings());
    }
    
    /**
     * Sets up action listeners for in-game menu buttons.
     */
    private void setupInGameMenuListeners() {
        if (inGameMenuPanel != null) {
            inGameMenuPanel.addResumeListener(e -> resumeGame());
            inGameMenuPanel.addSaveListener(e -> saveGame());
            inGameMenuPanel.addSettingsListener(e -> showSettingsFromGame());
            inGameMenuPanel.addSaveAndExitListener(e -> saveAndExit());
        }
    }
    
    /**
     * Shows the loading screen and starts the loading sequence.
     */
    public void showLoadingScreen() {
        cardLayout.show(cardPanel, LOADING_CARD);
        loadingScreenPanel.start();
    }
    
    /**
     * Shows the main menu.
     */
    public void showMainMenu() {
        // Stop game if running
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }
        cardLayout.show(cardPanel, MENU_CARD);
        mainMenuPanel.requestFocusInWindow();
    }
    
    /**
     * Shows the settings panel.
     */
    private void showSettings() {
        cardLayout.show(cardPanel, SETTINGS_CARD);
        settingsPanel.requestFocusInWindow();
    }
    
    /**
     * Starts a new game.
     */
    private void startNewGame() {
        // Create game components if not already created
        if (gamePanel == null) {
            initializeGamePanel();
        }
        
        // Switch to game view
        cardLayout.show(cardPanel, GAME_CARD);
        
        // Start the game
        startGame();
    }
    
    /**
     * Initializes the game panel with all required dependencies.
     */
    private void initializeGamePanel() {
        // Create player entity
        entity.Player player = new entity.Player("Player");
        
        // Create use case
        PlayerMovementUseCase playerMovementUseCase = new PlayerMovementUseCase(player);
        
        // Create controller
        this.playerInputController = new PlayerInputController(playerMovementUseCase);
        playerInputController.setParentFrame(this);
        
        // Set up pause menu listener
        playerInputController.setPauseMenuListener(() -> showInGameMenu());
        
        // Create game panel
        this.gamePanel = new GamePanel(playerMovementUseCase, playerInputController);
        
        // Create in-game menu panel
        this.inGameMenuPanel = new InGameMenuPanel();
        setupInGameMenuListeners();
        
        // Add to card panel
        cardPanel.add(gamePanel, GAME_CARD);
        cardPanel.add(inGameMenuPanel, IN_GAME_MENU_CARD);
    }
    
    /**
     * Shows the in-game pause menu and pauses the game.
     */
    private void showInGameMenu() {
        if (gamePanel != null && inGameMenuPanel != null) {
            gamePanel.pauseGame();
            cardLayout.show(cardPanel, IN_GAME_MENU_CARD);
            inGameMenuPanel.requestFocusInWindow();
        }
    }
    
    /**
     * Resumes the game from the pause menu.
     */
    private void resumeGame() {
        if (gamePanel != null) {
            cardLayout.show(cardPanel, GAME_CARD);
            gamePanel.resumeGame();
            gamePanel.requestFocusInWindow();
        }
    }
    
    /**
     * Saves the game (frontend stub - backend to be implemented).
     */
    private void saveGame() {
        JOptionPane.showMessageDialog(
            this,
            "Game saved successfully!\n" +
            "(Save functionality will be fully implemented later.\n" +
            "The backend for save/load is being developed by another team member.)",
            "Game Saved",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Shows settings from in-game menu.
     */
    private void showSettingsFromGame() {
        cardLayout.show(cardPanel, SETTINGS_CARD);
        settingsPanel.requestFocusInWindow();
    }
    
    /**
     * Saves and exits to main menu.
     */
    private void saveAndExit() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Save and return to main menu?",
            "Save & Exit",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            saveGame();
            if (gamePanel != null) {
                gamePanel.stopGameLoop();
            }
            showMainMenu();
        }
    }
    
    /**
     * Loads a saved game (frontend stub - backend to be implemented).
     */
    private void loadGame() {
        JOptionPane.showMessageDialog(
            this,
            "Load game functionality will be implemented later.\n" +
            "The backend for save/load is being developed by another team member.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Saves settings and returns to main menu.
     */
    private void saveSettings() {
        settingsPanel.applySettings();
        
        // Apply resolution to window if needed
        Dimension newSize = new Dimension(
            gameSettings.getRenderWidth(),
            gameSettings.getRenderHeight()
        );
        
        // Only update if different and reasonable
        if (newSize.width >= 640 && newSize.height >= 400) {
            setPreferredSize(newSize);
            pack();
            setLocationRelativeTo(null);  // Re-center
        }
        
        showMainMenu();
    }
    
    /**
     * Cancels settings changes and returns to appropriate view.
     */
    private void cancelSettings() {
        settingsPanel.revertSettings();
        
        // Return to game if it was running, otherwise main menu
        if (gamePanel != null && inGameMenuPanel != null) {
            showInGameMenu();
        } else {
            showMainMenu();
        }
    }
    
    /**
     * Exits the game application.
     */
    private void exitGame() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Starts the game loop.
     * Should be called when switching to game view.
     */
    private void startGame() {
        if (gamePanel != null) {
            gamePanel.resumeGame();
            gamePanel.requestFocus();
        }
    }
    
    /**
     * Stops the game loop (e.g., for pausing).
     */
    public void stopGame() {
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }
    }
    
    /**
     * Gets the game settings.
     * @return the current game settings
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }
}

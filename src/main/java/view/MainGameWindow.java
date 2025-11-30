package view;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.*;

import api.AlphaStockDataAccessObject;
import data_access.EventDataAccessObject;
import data_access.ItemDataAccessObject;
import data_access.LoadFileUserDataAccessObject;
import data_access.NPCDataAccessObject;
import data_access.Paybill.PaybillDataAccessObject;
import data_access.QuestDataAccessObject;
import data_access.SaveFileUserDataAccessObject;
import data_access.SleepDataAccessObject;
import data_access.WorldItemDataAccessObject;
import entity.GameMap;
import entity.GameSettings;
import entity.Item;
import entity.NPC;
import entity.Player;
import entity.Quest;
import entity.WorldItem;
import interface_adapter.ViewManagerModel;
import interface_adapter.events.*;
import interface_adapter.load_progress.LoadProgressController;
import interface_adapter.load_progress.LoadProgressPresenter;
import interface_adapter.load_progress.LoadProgressViewModel;
import interface_adapter.paybills.PaybillController;
import interface_adapter.paybills.PaybillPresenter;
import interface_adapter.paybills.PaybillViewModel;
import interface_adapter.save_progress.SaveProgressController;
import interface_adapter.save_progress.SaveProgressPresenter;
import interface_adapter.save_progress.SaveProgressViewModel;
import interface_adapter.sleep.SleepController;
import interface_adapter.sleep.SleepPresenter;
import interface_adapter.sleep.SleepViewModel;
import interface_adapter.stock_trading.StockTradingController;
import use_case.PlayerMovementUseCase;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeDataAccessInterface;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeInputBoundary;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeInteractor;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeOutputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventDataAccessInterface;
import use_case.events.StartRandomEvent.StartRandomEventInputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventInteractor;
import use_case.events.StartRandomEvent.StartRandomEventOutputBoundary;
import use_case.load_progress.*;
import use_case.paybills.PaybillDataAccessInterface;
import use_case.paybills.PaybillInputBoundary;
import use_case.paybills.PaybillInteractor;
import use_case.paybills.PaybillOutputBoundary;
import use_case.save_progress.SaveProgressDataAccessInterface;
import use_case.save_progress.SaveProgressInputData;
import use_case.save_progress.SaveProgressInteractor;
import use_case.sleep.SleepDataAccessInterface;
import use_case.sleep.SleepInputBoundary;
import use_case.sleep.SleepInteractor;
import use_case.stock_game.PlayStockGameDataAccessInterface;
import use_case.stock_game.PlayStockGameInputBoundary;
import use_case.stock_game.PlayStockGameInteractor;

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

    // Sleep system components
    private ViewManagerModel viewManagerModel;
    private SleepViewModel sleepViewModel;
    private SleepController sleepController;
    private DaySummaryView daySummaryView;
    private EndGameView endGameView;
    private Player player;
    private PaybillView paybillView;
    private PaybillViewModel paybillViewModel;
    PaybillController paybillController;
    private PaybillDataAccessInterface paybillDataAccess;
    private EventView eventView;
    private EventViewModel eventViewModel;
    private StartEventController startEventController;

    // Stock trading system components
    private StockTradingController stockTradingController;
    private StockGameView stockGameView;

    // Save/Load system components
    private SaveProgressInteractor saveProgressInteractor;
    private SaveProgressController saveProgressController;
    private SaveProgressViewModel saveProgressViewModel;

    private LoadProgressInteractor loadProgressInteractor;
    private LoadProgressController loadProgressController;
    private LoadProgressViewModel loadProgressViewModel;
    private static final String SAVE_FILE = "src/main/resources/saveFile.json";

    // Quest system components
    private QuestDataAccessObject questDataAccess;
    private ItemDataAccessObject itemDataAccess;

    // Card names for CardLayout
    private static final String LOADING_CARD = "loading";
    private static final String MENU_CARD = "menu";
    private static final String SETTINGS_CARD = "settings";
    private static final String GAME_CARD = "game";
    private static final String IN_GAME_MENU_CARD = "inGameMenu";
    private static final String DAY_SUMMARY_CARD = "daySummary";
    private static final String END_GAME_CARD = "endGame";
    private static final String PAYBILL_CARD = "paybill";
    private static final String EVENT_CARD = "event";

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

        // Initialize save/load system
        SaveProgressDataAccessInterface saveDataAccess = new SaveFileUserDataAccessObject();
        this.saveProgressViewModel = new SaveProgressViewModel();
        SaveProgressPresenter savePresenter = new SaveProgressPresenter(saveProgressViewModel);
        this.saveProgressInteractor = new SaveProgressInteractor(saveDataAccess, savePresenter);
        this.saveProgressController = new SaveProgressController(saveProgressInteractor);

        LoadProgressDataAccessInterface loadDataAccess = new LoadFileUserDataAccessObject();
        this.loadProgressViewModel = new LoadProgressViewModel();
        LoadProgressPresenter loadPresenter = new LoadProgressPresenter(loadProgressViewModel);
        this.loadProgressInteractor = new LoadProgressInteractor(loadDataAccess, loadPresenter);
        this.loadProgressController = new LoadProgressController(loadProgressInteractor);


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
        // Stop existing game if running
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
            cardPanel.remove(gamePanel);
        }

        // Remove old panels if they exist
        if (inGameMenuPanel != null) {
            cardPanel.remove(inGameMenuPanel);
        }
        if (daySummaryView != null) {
            cardPanel.remove(daySummaryView);
        }
        if (endGameView != null) {
            cardPanel.remove(endGameView);
        }
        if (paybillView != null) {
            cardPanel.remove(paybillView);
        }

        // Delete bills.json to ensure fresh bills are generated for new game
        java.io.File billsFile = new java.io.File("bills.json");
        if (billsFile.exists()) {
            billsFile.delete();
            System.out.println("Deleted bills.json for new game");
        }

        // Always create a fresh game with a new player and map
        initializeGamePanel(new Player("Player"), new GameMap());

        // Switch to game view
        cardLayout.show(cardPanel, GAME_CARD);

        // Start the game
        startGame();
    }

    /**
     * Initializes the game panel with all required dependencies.
     */
    private void initializeGamePanel(Player player, GameMap gameMap) {
        // Create player entity
        this.player = player;


        // Create view manager model
        this.viewManagerModel = new ViewManagerModel();

        // Initialize Paybill system
        initializePaybillSystem();

        // Create sleep system components (following Clean Architecture)
        // 1. View Model
        this.sleepViewModel = new SleepViewModel();

        // 2. Data Access
        SleepDataAccessInterface sleepDataAccess = new SleepDataAccessObject();

        // 3. Presenter (output boundary)
        SleepPresenter sleepPresenter = new SleepPresenter(viewManagerModel, sleepViewModel);
        
        // 4. Interactor (input boundary) - use shared paybillDataAccess for accurate debt calculation
        SleepInputBoundary sleepInteractor = new SleepInteractor(sleepPresenter, sleepDataAccess,
                (data_access.Paybill.PaybillDataAccessObject) paybillDataAccess);
        
        // 5. Controller
        this.sleepController = new SleepController(sleepInteractor);

        // Initialize Stock Trading system
        initializeStockTradingSystem();

        // Create use case
        PlayerMovementUseCase playerMovementUseCase = new PlayerMovementUseCase(player);

        // Create controller
        this.playerInputController = new PlayerInputController(playerMovementUseCase);
        playerInputController.setParentFrame(this);

        // Set up pause menu listener
        playerInputController.setPauseMenuListener(() -> showInGameMenu());

        // Create NPC data access
        NPCDataAccessObject npcDataAccess = new NPCDataAccessObject();

        // Create game panel
        this.gamePanel = new GamePanel(playerMovementUseCase, playerInputController, gameMap, npcDataAccess);

        // Set up sleep system callbacks
        playerInputController.setSleepZoneChecker(() -> gamePanel.isInSleepZone());
        playerInputController.setSleepActionListener(() -> sleepController.sleep(player));

        // Set up stock trading system callbacks
        playerInputController.setStockTradingZoneChecker(() -> gamePanel.isInStockTradingZone());
        playerInputController.setStockTradingActionListener(() -> {
            gamePanel.pauseGame();  // Pause main game while trading
            stockTradingController.startStockTrading(player);
        });

        // Set up Event system
        initializeEventSystem();

        // Set up mailbox system callbacks
        playerInputController.setMailboxZoneChecker(() -> gamePanel.isInMailboxZone());
        playerInputController.setMailboxActionListener(() -> {
            gamePanel.pauseGame();  // Pause main game while viewing bills
            showPaybillView();
        });

        // Set up NPC interaction callbacks
        playerInputController.setNPCInteractionChecker(() -> gamePanel.getNearbyNPC());
        playerInputController.setNPCInteractionListener((NPC npc) -> {
            gamePanel.pauseGame();
            showNPCDialog(npc, npcDataAccess);
        });

        // Set up inventory and world item system
        this.itemDataAccess = new ItemDataAccessObject();
        WorldItemDataAccessObject worldItemDataAccess = new WorldItemDataAccessObject(itemDataAccess.getItemMap());
        gamePanel.setWorldItemDataAccess(worldItemDataAccess);

        // Set up quest system
        this.questDataAccess = new QuestDataAccessObject(itemDataAccess.getItemMap());

        // Set up inventory slot selection callbacks
        playerInputController.setInventorySlotSelector(new PlayerInputController.InventorySlotSelector() {
            @Override
            public void setSelectedInventorySlot(int slot) {
                gamePanel.setSelectedInventorySlot(slot);
            }
            @Override
            public int getSelectedInventorySlot() {
                return gamePanel.getSelectedInventorySlot();
            }
        });

        // Set up world item interaction callbacks
        playerInputController.setWorldItemChecker(() -> gamePanel.getNearbyWorldItem());
        playerInputController.setWorldItemActionListener((WorldItem worldItem) -> {
            Item item = worldItem.getItem();
            if (worldItem.isStoreItem()) {
                // Handle store purchase
                if (player.getBalance() >= item.getPrice()) {
                    if (player.getInventory().size() < 5) {
                        player.setBalance(player.getBalance() - item.getPrice());
                        player.addDailySpending(item.getPrice());
                        player.addInventory(item);
                        System.out.println("Purchased " + item.getName() + " for $" + item.getPrice());
                    } else {
                        System.out.println("Inventory full!");
                    }
                } else {
                    System.out.println("Not enough money to buy " + item.getName());
                }
            } else {
                // Handle free item pickup
                if (player.getInventory().size() < 5) {
                    player.addInventory(item);
                    worldItemDataAccess.collectItem(worldItem);
                    System.out.println("Picked up " + item.getName());
                } else {
                    System.out.println("Inventory full!");
                }
            }
        });

        // Set up inventory item use callback
        playerInputController.setInventoryUseListener((int slotIndex) -> {
            int slotKey = slotIndex + 1;  // Convert 0-4 to 1-5
            Item item = player.getInventory().get(slotKey);
            if (item != null) {
                player.itemUsed(slotKey);
                System.out.println("Used " + item.getName());
                gamePanel.setSelectedInventorySlot(-1);  // Deselect after use
            }
        });

        // Set up inventory item drop callback
        playerInputController.setInventoryDropListener((int slotIndex) -> {
            int slotKey = slotIndex + 1;  // Convert 0-4 to 1-5
            Item item = player.getInventory().get(slotKey);
            if (item != null) {
                // Get player's current position and zone
                double dropX = player.getX();
                double dropY = player.getY();
                String currentZone = gamePanel.getGameMap().getCurrentZone().getName();

                // Remove from inventory and place in world
                player.removeInventory(slotKey);
                worldItemDataAccess.addDroppedItem(item, currentZone, dropX, dropY);

                System.out.println("Dropped " + item.getName() + " at (" + dropX + ", " + dropY + ") in " + currentZone);
                gamePanel.setSelectedInventorySlot(-1);  // Deselect after drop
            }
        });

        // Create sleep views
        this.daySummaryView = new DaySummaryView(sleepViewModel, viewManagerModel, cardPanel);
        this.endGameView = new EndGameView(sleepViewModel, viewManagerModel, cardPanel);

        // Create in-game menu panel
        this.inGameMenuPanel = new InGameMenuPanel();
        setupInGameMenuListeners();

        // Add to card panel
        cardPanel.add(gamePanel, GAME_CARD);
        cardPanel.add(inGameMenuPanel, IN_GAME_MENU_CARD);
        cardPanel.add(daySummaryView, DAY_SUMMARY_CARD);
        cardPanel.add(endGameView, END_GAME_CARD);
        // Note: paybillView is already added in initializePaybillSystem()

        // Connect ViewManagerModel to CardLayout
        viewManagerModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                String viewName = (String) evt.getNewValue();
                System.out.println("ViewManager: Switching to view: " + viewName);
                cardLayout.show(cardPanel, viewName);

                // Request focus for the new view and handle state
                switch (viewName) {
                    case GAME_CARD:
                        gamePanel.resumeGame();
                        gamePanel.requestFocusInWindow();
                        break;
                    case DAY_SUMMARY_CARD:
                        daySummaryView.requestFocusInWindow();
                        break;
                    case END_GAME_CARD:
                        endGameView.requestFocusInWindow();
                        break;
                    case PAYBILL_CARD:
                        paybillView.requestFocusInWindow();
                        paybillView.loadInitialBills();
                        break;
                }
            }
        });
    }

    private void initializePaybillSystem() {
        System.out.println("=== INITIALIZE PAYBILL SYSTEM STARTED ===");
        // Create Paybill ViewModel
        this.paybillViewModel = new PaybillViewModel();

        // Create Paybill Data Access (stored as field for sharing with SleepInteractor)
        this.paybillDataAccess = new PaybillDataAccessObject();

        // Create Paybill Presenter
        PaybillOutputBoundary paybillPresenter = new PaybillPresenter(paybillViewModel, viewManagerModel);

        // Create Paybill Interactor
        PaybillInputBoundary paybillInteractor = new PaybillInteractor(paybillDataAccess, paybillPresenter, player);

        // Create Paybill Controller
        this.paybillController = new PaybillController(paybillInteractor);

        // Create Paybill View
        this.paybillView = new PaybillView(paybillViewModel, paybillController, viewManagerModel, paybillDataAccess);

        // Add to card panel
        cardPanel.add(paybillView, PAYBILL_CARD);
    }

    /**
     * Initializes the stock trading game system.
     */
    private void initializeStockTradingSystem() {
        System.out.println("=== INITIALIZE STOCK TRADING SYSTEM STARTED ===");

        // Get Alpha Vantage API key from environment variable
        String apiKey = System.getenv("ALPHA_VANTAGE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("WARNING: ALPHA_VANTAGE_API_KEY environment variable not set.");
            System.err.println("Stock data fetching will not work. Set the environment variable to enable API calls.");
            apiKey = "DEMO";  // Use demo key as fallback (very limited)
        }

        // Create Stock Game Data Access
        PlayStockGameDataAccessInterface stockDataAccess = new AlphaStockDataAccessObject();

        // Create Stock Game View (Presenter)
        this.stockGameView = new StockGameView();
        stockGameView.setPlayer(player);  // Set player reference for balance updates

        // Set callback to resume the main game when stock game ends
        stockGameView.setOnGameEndCallback(() -> {
            if (gamePanel != null) {
                gamePanel.resumeGame();
            }
        });

        // Create Stock Game Interactor
        PlayStockGameInputBoundary stockGameInteractor = new PlayStockGameInteractor(
                stockDataAccess,
                stockGameView
        );

        // Create Stock Trading Controller with API key
        this.stockTradingController = new StockTradingController(stockGameInteractor, apiKey);

        System.out.println("=== INITIALIZE STOCK TRADING SYSTEM COMPLETED ===");
    }

    private void initializeEventSystem() {
        System.out.println("=== INITIALIZE EVENT SYSTEM STARTED ===");

        // Create Event ViewModel
        this.eventViewModel = new EventViewModel("event");

        // Create Event Data Access
        StartRandomEventDataAccessInterface eventDataAccess = new EventDataAccessObject();
        eventDataAccess.createEventList();
        eventDataAccess.setPlayer(player);

        // Create Event Presenter
        StartRandomEventOutputBoundary startRandomEventPresenter= new StartEventPresenter(eventViewModel,
                viewManagerModel);
        // Create Event Interactor
        StartRandomEventInputBoundary startRandomEventInteractor = new StartRandomEventInteractor(eventDataAccess,
                startRandomEventPresenter);

        // Create Event Controller
        this.startEventController = new StartEventController(startRandomEventInteractor);
        gamePanel.setStartEventController(startEventController);

        // Create Outcome Presenter
        ActivateRandomOutcomeOutputBoundary activateOutcomePresenter = new ActivateOutcomePresenter(eventViewModel,
                viewManagerModel);

        // Create Outcome Interactor
        ActivateRandomOutcomeInputBoundary activateRandomOutcomeInteractor = new ActivateRandomOutcomeInteractor(
                (ActivateRandomOutcomeDataAccessInterface) eventDataAccess, activateOutcomePresenter);

        // Create Outcome Controller
        EventOutcomeController eventOutcomeController = new EventOutcomeController(activateRandomOutcomeInteractor);

        // Create Event View
        this.eventView = new EventView(eventViewModel, viewManagerModel);
        this.eventView.setActivateRandomOutcomeController(eventOutcomeController);
        this.eventView.setGamePanel(this.gamePanel);

        // Add to card panel
        cardPanel.add(eventView, EVENT_CARD);

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
     * Saves the current game state to file.
     */
    private void saveGame() {
        SaveGameView saveGameView = new SaveGameView(saveProgressViewModel);

        JFrame frame = new JFrame("Saving...");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null);
        frame.add(saveGameView);
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            new Timer(2550, e -> {
                frame.dispose();
                saveGameData();
            }) {{
                setRepeats(false);
            }}.start();
        });
    }

    private void saveGameData(){
        if (gamePanel == null || player == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No game in progress to save!",
                    "Cannot Save",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            // Get current zone name from the game map
            String currentZone = gamePanel.getGameMap().getCurrentZone().getName();

            // Create input data and save the game using the interactor
            saveProgressController.saveGame(player, SAVE_FILE, currentZone);

            JOptionPane.showMessageDialog(
                    this,
                    "Game saved successfully, " + saveProgressViewModel.getPlayerName() + "!" +
                            "\n Save Day: " + saveProgressViewModel.getCurrentDay() +
                            "\n Last Save: " + saveProgressViewModel.getSaveDate(),
                    "Game Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save game: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    /**
     * Shows settings from in-game menu.
     */
    private void showSettingsFromGame() {
        cardLayout.show(cardPanel, SETTINGS_CARD);
        settingsPanel.requestFocusInWindow();
    }

    /**
     * Shows the NPC interaction dialog for a specific NPC.
     *
     * @param npc The NPC to interact with
     * @param npcDataAccess The NPC data access object
     */
    private void showNPCDialog(NPC npc, NPCDataAccessObject npcDataAccess) {
        // Create ViewModel
        interface_adapter.events.npc_interactions.NpcInteractionsViewModel viewModel =
                new interface_adapter.events.npc_interactions.NpcInteractionsViewModel();

        // Create Presenter
        interface_adapter.events.npc_interactions.NpcInteractionsPresenter presenter =
                new interface_adapter.events.npc_interactions.NpcInteractionsPresenter(viewModel);

        // Create Interactor
        use_case.npc_interactions.NpcInteractionsInteractor interactor =
                new use_case.npc_interactions.NpcInteractionsInteractor(npcDataAccess, presenter);

        // Create Controller
        interface_adapter.events.npc_interactions.NpcInteractionsController controller =
                new interface_adapter.events.npc_interactions.NpcInteractionsController(interactor);

        // Create dialog
        JFrame frame = new JFrame("Chat with " + npc.getName());
        frame.setSize(450, 550);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JTextField inputField = new JTextField(25);
        JButton sendBtn = new JButton("Send");

        // Check for active quest
        Quest activeQuest = questDataAccess != null
                ? questDataAccess.getActiveQuestForNPC(npc.getName(), player.getInventory())
                : null;

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new java.awt.BorderLayout());
        inputPanel.add(inputField, java.awt.BorderLayout.CENTER);
        inputPanel.add(sendBtn, java.awt.BorderLayout.EAST);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new java.awt.BorderLayout());
        bottomPanel.add(inputPanel, java.awt.BorderLayout.CENTER);

        // Add quest button if there's an active quest
        if (activeQuest != null) {
            final Quest quest = activeQuest;
            JButton giveItemBtn = new JButton("Give " + quest.getRequiredItemName());
            giveItemBtn.setBackground(new java.awt.Color(100, 200, 100));

            giveItemBtn.addActionListener(e -> {
                // Find and remove the item from player's inventory
                java.util.Map<Integer, Item> inventory = player.getInventory();
                Integer slotToRemove = null;
                for (java.util.Map.Entry<Integer, Item> entry : inventory.entrySet()) {
                    if (entry.getValue().getName().equals(quest.getRequiredItemName())) {
                        slotToRemove = entry.getKey();
                        break;
                    }
                }

                if (slotToRemove != null) {
                    player.removeInventory(slotToRemove);

                    // Give rewards
                    if (quest.getMoneyReward() > 0) {
                        player.setBalance(player.getBalance() + quest.getMoneyReward());
                        player.addDailyEarnings(quest.getMoneyReward());
                    }

                    if (quest.getItemReward() != null && itemDataAccess != null) {
                        Item rewardItem = itemDataAccess.getItemMap().get(quest.getItemReward());
                        if (rewardItem != null && player.getInventory().size() < 5) {
                            player.addInventory(rewardItem);
                        }
                    }

                    if (quest.getRelationshipReward() > 0) {
                        player.addNPC(npc);
                        int currentScore = player.getNPCScore(npc);
                        player.addNPCScore(npc, currentScore + quest.getRelationshipReward());
                    }

                    // Mark quest as completed
                    questDataAccess.completeQuest(quest.getId());

                    // Show reward message in chat
                    String rewardMsg = "Quest completed! Rewards: " + quest.getRewardDescription();
                    chatArea.append("\n[SYSTEM] " + rewardMsg + "\n\n");

                    // Disable the button
                    giveItemBtn.setEnabled(false);
                    giveItemBtn.setText("Delivered!");

                    System.out.println("Quest completed: " + quest.getId() + " - " + rewardMsg);
                }
            });

            // Add quest info panel
            JPanel questPanel = new JPanel();
            questPanel.setLayout(new java.awt.BorderLayout());
            questPanel.setBackground(new java.awt.Color(255, 255, 200));
            JLabel questLabel = new JLabel("  Quest: " + quest.getDescription());
            questPanel.add(questLabel, java.awt.BorderLayout.CENTER);
            questPanel.add(giveItemBtn, java.awt.BorderLayout.EAST);

            bottomPanel.add(questPanel, java.awt.BorderLayout.NORTH);
        }

        frame.getContentPane().setLayout(new java.awt.BorderLayout());
        frame.getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        // Set up viewModel listener for updating chat area
        viewModel.setListener(() -> {
            simulateTyping(chatArea, viewModel.getNpcName(), viewModel.getAiResponse());
        });

        // Set up send button action
        sendBtn.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.trim().isEmpty()) {
                chatArea.append("You: " + message + "\n");
                inputField.setText("");
                controller.handleUserMessage(npc.getName(), message);
            }
        });

        // Handle window closing to resume game
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                gamePanel.resumeGame();
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }

    /**
     * Simulates typing effect for NPC responses.
     *
     * @param chatArea The text area to display the message
     * @param speaker The name of the speaker
     * @param message The message to display
     */
    private void simulateTyping(JTextArea chatArea, String speaker, String message) {
        final String fullMessage = speaker + ": " + message + "\n";
        Timer timer = new Timer(30, null);
        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < fullMessage.length()) {
                chatArea.append(String.valueOf(fullMessage.charAt(index[0])));
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                index[0]++;
            } else {
                ((Timer) e.getSource()).stop();
            }
        });

        timer.start();
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
     * Shows the pay bills view.
     */
    public void showPaybillView(){
        if (paybillView != null) {
            // Update view manager state so ESC can properly switch back to game
            viewManagerModel.setState(PAYBILL_CARD);
            viewManagerModel.firePropertyChange();
        }
    }

    /**
     * Loads a saved game from file.
     */
    private void loadGame() {

        SwingUtilities.invokeLater(() -> {
            LoadingGameView loadingGameView = new LoadingGameView(loadProgressViewModel);

            JFrame frame = new JFrame("Loading...");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.add(loadingGameView);
            frame.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                new Timer(2550, e -> {
                    frame.dispose();
                    loadGameData();
                }) {{
                    setRepeats(false);
                }}.start();
            });
        });
    }

    private void loadGameData() {
        // Check if save file exists
        java.io.File saveFile = new java.io.File(SAVE_FILE);
        if (!saveFile.exists()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No saved game found!",
                    "Cannot Load",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            // Create a temporary GameMap for loading
            GameMap tempGameMap = new GameMap();

            Player loadedPlayer = loadProgressController.loadGame(tempGameMap, SAVE_FILE);

            // If we haven't initialized the game panel yet, do it now
            if (gamePanel == null) {
                initializeGamePanel(loadedPlayer, tempGameMap);
            } else {
                // Replace the current player with the loaded player
                this.player = loadedPlayer;

                // Update the player movement use case with the loaded player
                PlayerMovementUseCase playerMovementUseCase = new PlayerMovementUseCase(loadedPlayer);
                playerInputController.updatePlayerMovementUseCase(playerMovementUseCase);

                // Update the game panel with the loaded game state
                gamePanel.loadGameState(playerMovementUseCase, tempGameMap);

                // Update stock game view with loaded player
                if (stockGameView != null) {
                    stockGameView.setPlayer(loadedPlayer);
                }
            }

            // Switch to game view
            cardLayout.show(cardPanel, GAME_CARD);
            startGame();

            JOptionPane.showMessageDialog(
                    this,
                    "Game loaded successfully! Welcome back, " + loadProgressViewModel.getPlayerName() + "!" +
                            "\n Last Save: " + loadProgressViewModel.getRecentSaveDate(),
                    "Game Loaded",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load game: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
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


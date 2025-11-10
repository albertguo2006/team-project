# Project Readme: California Prop. 65

**Team:** TUT0401-14
**Domain:** Stock Market and IRL Simulator Game

-----

## 1\. Project Description

California Prop. 65 is a 2D simulation game that provides a satirical commentary on the financial pressures of modern life. The player must navigate an exaggerated capitalistic world, managing a 9-5 job, paying bills, and handling unexpected life events. The core gameplay loop involves surviving, managing virtual money, and attempting to build wealth through a simulated stock market.

This project will be developed as a standalone **Java application** using the **Java Swing GUI toolkit**. It will strictly adhere to **Clean Architecture** principles to ensure a maintainable, testable, and scalable codebase.

## Quick Start: How to Run the Program

### Prerequisites
- Java 11 or higher installed on your system
- `javac` compiler available in your PATH

### Option 1: Using Maven (Recommended)
```bash
cd /path/to/team-project
mvn clean compile exec:java -Dexec.mainClass="app.Main"
```

### Option 2: Manual Compilation & Execution
```bash
# 1. Navigate to the source directory
cd src/main/java

# 2. Compile all Java files
javac -d ../../../target/classes \
  app/Main.java \
  entity/Player.java \
  entity/NPC.java \
  entity/Event.java \
  entity/Stock.java \
  use_case/Direction.java \
  use_case/PlayerMovementUseCase.java \
  interface_adapter/events/PlayerInputController.java \
  view/GamePanel.java \
  view/MainGameWindow.java

# 3. Run the application
cd ../../../target/classes
java app.Main
```

### Option 3: Quick Test (One-Line Compile & Run)
```bash
cd src/main/java && \
javac -d /tmp/prop65 \
  app/Main.java entity/Player.java entity/NPC.java entity/Event.java \
  entity/Stock.java use_case/Direction.java use_case/PlayerMovementUseCase.java \
  interface_adapter/events/PlayerInputController.java view/GamePanel.java view/MainGameWindow.java && \
cd /tmp/prop65 && java app.Main
```

### Controls (Use Case 1: Character Movement)
Once the game window opens, use these controls:

| Key | Action |
|-----|--------|
| **W** | Move character up |
| **A** | Move character left |
| **S** | Move character down |
| **D** | Move character right |
| **W + D** (or any combination) | Move diagonally |
| **ESC** | Exit the application |

### What You'll See
- A green game window (800x600 pixels)
- A blue character sprite in the center that you can control
- A yellow arrow indicating movement direction
- HUD display showing:
  - Current position (X, Y coordinates)
  - Cash balance
  - Movement status ("MOVING" indicator)
  - Control instructions

### Troubleshooting

**"Command not found: java"**
- Ensure Java is installed: `java -version`
- If not installed, download from [java.com](https://www.java.com) or use your package manager

**"javac: command not found"**
- You need the Java Development Kit (JDK), not just JRE
- Download JDK from [oracle.com](https://www.oracle.com/java/technologies/downloads/) or use:
  ```bash
  sudo apt-get install default-jdk  # Ubuntu/Debian
  brew install openjdk              # macOS
  ```

**Game window doesn't appear**
- Ensure you're running on a display (not headless)
- On Linux, you may need: `DISPLAY=:0 java app.Main`

**Character doesn't respond to WASD input**
- Click on the game window to ensure it has focus
- Ensure caps lock is off (the program uses lowercase WASD)

### Core MVP Features

The Minimum Viable Product (MVP) will focus on delivering the core "survive the day" loop. This includes:

1.  **2D Environment Interaction:** The player can move a 2D character sprite around a basic environment (e.g., apartment, office).
2.  **Go to Work / Stock Minigame:** The player's job is represented by a stock trading minigame, which is the primary method of earning money.
3.  **Core Systems:** The game will support saving/loading progress, a daily cycle (go to sleep), random events, and basic NPC interaction.

-----

## 2\. Project Architecture: CLEAN Architecture in Java Swing

To meet the project requirements, we will separate our application into the four distinct layers of Clean Architecture. This ensures that our core game logic (Domain and Application) is completely independent of our GUI (Infrastructure).

1.  **1\_Domain (Entities):**

      * **Content:** Pure Java Objects (POJOs) that represent the core data and concepts of the game.
      * **Rules:** Contains no logic related to *how* the game is played or displayed. Knows nothing about Swing, databases, or APIs.
      * **Examples:** `Player.java`, `Stock.java`, `NPC.java`, `Event.java`, `GameState.java`.

2.  **2\_Application (Use Cases):**

      * **Content:** The business logic and rules of the game. This layer orchestrates the Entities.
      * **Rules:** Contains all game logic (e.g., "how to calculate profit from a stock trade," "what happens when a player selects an event option"). It depends *only* on the Domain layer. It defines *interfaces* for any outside data or services it needs (e.g., `IStockDataService`, `IGameSaveRepository`).
      * **Examples:** `StockMarketUseCase.java`, `PlayerMovementUseCase.java`, `GameSaveUseCase.java`, `NPCInteractionUseCase.java`.

3.  **3\_Presentation (Controllers & Presenters):**

      * **Content:** The bridge between the Application (logic) and Infrastructure (GUI).
      * **Rules:** This layer adapts data for the view and translates user input into calls to the Use Cases.
      * **Controllers:** Java **Event Listeners** (`ActionListener`, `KeyListener`, `MouseListener`). They take raw input from Swing components (like a `JButton` click) and call the appropriate `UseCase` method.
      * **Presenters:** Classes responsible for formatting data from the Use Cases for the View. (e.g., taking a `List<Double>` of stock prices and preparing `(x, y)` coordinates for a `JPanel` to draw).

4.  **4\_Infrastructure (Frameworks & Drivers):**

      * **Content:** The "outside world." This is where Swing, our API clients, and our file system live.
      * **Rules:** This layer is the "glue" that implements the interfaces defined in the Application layer. It is volatile and can be swapped out.
      * **GUI (View):** All Java Swing components (`JFrame`, `JPanel`, `JButton`, `JDialog`).
      * **Services:** `AlphaVantageClient.java` (implements `IStockDataService`), `GeminiApiClient.java` (implements `ILLMService`). These classes contain the `java.net.http.HttpClient` logic.
      * **Persistence:** `FileSaveRepository.java` (implements `IGameSaveRepository`). This class uses `Gson` or `Jackson` to write a `GameState` object to a JSON file.

-----

## 3\. Recommended Package Structure

This structure enforces the architectural layers.

```java
src/
└── com/tut0401/prop65/
    ├── 1_domain/
    │   ├── entities/
    │   │   ├── Player.java
    │   │   ├── Stock.java
    │   │   ├── Npc.java
    │   │   ├── Event.java
    │   │   └── GameState.java
    │
    ├── 2_application/
    │   ├── usecases/
    │   │   ├── PlayerMovementUseCase.java
    │   │   ├── StockMarketUseCase.java
    │   │   ├── NpcInteractionUseCase.java
    │   │   ├── RandomEventUseCase.java
    │   │   ├── GameSaveUseCase.java
    │   │   └── FinancialUseCase.java
    │   └── interfaces/
    │       ├── IStockDataService.java  // Implemented by Infrastructure
    │       ├── ILlmService.java        // Implemented by Infrastructure
    │       └── IGameSaveRepository.java // Implemented by Infrastructure
    │
    ├── 3_presentation/
    │   ├── controllers/
    │   │   ├── PlayerInputController.java  // Implements KeyListener
    │   │   ├── StockMinigameController.java // Contains ActionListeners
    │   │   └── SettingsMenuController.java  // Contains ActionListeners
    │   └── presenters/
    │       └── StockGraphPresenter.java // Logic to prep data for graph
    │
    ├── 4_infrastructure/
    │   ├── gui/
    │   │   ├── MainGameWindow.java  // The main JFrame
    │   │   ├── GamePanel.java       // The main 2D game view (extends JPanel)
    │   │   ├── StockMinigamePanel.java // The stock game UI (extends JPanel)
    │   │   ├── NpcDialog.java       // (extends JDialog)
    │   │   ├── EventDialog.java     // (extends JDialog)
    │   │   └── BillDialog.java      // (extends JDialog)
    │   ├── services/
    │   │   ├── AlphaVantageClient.java // Implements IStockDataService
    │   │   └── GeminiApiClient.java    // Implements ILlmService
    │   └── persistence/
    │       └── FileSaveRepository.java // Implements IGameSaveRepository
    │
    └── Main.java // Main entry point, creates and shows the MainGameWindow
```

-----

## 4\. Core Game Loop (Swing Implementation)

To create a 2D game ("User Story \#1") in Swing, we cannot rely on a simple event-driven model. We must create a real-time **game loop**.

1.  **`MainGameWindow.java` (`JFrame`):** This is the main application window. It will hold the `GamePanel`.
2.  **`GamePanel.java` (`JPanel`):**
      * This class will override the `protected void paintComponent(Graphics g)` method.
      * Inside `paintComponent`, we will draw the entire game state: the level background, the player sprite, and any NPCs, using `Graphics2D` methods (e.g., `g.drawImage()`, `g.fillRect()`).
      * This panel will be the focus of the application.
3.  **`PlayerInputController.java` (`KeyListener`):**
      * This controller will be attached to the `GamePanel`.
      * It will listen for `keyPressed` and `keyReleased` events (e.g., W, A, S, D).
      * When a key is pressed, it will call a method in the `PlayerMovementUseCase` (e.g., `useCase.setMoving(Direction.UP, true)`).
4.  **The Game Loop (`javax.swing.Timer`):**
      * A `javax.swing.Timer` will be initialized in the `GamePanel` (or a main game manager) to fire approximately 60 times per second (e.g., every 16ms).
      * This `Timer` is the "heartbeat" of the game.
      * On each "tick" (its `actionPerformed` method), it will:
        1.  **Update:** Call a main `update()` method, which in turn calls the `PlayerMovementUseCase.updatePosition()` to calculate the player's new `x, y` coordinates based on current input.
        2.  **Render:** Call `gamePanel.repaint()`. This tells Swing to schedule a call to `paintComponent`, which will redraw the player at their new coordinates.

-----

## 5\. MVP Module Breakdown & Team Responsibilities

### Albert: Use Case \#1 (Environment Interaction)

  * **User Story:** As a user, I want to be able to interact with and move around in a virtual environment.
  * **Architecture:**
      * **Domain:** Modify `Player.java` to include `double x`, `double y`, `double speed`.
      * **Application:** Create `PlayerMovementUseCase.java`. This class will manage the player's position and movement state.
          * Methods: `setMovementState(Direction dir, boolean isMoving)`, `updatePosition(double deltaTime)`. This method calculates the new `x, y` based on speed and time, and handles basic collision detection (checking against a simple map array).
      * **Presentation:** Create `PlayerInputController.java` (implements `KeyListener`). On `keyPressed("W")`, call `useCase.setMovementState(Direction.UP, true)`.
      * **Infrastructure:**
          * Create `GamePanel.java` (extends `JPanel`). This is the core view.
          * Implement the `javax.swing.Timer` game loop here. The loop's `actionPerformed` will call `playerMovementUseCase.updatePosition()` and then `this.repaint()`.
          * Implement `paintComponent(Graphics g)`. This method will read `player.getX()` and `player.getY()` (from the entity) and draw a `g.fillRect(player.getX(), ...)` or `g.drawImage(...)`.
          * Implement the interaction trigger (e.g., if player `x, y` is near an object and "E" is pressed).

### Cynthia: Use Case \#6 (Stock Investing)

  * **User Story:** As a user, I want to work a 9-5 and invest in a simulated stock market.
  * **Architecture:**
      * **Domain:** Create `Stock.java` (with `priceHistory`). Modify `Player.java` (with `cashBalance`).
      * **Application:**
          * Create `IStockDataService.java` interface (e.g., `List<Double> getStockHistory(String ticker)`).
          * Create `StockMarketUseCase.java`. This is the *logic core*.
          * Methods: `startMinigame(Player p)`. This fetches data via `IStockDataService`, sets up the 30-second game timer (e.g., `java.util.Timer`), and generates the price walk.
          * Methods: `buy(Player p)`, `sell(Player p)`, `endMinigame(Player p)`. These methods modify the `Player`'s `cashBalance` and stock holdings.
      * **Infrastructure (Services):** Create `AlphaVantageClient.java` (implements `IStockDataService`). This class uses `java.net.http.HttpClient` to call the Alpha Vantage API and parse the JSON response (using `Gson` or `Jackson`).
      * **Infrastructure (GUI):** Create `StockMinigamePanel.java` (extends `JPanel`). This panel will be shown in the `MainGameWindow` (e.g., using a `CardLayout`) when the game starts. It will contain "Buy" and "Sell" `JButton`s and a `JPanel` for the graph.
      * **Presentation:** Create `StockMinigameController.java`. This adds `ActionListener`s to the buttons. The "Buy" listener calls `stockMarketUseCase.buy()`. This layer also needs a way to get data *from* the use case (e.g., via the Observer pattern) to update the graph, which will be drawn in the `StockMinigamePanel`'s `paintComponent`.

### Joshua: Use Case \#2 (Random Events)

  * **User Story:** As a user, experience random events/challenges.
  * **Architecture:**
      * **Domain:** Create `Event.java` and `EventOption.java` (with `eventDescription`, `optionText`, `moneyEffect`).
      * **Application:** Create `RandomEventUseCase.java`.
          * Methods: `triggerRandomEvent()`. This method randomly selects an `Event` from a predefined list (e.g., loaded from a config file) and returns it.
          * Methods: `selectEventOption(Player p, EventOption option)`. This method applies the `moneyEffect` to the `p.cashBalance`.
      * **Infrastructure (GUI):** Create `EventDialog.java` (extends `JDialog`). This is a modal pop-up window.
          * Its constructor will take an `Event` object.
          * It will dynamically create a `JLabel` for the `eventDescription` and a `JButton` for each `EventOption`.
      * **Presentation (Controller):** The `ActionListener` for each option `JButton` (created in the `EventDialog`) will call `randomEventUseCase.selectEventOption()` with the corresponding option, and then `dispose()` the dialog.
      * **Integration:** Sherry's `TimeUseCase` will call `randomEventUseCase.triggerRandomEvent()` and show this dialog.

### Jayden: Use Case \#3 (NPC Interactions)

  * **User Story:** As a user, I want to be able to interact/talk to other characters or npcs.
  * **Architecture:**
      * **Domain:** Create `Npc.java` (with `name`, `dialoguePrompt`, `x`, `y`). Modify `Player.java` to hold `relationships`.
      * **Application:**
          * Create `ILlmService.java` interface (e.g., `String getDynamicResponse(String prompt)`).
          * Create `NpcInteractionUseCase.java`.
          * Methods: `interact(Player p, Npc npc, String playerInput)`. This method builds the full prompt, calls `iLlmService.getDynamicResponse()`, and returns the NPC's response string.
      * **Infrastructure (Services):** Create `GeminiApiClient.java` (implements `ILlmService`). This uses `HttpClient` to call the Google Gemini API.
      * **Infrastructure (GUI):** Create `NpcDialog.java` (extends `JDialog`). This modal pop-up contains a `JTextArea` (for chat history), a `JTextField` (for user input), and a "Send" `JButton`.
      * **Presentation (Controller):** The "Send" button's `ActionListener` gets text from the `JTextField`, calls `npcInteractionUseCase.interact()`, and appends both the player's input and the NPC's response to the `JTextArea`.
      * **Integration:** Albert's `PlayerMovementUseCase` (or `PlayerInputController`) will detect an interaction (e.g., "E" key near an NPC) and trigger this dialog.

### Anjani: Use Case \#4 (Saving/Loading)

  * **User Story:** As a user, I want to be able to save/load my progress.
  * **Architecture:**
      * **Domain:** Create `GameState.java`. This is a POJO that holds *all* data to be persisted (the `Player` entity, `List<Npc>` states, current in-game day, etc.).
      * **Application:**
          * Create `IGameSaveRepository.java` interface (e.g., `void save(GameState state)`, `GameState load()`).
          * Create `GameSaveUseCase.java`.
          * Methods: `saveGame()`. This method gathers all current data from the live `Player` entity (and other entities) into a new `GameState` object and passes it to `iGameSaveRepository.save()`.
          * Methods: `loadGame()`. This method calls `iGameSaveRepository.load()` and receives a `GameState` object. It then *updates* the application's *current* live `Player` entity (and others) with this loaded data.
      * **Infrastructure (Persistence):** Create `FileSaveRepository.java` (implements `IGameSaveRepository`). This class uses `Gson` or `Jackson` to serialize the `GameState` object to a JSON file (e.g., `save.json`) and deserialize it.
      * **Infrastructure (GUI):** Add a `JMenuBar` to the `MainGameWindow.java`.
      * **Presentation (Controller):** Create `SettingsMenuController.java`. This adds `ActionListener`s to "Save" and "Load" `JMenuItem`s, which call `gameSaveUseCase.saveGame()` and `gameSaveUseCase.loadGame()` respectively.

### Sherry: Use Case \#5 (Everyday Tasks: Bills + Sleep)

  * **User Story:** As a user, I want to be able to... go through everyday tasks (pay bills, ... go to bed).
  * **Architecture:**
      * **Domain:** Modify `Player.java` for `cashBalance`.
      * **Application:**
          * Create `FinancialUseCase.java`. Methods: `payBills(Player p, double amount)`. This checks if the player has enough `cashBalance` and subtracts the amount.
          * Create `TimeUseCase.java`. This orchestrates the end-of-day sequence.
          * Methods: `goToSleep(Player p)`. This is a critical method that calls other use cases in order:
            1.  Calls `Joshua_RandomEventUseCase.triggerRandomEvent()` (and shows the dialog).
            2.  Generates bills and shows the `BillDialog`.
            3.  Calls `Anjani_GameSaveUseCase.saveGame()` (this is the auto-save).
            4.  Advances the in-game day counter.
      * **Infrastructure (GUI):**
          * Create `BillDialog.java` (extends `JDialog`). Shows the user their bills and a "Pay" button.
          * The "Go to Sleep" trigger will be in Albert's `GamePanel` (e.g., player walks to a bed and presses "E").
      * **Presentation (Controller):** The "Pay" button's `ActionListener` in `BillDialog` will call `financialUseCase.payBills()`. The "Sleep" interaction will call `timeUseCase.goToSleep()`.

-----

## 6\. API and Data Persistence Plan

  * **API Usage:**
    1.  **Alpha Vantage:** (Cynthia) Used via `AlphaVantageClient.java` to get historical stock data for the trading minigame.
    2.  **Google Gemini:** (Jayden) Used via `GeminiApiClient.java` to generate dynamic dialogue for NPCs.
  * **Data Persistence:**
    1.  **File System:** (Anjani) The `FileSaveRepository.java` will implement the `IGameSaveRepository` interface. It will use the `Gson` library to serialize the `GameState` domain object into a `save.json` file. This fulfills the data persistence requirement.

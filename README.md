# California Prop. 65

**Team:** TUT0401-14
**Domain:** Stock Market and IRL Simulator Game

## 1\. Project Description

**California Prop. 65** is a simulation game that satirizes the financial pressures of everyday life. Players must navigate a comically exaggerated capitalistic world, balancing a draining 9-5 job, paying bills, and managing financial setbacks. The core gameplay loop involves surviving, managing virtual money, and attempting to get ahead by investing in a simulated stock market.

### Core MVP Features

The primary goal for our Minimum Viable Product (MVP) is to deliver the core "survive the day" loop. This includes:

1.  **Environment Interaction:** The player can move around a basic 2D/3D environment (e.g., apartment, office).
2.  **Go to Work:** The player can travel to a "work" location.
3.  **Stock Minigame:** Arriving at work triggers the core stock trading minigame, which is the primary method of earning money.
4.  **Core Systems:** Basic save/load functionality, bill payment, and a "go to sleep" mechanic to end the day.

## 2\. Project Architecture: CLEAN Architecture

To ensure our project is **scalable**, **testable**, and **maintainable**, we will follow the principles of **CLEAN Architecture**. This separates our code into distinct layers, where dependencies only point *inward*.

  * **The Problem:** We don't want our *game logic* (like how much a stock price changes) to be mixed up with *engine-specific code* (like how to display a graph in Unity).
  * **The Solution:** We create layers. The "core" of our game (the rules) knows nothing about the "outside" (Unity, APIs, databases).

Here are our layers, from inside to out:

1.  **Domain (Entities):** The core data structures and objects. Pure C\# classes with no outside dependencies.
      * *Examples: `Player`, `Stock`, `Event`, `NPC`*
2.  **Application (Use Cases):** The business logic and rules of the game. This layer orchestrates the Entities. It defines *interfaces* for any outside data it needs (e.t., "I need stock data," but doesn't know *how* we get it).
      * *Examples: `StockMarketUseCase`, `PlayerMovementUseCase`, `GameSaveUseCase`*
3.  **Presentation (Controllers/Presenters):** Acts as the bridge. It translates input from the *Frameworks* layer (like a button click) into a call to a *Use Case*. It also takes data from the *Use Case* and formats it for the UI.
      * *Examples: `PlayerInputController`, `StockMinigamePresenter`*
4.  **Infrastructure (Frameworks & Drivers):** The "outside world." This is where Unity, our APIs (Alpha Vantage, Gemini), and our save/load system live. This layer *implements* the interfaces defined in the Application layer.
      * *Examples: `UnityTimeProvider`, `AlphaVantageClient`, `FileSaveSystem`*

-----

## 3\. Recommended Folder Structure

This folder structure strictly enforces the CLEAN architecture layers.

```
Assets/
├── 1_Domain/
│   ├── Entities/
│   │   ├── Player.cs
│   │   ├── Stock.cs
│   │   ├── NPC.cs
│   │   ├── Event.cs
│   │   └── GameState.cs
│   └── Interfaces/
│       ├── IPlayerRepository.cs
│       ├── IStockRepository.cs
│       ├── INPCRepository.cs
│       └── IGameSaveRepository.cs
│
├── 2_Application/
│   ├── UseCases/
│   │   ├── PlayerMovementUseCase.cs
│   │   ├── StockMarketUseCase.cs
│   │   ├── NPCInteractionUseCase.cs
│   │   ├── RandomEventUseCase.cs
│   │   ├── GameSaveUseCase.cs
│   │   └── FinancialUseCase.cs
│   └── Interfaces/
│       ├── IStockDataService.cs
│       ├── ILLMService.cs
│       └── ISaveLoadService.cs
│
├── 3_Presentation/
│   ├── Controllers/
│   │   ├── PlayerInputController.cs
│   │   ├── StockMinigameController.cs
│   │   ├── NPCInteractionController.cs
│   │   ├── BillsUIController.cs
│   │   └── SettingsMenuController.cs
│   └── Presenters/
│       ├── StockGraphPresenter.cs
│       ├── DialoguePresenter.cs
│       ├── PlayerStatsUIPresenter.cs
│       └── BillsUIPresenter.cs
│
├── 4_Infrastructure/
│   ├── Services/ (Implementations of Application Interfaces)
│   │   ├── AlphaVantageClient.cs (Implements IStockDataService)
│   │   ├── GeminiApiClient.cs (Implements ILLMService)
│   │   ├── FileSaveSystem.cs (Implements ISaveLoadService)
│   ├── Unity/ (All MonoBehaviour scripts)
│   │   ├── GameLoopManager.cs (Main entry point)
│   │   ├── PlayerView.cs (Handles Rigidbody, Transform, Animation)
│   │   ├── NPCView.cs
│   │   └── Triggers/
│   │       ├── WorkZoneTrigger.cs
│   │       ├── SleepTrigger.cs
│   │       └── NPCInteractable.cs
│   ├── ScriptableObjects/ (For configuration)
│   │   ├── StockConfig.cs
│   │   ├── EventConfig.cs
│   │   └── NPCConfig.cs
│
├── Prefabs/
├── Scenes/
└── Settings/
```

-----

## 4\. MVP Module Breakdown & Team Responsibilities

Here is the detailed architecture and task list for each MVP module.

### Albert: Use Case \#1 (Environment Interaction)

  * **User Story:** As a user, I want to be able to interact with and move around in a virtual environment.
  * **Core Logic:** Translate user input into character movement while respecting physics and boundaries.

**Architecture:**

1.  **Domain (`/1_Domain/Entities/Player.cs`):**
      * The `Player` entity will hold data like `currentPosition` (Vector3) and `movementSpeed` (float). This class should *not* know about Unity's `Transform` or `Rigidbody`.
2.  **Application (`/2_Application/UseCases/PlayerMovementUseCase.cs`):**
      * Create a class `PlayerMovementUseCase`.
      * It will have a method: `Move(Player player, Vector3 direction)`.
      * This method contains the *logic* of movement (e.g., applying speed, checking for stamina if we add it later). It does *not* handle collisions.
3.  **Presentation (`/3_Presentation/Controllers/PlayerInputController.cs`):**
      * This **MonoBehaviour** script will live in the scene.
      * It will use Unity's Input System (e.g., `Input.GetAxis("Horizontal")`).
      * In its `Update()` loop, it will read the input, create a `direction` vector, and call the `PlayerMovementUseCase.Move()` method.
4.  **Infrastructure (`/4_Infrastructure/Unity/PlayerView.cs`):**
      * This **MonoBehaviour** script is attached to the Player GameObject.
      * It holds the reference to the `Rigidbody` or `CharacterController`.
      * The `PlayerMovementUseCase` (or an intermediary) will update the `Player` entity's position. This `PlayerView` script will observe that change (or be called directly) and apply the actual movement to the `Rigidbody` (e.g., `rb.MovePosition()`). This class handles the *physics and collisions*.

-----

### Cynthia: Use Case \#6 (Stock Investing)

  * **User Story:** As a user, I want to work a 9-5 and invest in a simulated stock market to earn virtual money.
  * **Core Logic:** Fetch real stock data, generate a playable minigame, and handle the buy/sell logic within a time limit.

**Architecture:**

1.  **Domain (`/1_Domain/Entities/Stock.cs`, `Player.cs`):**
      * `Stock` class: Holds `tickerSymbol`, `companyName`, and `List<double> priceHistory`.
      * `Player` class: Holds `cashBalance`.
2.  **Application (`/2_Application/UseCases/StockMarketUseCase.cs`):**
      * This is the *brain* of the minigame. It is a **pure C\# class**.
      * It needs data. It will depend on an *interface*: `IStockDataService`.
      * **Methods:**
          * `StartMinigame(Player player, string stockTicker)`: Calls `IStockDataService.GetStockData()`, stores the data, sets the timer (30s), and calculates the player's initial investment (40% of cash).
          * `Tick()`: Called every frame/second by the controller. It advances the minigame timer and generates the *next* stock price based on the algorithm (weighted random walk on real data).
          * `Buy(Player player)`: Logic to buy stock with all available minigame cash.
          * `Sell(Player player)`: Logic to sell all held stock.
          * `EndMinigame(Player player)`: Called when timer hits 0 or user exits. Sells all stock, calculates profit/loss, and updates the `player.cashBalance`.
3.  **Presentation (`/3_Presentation/`):**
      * `StockMinigameController.cs`: A **MonoBehaviour** that manages the minigame UI.
          * Listens for "Buy" and "Sell" button clicks and calls the corresponding `StockMarketUseCase` methods.
          * Runs a `Coroutine` or uses `Update()` to call `StockMarketUseCase.Tick()` and update the timer UI.
      * `StockGraphPresenter.cs`: A **MonoBehaviour** that takes the `priceHistory` list from the `StockMarketUseCase` and renders it on the UI (e.g., using a LineRenderer or UI\_Graph asset).
4.  **Infrastructure (`/4_Infrastructure/`):**
      * `AlphaVantageClient.cs`: Implements the `IStockDataService` interface. This class is the *only* one that knows about Alpha Vantage. It will use `UnityWebRequest` to call the API and parse the JSON into a `List<double>`.
      * `WorkZoneTrigger.cs`: A **MonoBehaviour** (e.g., a box collider) that the player enters. On trigger, it calls the `StockMinigameController` to start the game.

-----

### Joshua: Use Case \#2 (Random Events)

  * **User Story:** As a user, experience random events/challenges.
  * **Core Logic:** Trigger an event, present options, and apply the outcome.

**Architecture:**

1.  **Domain (`/1_Domain/Entities/Event.cs`, `Player.cs`):**
      * `Event` class: Holds `eventID`, `eventDescription`, and a `List<EventOption>`.
      * `EventOption` class: Holds `optionText` and `outcome` (e.g., a struct with `moneyChange` and `statChange`).
2.  **Application (`/2_Application/UseCases/RandomEventUseCase.cs`):**
      * **Methods:**
          * `TriggerRandomEvent(Player player)`: Selects a random `Event` (e.g., from a `ScriptableObject` list passed in). Checks if `player.events` list already contains it.
          * `SelectEventOption(Player player, EventOption option)`: Applies the `option.outcome` to the `player` (e.g., `player.cashBalance += option.outcome.moneyChange`). Adds `eventID` to `player.events`.
3.  **Presentation (`/3_Presentation/`):**
      * `EventPresenter.cs`: A **MonoBehaviour** that controls the Event pop-up UI. It's normally hidden.
      * `RandomEventUseCase` will call this `Presenter` to show the UI, populating the `eventDescription` text and creating buttons for each `EventOption`.
      * The buttons' `OnClick` listeners will call `RandomEventUseCase.SelectEventOption()`.
4.  **Infrastructure (`/4_Infrastructure/Unity/Triggers/`):**
      * Event triggers can be placed in the world (e.g., `SleepTrigger` calls `RandomEventUseCase.TriggerRandomEvent()` before sleeping).

-----

### Jayden: Use Case \#3 (NPC Interactions)

  * **User Story:** As a user, I want to be able to interact/talk to other characters or npcs.
  * **Core Logic:** Send user input and NPC personality to an LLM and display the response.

**Architecture:**

1.  **Domain (`/1_Domain/Entities/NPC.cs`, `Player.cs`):**
      * `NPC` class: Holds `name` and `dialoguePrompt` (the personality).
      * `Player` class: Holds `Map<NPC, Integer> relationships`.
2.  **Application (`/2_Application/UseCases/NPCInteractionUseCase.cs`):**
      * Depends on an *interface*: `ILLMService`.
      * **Methods:**
          * `StartInteraction(NPC npc)`: Prepares the dialogue state.
          * `SendPlayerMessage(Player player, NPC npc, string playerInput)`:
            1.  Constructs a full prompt (e.g., `npc.dialoguePrompt` + "The player says: " + `playerInput`).
            2.  Calls `await ILLMService.GetResponse(fullPrompt)`.
            3.  Receives the `npcResponse` string.
            4.  (Bonus) Can do simple sentiment analysis on `playerInput` to update `player.relationships`.
            5.  Returns the `npcResponse` to the Presentation layer.
3.  **Presentation (`/3_Presentation/`):**
      * `NPCInteractionController.cs`: A **MonoBehaviour**. When the player interacts, it calls `StartInteraction`. It takes text from an `InputField` and calls `SendPlayerMessage`.
      * `DialoguePresenter.cs`: A **MonoBehaviour**. Receives the `npcResponse` string from the `UseCase` and displays it in a `TextMeshProUGUI` component.
4.  **Infrastructure (`/4_Infrastructure/`):**
      * `GeminiApiClient.cs`: Implements the `ILLMService` interface. This class handles the `UnityWebRequest` to the Google Gemini API, including managing the API key and parsing the JSON response.
      * `NPCInteractable.cs`: A **MonoBehaviour** on the NPC GameObject that detects the player's "interact" button press.

-----

### Anjani: Use Case \#4 (Saving/Loading)

  * **User Story:** As a user, I want to be able to save/load my progress.
  * **Core Logic:** Gather all game state, serialize it, and write it to a file. Then, be able to read, deserialize, and restore the state.

**Architecture:**

1.  **Domain (`/1_Domain/Entities/GameState.cs`):**
      * A **serializable** C\# class.
      * It contains *data*, not logic. E.g., `PlayerData`, `WorldData` (like in-game time), `List<NPCData>`.
      * `PlayerData` would store `cashBalance`, `relationships`, `events`, `position`.
2.  **Application (`/2_Application/UseCases/GameSaveUseCase.cs`):**
      * Depends on an *interface*: `ISaveLoadService`.
      * **Methods:**
          * `SaveGame()`:
            1.  Gathers all necessary data from the `Player` entity and other game systems.
            2.  Assembles a new `GameState` object.
            3.  Calls `ISaveLoadService.Save(gameState)`.
          * `LoadGame()`:
            1.  Calls `ISaveLoadService.Load()`.
            2.  Receives a `GameState` object.
            3.  Restores the game. This is tricky. It will likely need to *push* this data back into the `Player` entity and notify other systems to update themselves (e.g., move the player GameObject).
          * `AutoSave()`: Called by the `TimeUseCase` (Sherry's module) at the end of the day.
3.  **Presentation (`/3_Presentation/Controllers/SettingsMenuController.cs`):**
      * A **MonoBehaviour** for the settings UI.
      * The "Save" button `OnClick` listener calls `GameSaveUseCase.SaveGame()`.
      * The "Load" button `OnClick` listener (on the main menu) calls `GameSaveUseCase.LoadGame()`.
4.  **Infrastructure (`/4_Infrastructure/Services/FileSaveSystem.cs`):**
      * Implements the `ISaveLoadService` interface.
      * **Methods:**
          * `Save(GameState state)`: Uses `JsonUtility` (or Newtonsoft) to serialize the `state` object into a JSON string. Writes this string to a file using `File.WriteAllText(Application.persistentDataPath + "/save.json")`.
          * `Load()`: Reads the text from the file, deserializes it back into a `GameState` object, and returns it.

-----

### Sherry: Use Case \#5 (Everyday Tasks)

  * **User Story:** As a user, I want to manage my virtual money (pay bills, go to sleep).
  * **Core Logic:** Track in-game time. At set intervals, trigger bills. Allow the player to sleep to advance time and trigger saves.

**Architecture:**

1.  **Domain (`/1_Domain/Entities/Player.cs`):**
      * The `Player` entity holds `cashBalance`.
2.  **Application (`/2_Application/UseCases/FinancialUseCase.cs`, `TimeUseCase.cs`):**
      * `FinancialUseCase.cs`:
          * `PayBills(Player player, double amount)`: Checks if `player.cashBalance >= amount`. If yes, subtracts it. If no, returns an error (for the UI to display).
      * `TimeUseCase.cs`:
          * Depends on `GameSaveUseCase` (Anjani's module).
          * `GoToSleep(Player player)`:
            1.  Advances the in-game day counter.
            2.  Triggers the bill logic (e.g., generates a bill amount and calls `BillsUIPresenter` to show it).
            3.  Calls `GameSaveUseCase.AutoSave()`.
            4.  (Bonus) Can call `RandomEventUseCase.TriggerRandomEvent()`.
3.  **Presentation (`/3_Presentation/`):**
      * `BillsUIController.cs`: A **MonoBehaviour** that shows the bill pop-up. The "Pay" button calls `FinancialUseCase.PayBills()`. It will display an error if the Use Case returns one.
      * `SleepController.cs`: (Can be part of `BillsUIController`). Shows the "End Day?" confirmation.
4.  **Infrastructure (`/4_Infrastructure/`):**
      * `GameClock.cs`: A **MonoBehaviour** that can track the in-game time (e.g., a simple float that counts up).
      * `SleepTrigger.cs`: A **MonoBehaviour** (collider) on the bed. When interacted with, it calls `TimeUseCase.GoToSleep()`.

-----

## 5\. Core API Integration

1.  **Google Gemini (Jayden: NPC Interaction)**

      * **File:** `/4_Infrastructure/Services/GeminiApiClient.cs`
      * **Purpose:** To generate dynamic NPC dialogue.
      * **Method:** This class will implement `ILLMService`. It will have an `async` method `GetResponse(string prompt)` that uses `UnityWebRequest` to send a POST request to the Gemini API endpoint.
      * **Security:** The API key **must not** be in the code. Store it in a file ignored by Git (e.g., `api_keys.json`) and load it at runtime.

2.  **Alpha Vantage (Cynthia: Stock Minigame)**

      * **File:** `/4_Infrastructure/Services/AlphaVantageClient.cs`
      * **Purpose:** To fetch historical stock data for the minigame.
      * **Method:** This class will implement `IStockDataService`. It will have an `async` method `GetStockData(string ticker)` that uses `UnityWebRequest` to send a GET request. It will parse the "Time Series (Daily)" JSON response and extract, for example, the last 100 days of "4. close" prices into a `List<double>`.
      * **Security:** Same as above; store the API key securely.

This structure keeps your logic clean, makes it easy to swap out systems (e.g., change from Alpha Vantage to another API), and allows each team member to work on their module without causing conflicts.

Would you like me to elaborate on the `GameState` design or the specific data flow for the stock minigame?

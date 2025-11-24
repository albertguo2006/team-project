
# California Prop. 65 - Game Documentation

**Team:** TUT0401-14 (CSC207 LEC0201 Group 14)  
**Domain:** Stock Market and Life Simulator Game  
**Architecture:** Clean Architecture with Java Swing

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Quick Start](#quick-start)
3. [Architecture](#architecture)
4. [Implemented Features](#implemented-features)
5. [Game Systems](#game-systems)
6. [Package Structure](#package-structure)
7. [How to Play](#how-to-play)
8. [Testing](#testing)
9. [Future Features](#future-features)
10. [Team Members](#team-members)

---

## Project Overview

California Prop. 65 is a 2D simulation game that provides satirical commentary on the financial pressures of modern life. Players navigate an exaggerated capitalistic world, managing a 9-5 job, paying bills, and handling unexpected life events. The core gameplay loop involves surviving a work week (Monday-Friday), managing money, and attempting to build wealth through a simulated stock market.

### Key Features

- **2D Environment Navigation**: Move through multiple interconnected zones (home, streets, subway, office)
- **Day-Based Progression**: Experience a full work week from Monday to Friday
- **Stock Trading Minigame**: Earn money by playing the stock market
- **Random Events**: Encounter unpredictable life challenges
- **NPC Interactions**: Talk to characters with AI-generated dialogue
- **Bill Payment System**: Manage recurring expenses
- **Save/Load System**: Persist game progress
- **Resizable Window**: 16:10 aspect ratio with automatic scaling (1920x1200 virtual resolution)

### Technology Stack

- **Language**: Java 11+
- **GUI Framework**: Java Swing
- **Build Tool**: Maven
- **APIs**: Alpha Vantage (stock data), Google Gemini (NPC dialogue)
- **Charting Library**: XChart (stock price visualization)
- **Data Format**: JSON (for saves, events, items, NPC prompts)
- **Audio**: Java AudioSystem (WAV format)

---

## Quick Start

### Prerequisites

- Java 11 or higher
- Maven (recommended) or `javac`

### Running the Game

#### Option 1: Using Maven (Recommended)

```bash
cd /path/to/team-project
mvn clean compile exec:java -Dexec.mainClass="app.Main"
```

#### Option 2: Using IntelliJ IDEA

1. Open project in IntelliJ IDEA
2. Build → Build Project (Ctrl+F9 / Cmd+F9)
3. Right-click on [`Main.java`](src/main/java/app/Main.java) → Run 'Main.main()'

#### Option 3: Manual Compilation

```bash
cd src/main/java
javac -d ../../../target/classes app/Main.java
cd ../../../target/classes
java app.Main
```

### Controls

| Key | Action |
|-----|--------|
| **W** | Move character up |
| **A** | Move character left |
| **S** | Move character down |
| **D** | Move character right |
| **E** | Interact / Sleep (when in sleep zone) |
| **ESC** | Pause menu / Exit |
| **ENTER** | Confirm / Continue |

### Troubleshooting

**"Command not found: java"**
- Install JDK 11+: `sudo apt-get install default-jdk` (Ubuntu) or download from [oracle.com](https://www.oracle.com/java/technologies/downloads/)

**Game window doesn't appear**
- Ensure running on a display (not headless)
- Linux: Try `DISPLAY=:0 java app.Main`

**Character doesn't respond to input**
- Click on game window to give it focus
- Ensure caps lock is off

---

## Architecture

This project strictly follows **Clean Architecture** principles with four distinct layers:

### 1. Domain Layer (Entities)

**Location**: [`src/main/java/entity/`](src/main/java/entity/)

Pure Java objects representing core game concepts. No dependencies on frameworks or I/O.

**Key Entities**:
- [`Player.java`](src/main/java/entity/Player.java) - Player state (position, balance, stats, day, health)
- [`Day.java`](src/main/java/entity/Day.java) - Weekday enum (Monday-Friday)
- [`DaySummary.java`](src/main/java/entity/DaySummary.java) - Daily financial summary
- [`GameEnding.java`](src/main/java/entity/GameEnding.java) - Week completion endings
- [`Zone.java`](src/main/java/entity/Zone.java) - Game map zones with neighbors
- [`GameMap.java`](src/main/java/entity/GameMap.java) - Complete map with all zones
- [`Event.java`](src/main/java/entity/Event.java) - Random events with outcomes
- [`NPC.java`](src/main/java/entity/NPC.java) - Non-player characters
- [`Stock.java`](src/main/java/entity/Stock.java) - Stock market data
- [`Bill.java`](src/main/java/entity/Bill.java) - Bills to pay
- [`Item.java`](src/main/java/entity/Item.java) - Inventory items
- [`Portfolio.java`](src/main/java/entity/Portfolio.java) - Player's stock holdings

### 2. Application Layer (Use Cases)

**Location**: [`src/main/java/use_case/`](src/main/java/use_case/)

Business logic and game rules. Depends only on entities and defines interfaces for external services.

**Key Use Cases**:
- [`PlayerMovementUseCase.java`](src/main/java/use_case/PlayerMovementUseCase.java) - Character movement logic
- [`sleep/SleepInteractor.java`](src/main/java/use_case/sleep/SleepInteractor.java) - Day advancement and health restoration
- [`paybills/PaybillInteractor.java`](src/main/java/use_case/paybills/PaybillInteractor.java) - Bill payment logic
- [`events/StartRandomEvent/`](src/main/java/use_case/events/StartRandomEvent/) - Random event triggering
- [`events/ActivateRandomOutcome/`](src/main/java/use_case/events/ActivateRandomOutcome/) - Event outcome processing
- [`npc_interactions/NpcInteractionsInteractor.java`](src/main/java/use_case/npc_interactions/NpcInteractionsInteractor.java) - NPC dialogue
- [`stock_game/`](src/main/java/use_case/stock_game/) - Stock trading minigame logic
- [`save_progress/SaveProgressInteractor.java`](src/main/java/use_case/save_progress/SaveProgressInteractor.java) - Game saving
- [`load_progress/LoadProgressInteractor.java`](src/main/java/use_case/load_progress/LoadProgressInteractor.java) - Game loading

### 3. Interface Adapter Layer

**Location**: [`src/main/java/interface_adapter/`](src/main/java/interface_adapter/)

Bridges between use cases and views. Translates data and handles presentation logic.

**Components**:
- **Controllers**: Translate user input into use case calls
- **Presenters**: Format use case output for views
- **ViewModels**: Hold state for views with property change support
- [`ViewManagerModel.java`](src/main/java/interface_adapter/ViewManagerModel.java) - Coordinates view transitions

### 4. Infrastructure Layer (Frameworks & UI)

**Location**: [`src/main/java/view/`](src/main/java/view/), [`src/main/java/api/`](src/main/java/api/), [`src/main/java/data_access/`](src/main/java/data_access/)

External frameworks, APIs, file I/O, and GUI components.

**Views**:
- [`MainGameWindow.java`](src/main/java/view/MainGameWindow.java) - Main JFrame with CardLayout
- [`GamePanel.java`](src/main/java/view/GamePanel.java) - Main game view with rendering loop
- [`MainMenuPanel.java`](src/main/java/view/MainMenuPanel.java) - Main menu
- [`DaySummaryView.java`](src/main/java/view/DaySummaryView.java) - Daily summary screen
- [`EndGameView.java`](src/main/java/view/EndGameView.java) - Week ending screen
- [`PaybillView.java`](src/main/java/view/PaybillView.java) - Bill payment interface
- [`SettingsPanel.java`](src/main/java/view/SettingsPanel.java) - Game settings
- [`InGameMenuPanel.java`](src/main/java/view/InGameMenuPanel.java) - Pause menu

**Data Access**:
- [`SaveFileUserDataObject.java`](src/main/java/data_access/SaveFileUserDataObject.java) - Save file management
- [`LoadFileUserDataAccessObject.java`](src/main/java/data_access/LoadFileUserDataAccessObject.java) - Load file management
- [`EventDataAccessObject.java`](src/main/java/data_access/EventDataAccessObject.java) - Event data from JSON
- [`NPCDataAccessObject.java`](src/main/java/data_access/NPCDataAccessObject.java) - NPC data
- [`ItemDataAccessObject.java`](src/main/java/data_access/ItemDataAccessObject.java) - Item data

**APIs**:
- [`AlphaStockDataAccessObject.java`](src/main/java/api/AlphaStockDataAccessObject.java) - Alpha Vantage API client
- Google Gemini integration (for NPC dialogue)

### Dependency Rule

All dependencies point **inward** toward entities. Outer layers depend on inner layers, never the reverse. Interfaces invert dependencies when outer layers need to provide services to inner layers.

---

## Implemented Features

### ✅ Character Movement System

**Implementation**: Use Case #1  
**Responsible**: Albert

- 2D character movement with WASD keys
- Real-time game loop using `javax.swing.Timer` (~60 FPS)
- Collision detection and boundary checking
- Virtual 1920x1200 coordinate system with automatic scaling
- Smooth diagonal movement
- Position tracking in HUD

**Files**: [`PlayerMovementUseCase.java`](src/main/java/use_case/PlayerMovementUseCase.java), [`GamePanel.java`](src/main/java/view/GamePanel.java), [`PlayerInputController.java`](src/main/java/interface_adapter/events/PlayerInputController.java)

### ✅ Day Progression System

**Implementation**: Use Case #5 (Sleep)  
**Responsible**: Sherry

- Monday through Friday work week
- Sleep in designated zone (top-right 400x400px in Home)
- Press E to sleep when in zone
- Daily financial tracking (earnings and spending)
- Health restoration (100) after sleeping
- One sleep per day restriction
- Day summary screen with fade transitions
- Week ending based on final balance

**Files**: [`Day.java`](src/main/java/entity/Day.java), [`DaySummary.java`](src/main/java/entity/DaySummary.java), [`GameEnding.java`](src/main/java/entity/GameEnding.java), [`SleepInteractor.java`](src/main/java/use_case/sleep/SleepInteractor.java), [`DaySummaryView.java`](src/main/java/view/DaySummaryView.java), [`EndGameView.java`](src/main/java/view/EndGameView.java)

**Endings**:
- **Wealthy** (≥$5000): "The Wealthy Executive"
- **Comfortable** (≥$2000): "The Comfortable Professional"
- **Struggling** (≥$1000): "The Struggling Worker"
- **Broke** (<$1000): "The Defeated"

### ✅ Zone Transition System

**Implementation**: Map Navigation  
**Responsible**: Albert

- 9 interconnected zones (Home, Streets, Subway, Office, Grocery)
- Background images for each zone
- Zone-specific music (lofi and elevator)
- Edge-based transitions between adjacent zones
- Special subway tunnel system (Subway 1 ↔ Subway 2)
- Visual zone name display
- Color-coded zones (fallback if images fail)

**Files**: [`Zone.java`](src/main/java/entity/Zone.java), [`GameMap.java`](src/main/java/entity/GameMap.java), [`Transition.java`](src/main/java/entity/Transition.java), [`GamePanel.java`](src/main/java/view/GamePanel.java)

### ✅ Bill Payment System

**Implementation**: Use Case #5 (Bills)  
**Responsible**: Sherry

- Pay bills through in-game menu
- Track bill types and amounts
- Deduct from player balance
- Bill history tracking
- Integration with daily spending

**Files**: [`Bill.java`](src/main/java/entity/Bill.java), [`PaybillInteractor.java`](src/main/java/use_case/paybills/PaybillInteractor.java), [`PaybillView.java`](src/main/java/view/PaybillView.java)

### ✅ Random Events System

**Implementation**: Use Case #2  
**Responsible**: Joshua

- Random event triggering with pity system (25% increase per non-trigger)
- Multiple event outcomes with financial impacts
- Events loaded from [`events.json`](src/main/resources/events.json)
- Event outcome selection
- Player stat modifications

**Files**: [`Event.java`](src/main/java/entity/Event.java), [`EventOutcome.java`](src/main/java/entity/EventOutcome.java), [`StartRandomEventInteractor.java`](src/main/java/use_case/events/StartRandomEvent/StartRandomEventInteractor.java), [`ActivateRandomOutcomeInteractor.java`](src/main/java/use_case/events/ActivateRandomOutcome/ActivateRandomOutcomeInteractor.java)

### ✅ Display Scaling System

**Implementation**: Resolution System

- Virtual 1920x1200 resolution (16:10 aspect ratio)
- Automatic scaling to any window size
- Letterbox/pillarbox for non-16:10 windows
- Resizable window with 640x400 minimum
- HiDPI/Retina display support
- Wayland support on Linux (Java 17+)
- Coordinate translation between screen and virtual space

**Files**: [`GamePanel.java`](src/main/java/view/GamePanel.java), [`MainGameWindow.java`](src/main/java/view/MainGameWindow.java), [`Main.java`](src/main/java/app/Main.java)

### ✅ Menu System

**Implementation**: UI System

- Main menu (New Game, Load Game, Settings, Exit)
- Loading screen with progress bar
- In-game pause menu (Resume, Save, Settings, Pay Bills, Exit)
- Settings panel (resolution, volume, controls)
- CardLayout view management
- Keyboard navigation

**Files**: [`MainMenuPanel.java`](src/main/java/view/MainMenuPanel.java), [`InGameMenuPanel.java`](src/main/java/view/InGameMenuPanel.java), [`SettingsPanel.java`](src/main/java/view/SettingsPanel.java), [`LoadingScreenPanel.java`](src/main/java/view/LoadingScreenPanel.java)

### ⚠️ Partial Implementation

#### Stock Trading Minigame

**Implementation**: Use Case #6
**Responsible**: Cynthia
**Status**: Backend complete, chart visualization implemented

- Alpha Vantage API integration
- 30-second trading sessions
- Buy/sell stock mechanics
- Real-time price chart with 50-bar history (XChart)
- Dynamic y-axis that zooms to current price range
- Portfolio management

**Files**: [`AlphaStockDataAccessObject.java`](src/main/java/api/AlphaStockDataAccessObject.java), [`Stock.java`](src/main/java/entity/Stock.java), [`Portfolio.java`](src/main/java/entity/Portfolio.java), [`use_case/stock_game/`](src/main/java/use_case/stock_game/), [`StockGameView.java`](src/main/java/view/StockGameView.java)

#### NPC Interactions

**Implementation**: Use Case #3  
**Responsible**: Jayden  
**Status**: Backend complete, UI integration pending

- Google Gemini API for dynamic dialogue
- NPC prompts from [`npc_prompts.json`](src/main/resources/npc_prompts.json)
- Relationship tracking
- Context-aware responses

**Files**: [`NPC.java`](src/main/java/entity/NPC.java), [`NpcInteractionsInteractor.java`](src/main/java/use_case/npc_interactions/NpcInteractionsInteractor.java), [`NPCDataAccessObject.java`](src/main/java/data_access/NPCDataAccessObject.java)

#### Save/Load System

**Implementation**: Use Case #4  
**Responsible**: Anjani  
**Status**: Backend complete, frontend stubbed

- JSON save file format
- Full game state serialization
- Autosave on sleep
- Manual save/load from menu

**Files**: [`SaveFileUserDataObject.java`](src/main/java/data_access/SaveFileUserDataObject.java), [`LoadFileUserDataAccessObject.java`](src/main/java/data_access/LoadFileUserDataAccessObject.java), [`SaveProgressInteractor.java`](src/main/java/use_case/save_progress/SaveProgressInteractor.java)

---

## Game Systems

### Game Loop Architecture

The game runs on a real-time loop using `javax.swing.Timer`:

```
[Timer fires every 16ms - ~60 FPS]
         ↓
1. UPDATE PHASE
   - PlayerMovementUseCase.updatePosition()
   - Update player coordinates
   - Check collisions
   - Check zone transitions
         ↓
2. RENDER PHASE
   - GamePanel.repaint()
   - paintComponent() draws:
     * Background/zone image
     * Player sprite
     * HUD (health, balance, day, position)
     * Sleep prompt (if in zone)
         ↓
[Wait 16ms, repeat]
```

### Virtual Resolution System

All game logic operates in 1920x1200 virtual space:

1. **Calculate viewport** maintaining 16:10 aspect ratio
2. **Add letterbox/pillarbox** if needed for non-16:10 windows
3. **Scale Graphics2D transform** to map virtual → screen coordinates
4. **Render at virtual coordinates** (automatically scaled)

Benefits:
- Consistent coordinate system across all screen sizes
- Automatic scaling without code changes
- Maintains visual quality on HiDPI displays

### Zone Transition Logic

```
Player reaches screen edge
         ↓
Determine edge direction (UP/DOWN/LEFT/RIGHT)
         ↓
Check for special transitions (subway tunnel)
         ↓
If none, check zone neighbors
         ↓
If valid → Load new zone
         ↓
Reposition player at corresponding edge
         ↓
Update background and music
```

### Day Progression Flow

```
Player enters sleep zone (top-right 400x400px in Home)
         ↓
Press E key
         ↓
SleepInteractor validates (not already slept today)
         ↓
Restore health to 100
         ↓
Create DaySummary (earnings, spending, net)
         ↓
    Is Friday?
    /        \
  YES        NO
   ↓          ↓
Determine   Advance day
ending tier  Reset flags
   ↓          ↓
EndGameView  DaySummaryView
   ↓          ↓
Main Menu   Continue game (next day)
```

---

## Package Structure

```
src/main/java/
├── app/
│   └── Main.java                          # Entry point
├── entity/                                # Domain layer
│   ├── Player.java                        # Player entity
│   ├── Day.java                          # Day enum
│   ├── DaySummary.java                   # Daily summary
│   ├── GameEnding.java                   # Week endings
│   ├── Zone.java                         # Map zone
│   ├── GameMap.java                      # Complete map
│   ├── Transition.java                   # Special transitions
│   ├── Event.java                        # Random events
│   ├── EventOutcome.java                 # Event results
│   ├── NPC.java                          # NPCs
│   ├── Stock.java                        # Stock data
│   ├── Bill.java                         # Bills
│   └── Item.java                         # Items
├── use_case/                             # Application layer
│   ├── PlayerMovementUseCase.java        # Movement logic
│   ├── Direction.java                    # Direction enum
│   ├── sleep/                            # Sleep system
│   ├── paybills/                         # Bill payment
│   ├── events/                           # Random events
│   ├── npc_interactions/                 # NPC dialogue
│   ├── stock_game/                       # Stock trading
│   ├── save_progress/                    # Saving
│   └── load_progress/                    # Loading
├── interface_adapter/                    # Adapter layer
│   ├── ViewManagerModel.java             # View coordination
│   ├── ViewModel.java                    # Base view model
│   ├── events/                           # Event adapters
│   │   └── PlayerInputController.java    # Input handling
│   ├── sleep/                            # Sleep adapters
│   └── paybills/                         # Bill adapters
├── view/                                 # UI layer
│   ├── MainGameWindow.java               # Main JFrame
│   ├── GamePanel.java                    # Game view
│   ├── MainMenuPanel.java                # Main menu
│   ├── DaySummaryView.java               # Day summary
│   ├── EndGameView.java                  # Ending screen
│   ├── PaybillView.java                  # Bill payment UI
│   ├── SettingsPanel.java                # Settings
│   ├── InGameMenuPanel.java              # Pause menu
│   └── LoadingScreenPanel.java           # Loading screen
├── data_access/                          # Data persistence
│   ├── SaveFileUserDataObject.java       # Save files
│   ├── LoadFileUserDataAccessObject.java # Load files
│   ├── EventDataAccessObject.java        # Event data
│   ├── NPCDataAccessObject.java          # NPC data
│   └── ItemDataAccessObject.java         # Item data
└── api/                                  # External APIs
    ├── AlphaStockDataAccessObject.java   # Alpha Vantage
    └── StockDataBase.java                # API interface

src/main/resources/
├── events.json                           # Event definitions
├── items.json                            # Item definitions
├── npc_prompts.json                      # NPC dialogue prompts
├── saveFile.json                         # Player save data
├── audio/                                # Music and SFX
│   ├── lofi-lofi-song-2-434695.wav
│   ├── local-forecast-elevator.wav
│   └── siren_cropped.wav
└── backgrounds/                          # Zone images
    ├── home.png
    ├── street_1.png, street_2.png, street_3.png
    ├── subway_1.png, subway_2.png
    ├── office.png, office_lobby.png
    └── store.png

src/test/java/                            # Unit tests
├── AlphaStockDataBaseTest.java
├── StartStockGameUsecaseTest.java
├── Events/
├── Paybill/
├── Sleep/
└── use_case/
```

---

## How to Play

### Starting the Game

1. Launch the game (see [Quick Start](#quick-start))
2. Loading screen appears with progress bar
3. Main menu appears

### Main Menu Options

- **New Game**: Start a new game (Monday, $1000 balance)
- **Load Game**: Load saved progress (when implemented)
- **Settings**: Adjust resolution, volume, controls
- **Exit**: Quit the game

### Gameplay Loop

1. **Navigate the World**
   - Use WASD to move your character
   - Walk to zone edges to transition between areas
   - Explore Home, Streets, Subway, Office, Grocery Store

2. **Manage Your Day**
   - Current day displayed in HUD (Monday - Day 1/5)
   - Health bar shows 0-100 health
   - Balance shows current money

3. **Earn Money**
   - Go to Office to work (stock trading minigame)
   - Daily earnings tracked automatically

4. **Pay Bills**
   - Press ESC for pause menu
   - Select "Pay Bills"
   - Pay pending bills to avoid penalties

5. **Sleep to Progress**
   - Return to Home zone
   - Go to top-right corner (sleep zone)
   - Press E when prompted
   - View daily summary (earnings, spending, net change)
   - Press ENTER to advance to next day

6. **Complete the Week**
   - Survive Monday through Friday
   - Maximize your balance
   - Sleep on Friday to see ending

### Endings

Your final balance determines your ending:

| Balance | Ending | Description |
|---------|--------|-------------|
| ≥ $5000 | Wealthy Executive | "You've mastered the art of financial success!" |
| ≥ $2000 | Comfortable Professional | "You made it through with money to spare." |
| ≥ $1000 | Struggling Worker | "You survived, but barely." |
| < $1000 | The Defeated | "The city has defeated you... for now." |

### Tips

- Sleep only once per day (you'll be blocked if you try again)
- Sleeping restores health to 100
- Track your daily earnings/spending in the day summary
- Use the pause menu (ESC) to access bills and settings
- Explore all zones to discover features
- The subway connects distant parts of the city

---

## Testing

### Running Tests

```bash
mvn test
```

### Test Coverage

#### Unit Tests

- **Entity Layer**: Day enum, Player state, GameEnding logic
- **Use Case Layer**: Sleep validation, Event triggering, Bill payment
- **API Layer**: Alpha Vantage integration
- **Data Access**: Event/NPC/Item loading from JSON

#### Integration Tests

- Full sleep cycle: E key → summary → ENTER → new day
- Zone transitions with edge cases
- Week completion flow
- Financial tracking accuracy

#### Manual Testing Checklist

- [ ] Character movement in all directions
- [ ] Zone transitions work correctly
- [ ] Background images and music load
- [ ] Sleep zone detection and visual indicator
- [ ] Day advancement and summary screen
- [ ] Health restoration after sleep
- [ ] Can't sleep twice in same day
- [ ] Week ending shows correct tier
- [ ] Bill payment deducts from balance
- [ ] Window resizing maintains aspect ratio
- [ ] HUD displays correct information

### Test Files

Located in [`src/test/java/`](src/test/java/):
- [`AlphaStockDataBaseTest.java`](src/test/java/AlphaStockDataBaseTest.java)
- [`Events/StartRandomEventInteractorTest.java`](src/test/java/Events/StartRandomEventInteractorTest.java)
- [`Events/ActivateRandomOutcomeInteractorTest.java`](src/test/java/Events/ActivateRandomOutcomeInteractorTest.java)
- [`Paybill/PaybillInteractorTest.java`](src/test/java/Paybill/PaybillInteractorTest.java)
- [`Sleep/SleepInteractorTest.java`](src/test/java/Sleep/SleepInteractorTest.java)
- [`use_case/npc_interactions/NPCDAOTest.java`](src/test/java/use_case/npc_interactions/NPCDAOTest.java)

---

## Future Features

This section describes how planned features will be implemented using Clean Architecture.

### 🔮 Stock Trading UI Integration

**Current Status**: Chart visualization complete, full game integration pending
**Completed**:
- ✅ Stock price chart using XChart library
- ✅ Real-time price updates with 50-bar history
- ✅ Dynamic y-axis that zooms to price range
- ✅ Buy/sell buttons with all-in/all-out mechanics
- ✅ Portfolio display (cash, shares, total equity)

**Remaining Work**:

1. **Game Integration**: Connect to main game loop
   - Trigger when player enters Office zone
   - Press E to start trading session
   - Return to main game after session ends

2. **Controller**: `StockGameController.java`
   - Bridge between GamePanel and StockGameView
   - Handle game state transitions

**Files to Update**:
- `GamePanel.java` - Add Office interaction trigger
- Create `interface_adapter/stock_game/StockGameController.java`

### 🔮 NPC Dialogue System

**Current Status**: Backend complete, dialogue generation works  
**Implementation Plan**:

1. **View Layer**: Create `NpcDialogView.java` extending `JDialog`
   - Modal dialogue window
   - JTextArea for conversation history
   - JTextField for player input
   - Send button

2. **Controller**: Already exists (`NpcInteractionsController.java`)
   - Connect to view's send button
   - Pass player input to `NpcInteractionsInteractor`
   - Display AI-generated response

3. **Integration**:
   - Place NPC sprites in zones (render in `GamePanel`)
   - Detect proximity to NPC
   - Press E to open dialogue window
   - Use Google Gemini API for responses

**Files to Create**:
- `view/NpcDialogView.java`
- NPC sprite rendering in `GamePanel.java`
- NPC placement data in `GameMap.java`

### 🔮 Save/Load UI Integration

**Current Status**: Backend complete, frontend stubbed  
**Implementation Plan**:

1. **Save System**:
   - Already autosaves on sleep (implemented)
   - Manual save from pause menu calls `SaveProgressInteractor`
   - Serialize `Player`, `GameMap` state, current day to JSON
   - Store in `saveFile.json`

2. **Load System**:
   - Load button reads `saveFile.json`
   - Deserialize into entities
   - Restore player position, balance, day, inventory
   - Restore zone state
   - Resume game at saved position

3. **UI Updates**:
   - Replace placeholder dialogs with actual save/load
   - Show save slots (multiple saves)
   - Display save metadata (day, balance, timestamp)

**Files to Update**:
- `MainGameWindow.java` - Remove stub dialogs
- `MainMenuPanel.java` - Enable load button
- `SaveFileUserDataObject.java` - Multiple save slots

### 🔮 Inventory System UI

**Current Status**: Backend exists, no UI  
**Implementation Plan**:

1. **View Layer**: Create `InventoryPanel.java`
   - 5-slot grid display
   - Item icons and names
   - Use/Drop buttons
   - Item descriptions on hover

2. **Controller**: `InventoryController.java`
   - Handle item use (calls `Player.itemUsed()`)
   - Handle item drop (calls `Player.removeInventory()`)
   - Update view when inventory changes

3. **Item Pickups**:
   - Render items in zones (sprite on ground)
   - Collision detection with player
   - Press E to pick up
   - Add to inventory via `Player.addInventory()`

4. **Integration**:
   - Access from pause menu or I key
   - Show in overlay on game screen
   - Items from `items.json` loaded by `ItemDataAccessObject`

**Files to Create**:
- `view/InventoryPanel.java`
- `interface_adapter/inventory/InventoryController.java`
- Update `GamePanel.java` for item rendering

### 🔮 Enhanced Random Events

**Current Status**: Event triggering works, needs richer UI
**Implementation Plan**:

1. **Event Dialog Enhancement**:
   - Create `EventDialog.java` with better visuals
   - Show event description with formatting
   - Display multiple choice buttons for outcomes
   - Show financial impact preview

2. **Event Types**:
   - **Positive Events**: Unexpected income, bonuses
   - **Negative Events**: Fines, accidents, repairs
   - **Choice Events**: Risk/reward scenarios
   - **Story Events**: Character development

3. **Integration**:
   - Trigger from `StartRandomEventInteractor` (already implemented)
   - Display modal dialog with choices
   - Apply outcome via `ActivateRandomOutcomeInteractor`
   - Update player stats and balance

**Files to Create**:
- `view/EventDialog.java`
- Expand `events.json` with more events

### 🔮 Weekend Days

**Current Status**: Game ends after Friday
**Implementation Plan**:

1. **Entity Updates**:
   - Extend `Day` enum: Add SATURDAY, SUNDAY
   - Modify week length from 5 to 7 days
   - Different endings for 1-week, 2-week, 1-month modes

2. **Weekend Activities**:
   - No work requirement
   - Optional activities (shopping, socializing)
   - Different random events
   - Rest and recovery bonuses

3. **Use Case Changes**:
   - `SleepInteractor` checks for Sunday (week end)
   - Weekend-specific event pool
   - Relationship building with NPCs

**Files to Modify**:
- `entity/Day.java` - Add weekend days
- `use_case/sleep/SleepInteractor.java` - Week ending logic
- `data_access/EventDataAccessObject.java` - Weekend events

### 🔮 Stats and Mood System

**Current Status**: Player has stats (Hunger, Energy, Mood) but not actively used
**Implementation Plan**:

1. **Stat Decay**:
   - Hunger decreases over time
   - Energy decreases with activity
   - Mood affected by events and choices

2. **Stat Effects**:
   - Low hunger: Reduced movement speed
   - Low energy: Can't work effectively
   - Low mood: More negative events

3. **Stat Management**:
   - Eat at Grocery Store (restores hunger)
   - Sleep restores energy
   - NPC interactions improve mood
   - Items can boost stats

4. **UI Display**:
   - Stat bars in HUD
   - Color-coded (green/yellow/red)
   - Warning messages when critical

**Files to Modify**:
- `entity/Player.java` - Stat decay methods
- `GamePanel.java` - Draw stat bars
- `use_case/PlayerMovementUseCase.java` - Speed reduction
- `view/StockGameView.java` - Effectiveness modifiers

### 🔮 Achievements System

**Current Status**: Not implemented
**Implementation Plan**:

1. **Achievement Types**:
   - **Financial**: Earn $X, Save $Y
   - **Social**: Meet all NPCs, Max relationship
   - **Exploration**: Visit all zones
   - **Survival**: Complete week without sleeping
   - **Trading**: Make profitable trades

2. **Achievement Tracking**:
   - Create `Achievement` entity
   - Track progress in `Player`
   - Save with game state
   - Display notification on unlock

3. **Rewards**:
   - Unlock special items
   - Starting bonus for new games
   - Cosmetic player sprites
   - Secret endings

**Files to Create**:
- `entity/Achievement.java`
- `use_case/achievements/AchievementTracker.java`
- `view/AchievementsPanel.java`
- Update `Player.java` with achievement list

### 🔮 Difficulty Modes

**Current Status**: Single difficulty
**Implementation Plan**:

1. **Mode Selection** (on New Game):
   - **Easy**: Higher starting balance ($2000), lower bills
   - **Normal**: Current settings ($1000, standard bills)
   - **Hard**: Lower balance ($500), higher bills, more negative events
   - **Impossible**: Extreme challenge mode

2. **Difficulty Modifiers**:
   - Bill amounts scaled by difficulty
   - Event outcome severity
   - Stock market volatility
   - NPC relationship thresholds

3. **Implementation**:
   - Add difficulty to `GameSettings`
   - Modifier multipliers in use cases
   - Show difficulty in save file
   - Separate leaderboards per difficulty

**Files to Modify**:
- `entity/GameSettings.java` - Add difficulty enum
- `MainMenuPanel.java` - Difficulty selection
- All use cases - Apply multipliers
- `SaveFileUserDataObject.java` - Store difficulty

### 🔮 Leaderboard and Statistics

**Current Status**: Not implemented
**Implementation Plan**:

1. **Statistics Tracking**:
   - Total games played
   - Best ending achieved
   - Total money earned
   - Days survived
   - Most profitable trade
   - Bills paid on time

2. **Leaderboard**:
   - Local leaderboard (top 10 runs)
   - Sort by final balance, days survived
   - Show difficulty mode
   - Timestamp of completion

3. **Stats View**:
   - Accessible from main menu
   - Lifetime statistics
   - Per-game statistics
   - Charts and graphs

**Files to Create**:
- `entity/GameStatistics.java`
- `data_access/StatisticsDataAccessObject.java`
- `view/LeaderboardPanel.java`
- `view/StatisticsPanel.java`

### 🔮 Audio System Enhancements

**Current Status**: Basic music per zone
**Implementation Plan**:

1. **Audio Features**:
   - Sound effects (walking, interaction, cash)
   - Ambient sounds per zone (traffic, office chatter)
   - Music crossfade between zones
   - Volume controls per category

2. **Audio Manager**:
   - `AudioManager` singleton
   - Background music queue
   - SFX mixing
   - Persistent volume settings

3. **Implementation**:
   - Java AudioSystem (javax.sound)
   - WAV format for compatibility
   - Streaming for music, loaded for SFX
   - Settings panel sliders

**Files to Create**:
- `audio/AudioManager.java`
- `audio/SoundEffect.java`
- Add more audio files to `resources/audio/`
- Update `SettingsPanel.java` with volume controls

### 🔮 Tutorial System

**Current Status**: No tutorial
**Implementation Plan**:

1. **First-Time Experience**:
   - Welcome screen explaining premise
   - Interactive tutorial on first play
   - Highlight controls and features
   - Practice movement and interactions

2. **Contextual Hints**:
   - Show hints when entering new zones
   - Tooltips for UI elements
   - Help button in pause menu
   - Keyboard shortcut reference

3. **Implementation**:
   - Check `GameSettings.hasCompletedTutorial`
   - Tutorial mode with scripted events
   - Overlay instructions
   - Skip button for experienced players

**Files to Create**:
- `view/TutorialOverlay.java`
- `use_case/tutorial/TutorialManager.java`
- Update `GameSettings.java` with tutorial flag

---

## Technical Specifications

### System Requirements

**Minimum**:
- Java 11+
- 2 GB RAM
- 500 MB disk space
- Integrated graphics
- 640x400 display

**Recommended**:
- Java 17+ (for better Wayland support)
- 4 GB RAM
- 1 GB disk space
- Dedicated graphics
- 1920x1200 or higher display

### Compatibility

- **OS**: Windows 7+, macOS 10.12+, Linux (X11 and Wayland)
- **Display**: Supports any aspect ratio (16:10 optimal)
- **Input**: Keyboard required
- **Network**: Required for stock data and NPC dialogue (offline mode planned)

### Performance

- **Target Frame Rate**: 60 FPS
- **Memory Usage**: ~200 MB runtime
- **Startup Time**: 2-3 seconds
- **Save File Size**: <1 MB

---

## Team Members

**CSC207 LEC0201 Group 14 - TUT0401-14**

| Member | GitHub | Responsibilities |
|--------|--------|-----------------|
| **Albert Guo** | - | Environment interaction, movement system, map transitions, scaling implementation |
| **Cynthia Rong** | - | Stock market minigame, Alpha Vantage API integration |
| **Joshua Iaboni** | - | Random events system, event outcomes |
| **Jayden Luo** | - | NPC interactions, Google Gemini integration |
| **Anjani Sjariffudin** | - | Save/load system, data persistence |
| **Sherry Yuehua Wang** | - | Day progression, sleep system, bill payment |

---

## Team Contract

As outlined in our [`TeamContract.md`](TeamContract.md), we commit to:

- **Communication**: Respond within 24 hours on Discord
- **Accountability**: Meet agreed deadlines or notify team promptly
- **Collaboration**: Equal participation and respect for all members
- **Quality**: Maintain Clean Architecture principles
- **Decision Making**: Democratic voting (simple majority)
- **Conflict Resolution**: Professional and constructive approach

---

## Project Timeline

### Phase 1: Core Systems (Completed)
- ✅ Project setup and architecture design
- ✅ Character movement and rendering
- ✅ Zone system with transitions
- ✅ Day progression with sleep
- ✅ Menu system and UI framework

### Phase 2: Game Features (In Progress)
- ✅ Bill payment system
- ✅ Random events backend
- ⚠️ Stock trading (backend complete, UI pending)
- ⚠️ NPC dialogue (backend complete, UI pending)
- ⚠️ Save/load (backend complete, frontend stubbed)

### Phase 3: Polish and Integration (Planned)
- 🔮 Complete all feature UIs
- 🔮 Comprehensive testing
- 🔮 Audio enhancements
- 🔮 Tutorial system
- 🔮 Performance optimization

### Phase 4: Future Enhancements (Post-Release)
- 🔮 Weekend days
- 🔮 Achievements
- 🔮 Difficulty modes
- 🔮 Leaderboards
- 🔮 Additional zones and NPCs

---

## Contributing

This is a course project for CSC207 at University of Toronto. Contributions are limited to team members.

### Development Guidelines

1. **Follow Clean Architecture**: Maintain layer boundaries
2. **Write Tests**: Unit tests for use cases, integration tests for workflows
3. **Document Code**: JavaDoc for public methods
4. **Code Style**: Follow Java conventions
5. **Git Workflow**: Feature branches, pull requests, code review

### Code Review Checklist

- [ ] Follows Clean Architecture principles
- [ ] No inner layer depends on outer layers
- [ ] All public methods documented
- [ ] Unit tests included
- [ ] Integration tests pass
- [ ] No hardcoded values (use constants)
- [ ] Error handling implemented
- [ ] Performance acceptable

---

## Known Issues

1. **Stock Game UI**: Backend complete but not integrated with main game
2. **NPC Dialogue**: AI responses work but no dialogue window yet
3. **Save/Load**: Frontend shows placeholder messages
4. **Special Transitions**: Subway tunnel infrastructure exists but not activated
5. **Event UI**: Events trigger but need better visual presentation

---

## Credits

### APIs and Libraries

- **Alpha Vantage**: Stock market data ([alphavantage.co](https://www.alphavantage.co/))
- **Google Gemini**: AI-generated NPC dialogue ([ai.google.dev](https://ai.google.dev/))
- **XChart**: Lightweight charting library for stock price visualization ([knowm.org/open-source/xchart](https://knowm.org/open-source/xchart/))
- **Java Swing**: GUI framework (built into Java)
- **Maven**: Build automation ([maven.apache.org](https://maven.apache.org/))
- **Gson**: JSON parsing (used for data files)

### Assets

- **Music**:
  - "Lofi Lofi Song 2" by Lofi-Lofi
  - "Local Forecast - Elevator" by Kevin MacLeod
- **Background Art**: Custom created for this project
- **Sound Effects**: Various open-source SFX

### Inspiration

This game draws inspiration from:
- Life simulation games (The Sims, Animal Crossing)
- Economic management games (Papers Please, Cart Life)
- Social commentary in games (Undertale, Night in the Woods)

---

## License

This project is created for educational purposes as part of CSC207 at University of Toronto.

**Course**: CSC207 - Software Design
**Term**: Fall 2024
**Instructor**: TBD
**Institution**: University of Toronto

---

## Contact

For questions about this project, contact team members through the course Discord or GitHub.

**Last Updated**: November 23, 2024
**Version**: 1.0.0
**Status**: In Development

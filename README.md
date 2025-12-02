
# Game Documentation

**Team:** TUT0401-14 (LEC0201 Group 14)  
**Domain:** Financial Sims Knock-Off
---

## Project Overview

Financial Sims Knock-Off is a simulation game where players attempt to survive in an increasingly demanding captialistic world. They must work a pointless 9-5 job that provides a meager and unstable source of income at best, all while needing to pay countless bills and manage unexpected events. The goal is to try to survive from Monday to Friday, and maybe have some fun and learn about stocks along the way! On a deeper level, our game provides satirical commentary on the modern day, and serves as a humourous outlet to release frustrations about financial pressures of today's world. 

### Key Features

- **2D Environment Navigation**: Move through multiple interconnected zones (home, streets, subway, office)
- **Daily Tasks**: Pay your growing stack of bills, and save the game by sleeping to end each day. 
- **Stock Trading Minigame**: Invest your money, buy low and sell high during a live mini-game to earn a profit. 
- **Random Events**: Test your chances when faced with unpredictable life events. 
- **NPC Interactions**: Talk to characters with AI-generated dialogue that you find along the way.
- **Save/Load System**: Maintain your progress by saving and loading the game.

### Technology Stack

- **Language**: Java 16+
- **GUI Framework**: Java Swing
- **Build Tool**: Maven
- **APIs**: Alpha Vantage (stock data), Google Gemini (NPC dialogue)
- **Charting Library**: XChart (stock price visualization)
- **Data Format**: JSON (for saves, events, items, NPC prompts)
- **Audio**: Java AudioSystem (WAV format)

---

## Quick Start

### Prerequisites

- Java 16 or higher
- Maven (recommended) or `javac`

### Running the Game

1. Open project in IntelliJ IDEA
2. Build → Build Project (Ctrl+F9 / Cmd+F9)
3. Right-click on [`Main.java`](src/main/java/app/Main.java) → Run 'Main.main()'

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

- Google Gemini API for dynamic dialogue via guided prompting
- NPC prompts and names from [`npc_prompts.json`](src/main/resources/npc_prompts.json)
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

### Test Files

Located in [`src/test/java/`](src/test/java/):
- [`AlphaStockDataBaseTest.java`](src/test/java/AlphaStockDataBaseTest.java)
- [`Events/StartRandomEventInteractorTest.java`](src/test/java/Events/StartRandomEventInteractorTest.java)
- [`Events/ActivateRandomOutcomeInteractorTest.java`](src/test/java/Events/ActivateRandomOutcomeInteractorTest.java)
- [`Paybill/PaybillInteractorTest.java`](src/test/java/Paybill/PaybillInteractorTest.java)
- [`Sleep/SleepInteractorTest.java`](src/test/java/Sleep/SleepInteractorTest.java)
- [`use_case/npc_interactions/NPCDAOTest.java`](src/test/java/use_case/npc_interactions/NPCDAOTest.java)
- and more
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

This is a course project for CSC207 at University of Toronto. Contributions are limited to team members.


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

## License

This project is created for educational purposes as part of CSC207 at University of Toronto.

**Course**: CSC207 - Software Design
**Term**: Fall 2024
**Institution**: University of Toronto

---

## Contact

For questions about this project, contact team members through the course Discord or GitHub.

**Last Updated**: December 1, 2025
**Version**: 1.0.0
**Status**: In Development

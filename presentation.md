# California Prop. 65 - Presentation Plan

**Team:** TUT0401-14 (CSC207 LEC0201 Group 14)  
**Project:** California Prop. 65 - Life Simulation Game  
**Presentation Date:** [Week of Presentation]

---

## Slide-by-Slide Outline

### Slide 1: Title & Introduction (1 minute)
**Presenter:** Albert Guo

- Project name: "California Prop. 65"
- Team members and tutorial section
- Brief tagline: "A satirical 2D life simulation game about surviving the financial pressures of modern life"
- Overview of presentation structure

---

### Slide 2: Project Overview & Motivation (2 minutes)
**Presenter:** Sherry Yuehua Wang

- **Problem Statement:** Managing work-life balance and financial stability in a challenging economy
- **Game Concept:** 
  - Navigate a 2D world as a working professional
  - Survive Monday through Friday managing money, health, and unexpected events
  - Satirical commentary on modern capitalism
- **Target Audience:** Casual gamers interested in life simulation and management games
- **Key Objective:** Build wealth while managing daily expenses and random life events

---

### Slide 3: Domain & Technology Stack (1.5 minutes)
**Presenter:** Albert Guo

- **Domain:** Life Simulation & Stock Market Trading
- **Technology Stack:**
  - Java 11+ with Swing for GUI
  - Maven for build automation
  - Alpha Vantage API for real stock data
  - Google Gemini API for NPC dialogue
  - JSON for data persistence
- **Architecture:** Clean Architecture (4-layer design)
- **Development Tools:** IntelliJ IDEA, Git/GitHub, JUnit for testing

---

### Slide 4: Clean Architecture Overview (2 minutes)
**Presenter:** Anjani Sjariffudin

- **Four Layers Explained:**
  1. **Entity Layer:** Pure domain objects (Player, Day, Event, Stock, etc.)
  2. **Use Case Layer:** Business logic and game rules
  3. **Interface Adapter Layer:** Controllers, Presenters, ViewModels
  4. **Infrastructure Layer:** Views (Swing), APIs, Data Access
- **Dependency Rule:** All dependencies point inward (outer layers depend on inner)
- **Benefits:** 
  - Testability (can test use cases without UI)
  - Maintainability (clear separation of concerns)
  - Flexibility (can swap UI framework or data sources)
- Architecture diagram showing layer relationships

---

### Slide 5: Use Case 1 - Character Movement & Environment Interaction (2 minutes)
**Presenter:** Albert Guo

- **Feature Description:**
  - 2D character movement with WASD controls
  - Real-time game loop (~60 FPS with javax.swing.Timer)
  - Virtual 1920x1200 coordinate system with automatic scaling
  - Collision detection and boundary checking
- **Implementation Highlights:**
  - [`PlayerMovementUseCase.java`](src/main/java/use_case/PlayerMovementUseCase.java) - Core movement logic
  - [`GamePanel.java`](src/main/java/view/GamePanel.java) - Rendering and game loop
  - [`PlayerInputController.java`](src/main/java/interface_adapter/events/PlayerInputController.java) - Input handling
- **Technical Challenges:**
  - Maintaining consistent speed across different frame rates
  - Implementing smooth diagonal movement
  - Scaling coordinates for different screen sizes
- **Design Patterns:** Observer pattern for key input, Strategy pattern for movement direction

---

### Slide 6: Use Case 2 - Random Events System (2 minutes)
**Presenter:** Joshua Iaboni

- **Feature Description:**
  - Dynamic random events that impact player finances and stats
  - Multiple outcomes per event with different consequences
  - Pity system (25% increase per non-trigger)
  - Events loaded from JSON configuration
- **Implementation Highlights:**
  - [`StartRandomEventInteractor.java`](src/main/java/use_case/events/StartRandomEvent/StartRandomEventInteractor.java) - Event triggering logic
  - [`ActivateRandomOutcomeInteractor.java`](src/main/java/use_case/events/ActivateRandomOutcome/ActivateRandomOutcomeInteractor.java) - Outcome processing
  - [`Event.java`](src/main/java/entity/Event.java) & [`EventOutcome.java`](src/main/java/entity/EventOutcome.java) - Domain entities
  - [`EventDataAccessObject.java`](src/main/java/data_access/EventDataAccessObject.java) - JSON data loading
- **Event Types:**
  - Financial impacts (bills, bonuses, fines)
  - Health effects
  - Choice-based scenarios
- **Design Decisions:** Probability-based triggering, data-driven event configuration

---

### Slide 7: Use Case 3 - NPC Interactions (2 minutes)
**Presenter:** Jayden Luo

- **Feature Description:**
  - AI-powered NPC dialogue using Google Gemini API
  - Context-aware conversations based on player state
  - Relationship tracking with NPCs
  - Dynamic responses to player input
- **Implementation Highlights:**
  - [`NpcInteractionsInteractor.java`](src/main/java/use_case/npc_interactions/NpcInteractionsInteractor.java) - Dialogue logic
  - [`NPC.java`](src/main/java/entity/NPC.java) - NPC entity with prompts
  - [`NPCDataAccessObject.java`](src/main/java/data_access/NPCDataAccessObject.java) - NPC data management
  - Google Gemini API integration for natural language generation
- **Current Status:** Backend complete, UI integration in progress
- **Technical Challenges:**
  - Managing API rate limits
  - Maintaining conversation context
  - Generating appropriate responses for game scenarios

---

### Slide 8: Use Case 4 - Save/Load Progress (1.5 minutes)
**Presenter:** Anjani Sjariffudin

- **Feature Description:**
  - Complete game state serialization to JSON
  - Autosave on sleep
  - Manual save/load from pause menu
  - Preserves player position, balance, day, inventory, relationships
- **Implementation Highlights:**
  - [`SaveProgressInteractor.java`](src/main/java/use_case/save_progress/SaveProgressInteractor.java) - Save logic
  - [`LoadProgressInteractor.java`](src/main/java/use_case/load_progress/LoadProgressInteractor.java) - Load logic
  - [`SaveFileUserDataObject.java`](src/main/java/data_access/SaveFileUserDataObject.java) - File management
  - [`saveFile.json`](src/main/resources/test_JSON_files/saveFile.json) - Save file format
- **Current Status:** Backend complete, frontend UI stubbed
- **Data Integrity:** Validation on load, backup saves, error handling

---

### Slide 9: Use Case 5 - Day Progression & Bill Payment (2 minutes)
**Presenter:** Sherry Yuehua Wang

- **Feature Description - Sleep System:**
  - Monday through Friday work week
  - Sleep to advance to next day
  - Health restoration (to 100) after sleeping
  - Daily financial tracking (earnings and spending)
  - One sleep per day restriction
  - Day summary screen with transitions
- **Feature Description - Bill Payment:**
  - Pay bills through in-game menu
  - Track bill types and amounts
  - Deduct from player balance
  - Integration with daily spending
- **Implementation Highlights:**
  - [`SleepInteractor.java`](src/main/java/use_case/sleep/SleepInteractor.java) - Sleep logic and day advancement
  - [`PaybillInteractor.java`](src/main/java/use_case/paybills/PaybillInteractor.java) - Bill payment logic
  - [`Day.java`](src/main/java/entity/Day.java), [`DaySummary.java`](src/main/java/entity/DaySummary.java), [`GameEnding.java`](src/main/java/entity/GameEnding.java) - Domain entities
  - [`DaySummaryView.java`](src/main/java/view/DaySummaryView.java), [`EndGameView.java`](src/main/java/view/EndGameView.java) - UI views
- **Game Endings:** Based on final balance (Wealthy ≥$5000, Comfortable ≥$2000, Struggling ≥$1000, Defeated <$1000)

---

### Slide 10: Use Case 6 - Stock Trading Minigame (2 minutes)
**Presenter:** Cynthia Rong

- **Feature Description:**
  - Real-time stock trading simulation
  - 30-second trading sessions
  - Buy/sell mechanics with portfolio tracking
  - Real stock data from Alpha Vantage API
  - Price history visualization
- **Implementation Highlights:**
  - [`AlphaStockDataAccessObject.java`](src/main/java/api/AlphaStockDataAccessObject.java) - API integration
  - [`Stock.java`](src/main/java/entity/Stock.java) & [`Portfolio.java`](src/main/java/entity/Portfolio.java) - Domain entities
  - Stock game use cases: Start, Play, Buy, End
  - [`StockGameView.java`](src/main/java/view/StockGameView.java) - Trading interface
- **Current Status:** Backend complete, UI integration in progress
- **API Integration:** REST API calls, JSON parsing, data caching for performance
- **Game Balance:** Risk/reward mechanics, price volatility

---

### Slide 11: Additional Systems & Features (1.5 minutes)
**Presenter:** Albert Guo

- **Zone Transition System:**
  - 9 interconnected zones (Home, Streets, Subway, Office, Grocery)
  - Background images and zone-specific music
  - Edge-based transitions between zones
  - Special subway tunnel system
- **Display Scaling System:**
  - Virtual 1920x1200 resolution with automatic scaling
  - Letterbox/pillarbox for non-16:10 windows
  - HiDPI/Retina display support
  - Maintains 16:10 aspect ratio
- **Menu System:**
  - Main menu (New Game, Load, Settings, Exit)
  - In-game pause menu (Resume, Save, Settings, Pay Bills)
  - Settings panel (resolution, volume, controls)
  - CardLayout for view management

---

### Slide 12: Testing Strategy (1.5 minutes)
**Presenter:** Joshua Iaboni

- **Unit Tests:**
  - Entity layer: Player state, Day enum, GameEnding logic
  - Use case layer: Sleep validation, Event triggering, Bill payment, Stock trading
  - API layer: Alpha Vantage integration
  - Data access: JSON loading and parsing
- **Test Coverage:**
  - [`SleepInteractorTest.java`](src/test/java/Sleep/SleepInteractorTest.java)
  - [`StartRandomEventInteractorTest.java`](src/test/java/Events/StartRandomEventInteractorTest.java)
  - [`PaybillInteractorTest.java`](src/test/java/Paybill/PaybillInteractorTest.java)
  - [`AlphaStockDataBaseTest.java`](src/test/java/AlphaStockDataBaseTest.java)
- **Testing Tools:** JUnit for automated testing, Maven for test execution
- **Integration Testing:** Full gameplay cycles, zone transitions, save/load operations

---

### Slide 13: Design Patterns & Best Practices (1.5 minutes)
**Presenter:** Anjani Sjariffudin

- **Design Patterns Used:**
  - **Observer Pattern:** ViewModels notify views of state changes
  - **Strategy Pattern:** Different movement directions, event outcomes
  - **Factory Pattern:** Creating entities from JSON data
  - **Singleton Pattern:** ViewManagerModel for view coordination
  - **Command Pattern:** Controller actions
- **SOLID Principles:**
  - Single Responsibility: Each class has one clear purpose
  - Open/Closed: Extensible through interfaces
  - Liskov Substitution: Proper inheritance hierarchies
  - Interface Segregation: Focused interfaces for use cases
  - Dependency Inversion: Use cases depend on interfaces, not implementations
- **Code Quality:** Consistent naming conventions, comprehensive JavaDoc, Git workflow with pull requests

---

### Slide 14: Challenges & Solutions (2 minutes)
**Presenter:** Jayden Luo

- **Technical Challenges:**
  1. **Coordinate System Scaling:** Virtual resolution vs actual screen size
     - Solution: Transform-based scaling in Graphics2D
  2. **API Integration:** Rate limits and network reliability
     - Solution: Caching, error handling, offline mode planning
  3. **Day Progression Logic:** Complex state management across days
     - Solution: Clear separation of concerns in SleepInteractor
  4. **Clean Architecture Learning Curve:** Understanding layer boundaries
     - Solution: Team discussions, code reviews, refactoring iterations
- **Team Collaboration:**
  - Using Git for version control
  - Regular standups and code reviews
  - Pair programming for complex features
  - Documentation in README and JavaDoc

---

### Slide 15: Future Enhancements (1 minute)
**Presenter:** Cynthia Rong

- **Planned Features:**
  - Complete UI integration for stock trading and NPC dialogue
  - Weekend days (Saturday/Sunday) with unique activities
  - Enhanced inventory system with item pickups
  - Stats and mood system affecting gameplay
  - Achievements and leaderboards
  - Difficulty modes (Easy, Normal, Hard, Impossible)
  - Tutorial system for new players
  - Audio enhancements (sound effects, ambient sounds)
- **Scalability:** Modular architecture allows easy addition of new features

---

### Slide 16: Lessons Learned (1.5 minutes)
**Presenter:** Sherry Yuehua Wang

- **Technical Learnings:**
  - Clean Architecture provides excellent separation of concerns
  - Importance of interfaces for testability
  - Real-time game loop design in Swing
  - API integration best practices
- **Team Learnings:**
  - Effective use of Git branches and pull requests
  - Code review improves code quality
  - Regular communication prevents integration issues
  - Documentation saves time in the long run
- **What We'd Do Differently:**
  - Start with UI mockups earlier
  - More frequent integration testing
  - Earlier API testing to identify limitations
  - Better task estimation and sprint planning

---

### Slide 17: Conclusion & Demo Transition (0.5 minutes)
**Presenter:** Albert Guo

- **Summary of Achievements:**
  - Fully functional 2D life simulation game
  - Six major use cases implemented
  - Clean Architecture with 4 distinct layers
  - Comprehensive testing suite
  - External API integrations (Alpha Vantage, Google Gemini)
- **Transition to Demo:** "Now let's see the game in action!"

---

## Demo Plan

### Demo Overview
**Duration:** 5-7 minutes  
**Format:** Live demonstration with pre-recorded backup  
**Primary Demo Runner:** Albert Guo (gameplay) + Cynthia Rong (narration)  
**Backup Demo Runner:** Sherry Yuehua Wang

### Demo Flow

#### 1. Startup & Main Menu (30 seconds)
**Narrator:** Albert Guo  
**Runner:** Albert Guo

- Launch the application
- Show loading screen with progress bar
- Display main menu
- **Highlight:** Clean UI, resolution settings, menu options
- **Narration:** "Here's the main menu where players can start a new game, load saved progress, or adjust settings."

---

#### 2. New Game & Initial Setup (30 seconds)
**Narrator:** Sherry Yuehua Wang  
**Runner:** Albert Guo

- Click "New Game"
- Show initial game state (Monday, Day 1/5, $1000 balance, 100 health)
- Display HUD elements (day, health, balance, position)
- Player spawns in Home zone
- **Highlight:** HUD display, starting conditions
- **Narration:** "The game starts on Monday with $1000. The goal is to survive the work week and build wealth."

---

#### 3. Character Movement & Zone Navigation (45 seconds)
**Narrator:** Albert Guo  
**Runner:** Albert Guo

- **Demonstrate WASD movement** in Home zone
- Show smooth diagonal movement
- **Walk to zone edge** to trigger transition
- **Transition to Street zone** (show background change and music transition)
- **Navigate through multiple zones:** Home → Street → Office → Street → Subway
- **Return to Home zone** for next feature
- **Highlight:** Smooth movement, zone transitions, background images, position tracking in HUD
- **Narration:** "Players navigate a 2D world using WASD keys. Moving to zone edges triggers transitions. Each zone has unique backgrounds and music."

---

#### 4. Day Progression & Sleep System (1 minute)
**Narrator:** Sherry Yuehua Wang  
**Runner:** Albert Guo

- Move to **sleep zone** (top-right corner of Home)
- Show **sleep prompt** appearing
- **Press E** to sleep
- Display **Day Summary screen** (earnings: $0, spending: $0, net: $0)
- **Press ENTER** to continue
- Show **day advanced to Tuesday** (Day 2/5)
- Show **health restored to 100**
- **Attempt to sleep again** (show blocked message: "Already slept today")
- **Highlight:** Sleep zone detection, day progression, health restoration, one-sleep-per-day restriction
- **Narration:** "At the end of each day, players return home to sleep. Sleeping advances the day, restores health, and shows a financial summary. You can only sleep once per day."

---

#### 5. Random Events System (45 seconds)
**Narrator:** Joshua Iaboni  
**Runner:** Albert Guo (with pre-recorded event if needed)

- **Trigger a random event** (either live or use pre-recorded clip)
- Show **event dialog** with description
- Display **multiple outcome choices**
- **Select an outcome**
- Show **financial impact** (balance change, stat changes)
- Return to gameplay
- **Highlight:** Event variety, choice system, consequences
- **Narration:** "Random events add unpredictability. Players face unexpected situations like bills, opportunities, or accidents. Each choice has financial and gameplay consequences."

---

#### 6. Bill Payment System (30 seconds)
**Narrator:** Sherry Yuehua Wang  
**Runner:** Albert Guo

- **Press ESC** to open pause menu
- Select **"Pay Bills"**
- Show **bill payment interface**
- Display **pending bills** with amounts
- **Pay a bill** (show balance deduction)
- **Close bill interface** and return to game
- **Highlight:** In-game financial management, pause menu integration
- **Narration:** "Players manage expenses through the bill payment system. Bills must be paid to avoid penalties, directly affecting the player's balance."

---

#### 7. Stock Trading Minigame (1 minute)
**Narrator:** Cynthia Rong  
**Runner:** Cynthia Rong

- **Navigate to Office zone**
- **Interact with stock trading terminal** (E key or collision)
- Show **stock trading interface**:
  - Real stock price from Alpha Vantage API
  - Buy/Sell buttons
  - Portfolio display
  - 30-second timer
- **Execute a trade** (buy some shares)
- Show **portfolio update**
- **Wait for timer** or execute another trade
- **End trading session**
- Show **profit/loss calculation**
- **Balance update** in HUD
- **Highlight:** Real-time stock data, trading mechanics, profit/loss tracking
- **Narration:** "The stock trading minigame lets players earn money through strategic trading. It uses real stock data from Alpha Vantage API. Players have 30 seconds to buy and sell stocks."

---

#### 8. NPC Interactions (45 seconds)
**Narrator:** Jayden Luo  
**Runner:** Albert Guo

- **Approach an NPC** in a zone (if UI integrated) OR **show backend demo**
- **Press E** to initiate conversation
- Show **dialogue window** with NPC response
- **Type player message**
- Show **AI-generated response** from Google Gemini
- **Continue conversation** (1-2 exchanges)
- **Exit dialogue**
- **Highlight:** AI-powered conversations, context awareness
- **Narration:** "NPCs provide dynamic dialogue powered by Google Gemini AI. Conversations are contextual and can affect relationships and game outcomes. This adds depth to the social simulation aspect."
- **Fallback:** If UI not ready, show code and API response in terminal

---

#### 9. Save/Load System (30 seconds)
**Narrator:** Anjani Sjariffudin  
**Runner:** Albert Guo

- **Press ESC** for pause menu
- Select **"Save Game"**
- Show **save confirmation** (or file being written if visible)
- **Exit to main menu** (or continue)
- **Load the saved game** from main menu
- Show **restored game state** (same position, day, balance)
- **Highlight:** Complete state persistence, seamless save/load
- **Narration:** "The save/load system preserves complete game state to JSON. Players can save at any time and resume exactly where they left off."
- **Fallback:** If UI stubbed, show JSON save file and explain backend

---

#### 10. Week Completion & Endings (45 seconds)
**Narrator:** Sherry Yuehua Wang  
**Runner:** Albert Guo (with pre-recorded clip if needed)

- **Fast-forward** to Friday (Day 5/5)
- Show **accumulated balance** (ideally ≥$2000 for good ending)
- **Sleep on Friday**
- Display **final day summary**
- Show **Week Ending Screen** with:
  - Ending title (e.g., "The Comfortable Professional")
  - Ending description
  - Final statistics
- **Return to main menu**
- **Highlight:** Multiple endings based on performance, complete gameplay loop
- **Narration:** "After Friday, players see their final outcome. There are four possible endings based on financial performance, from 'Wealthy Executive' to 'The Defeated.' This completes one full week of gameplay."

---

### Demo Contingency Plans

#### If Technical Issues Occur:
1. **Backup Recording:** Pre-recorded 5-minute demo video showing all features
2. **Code Walkthrough:** Show key source files and explain implementation
3. **Screenshot Deck:** Prepared slides with gameplay screenshots and descriptions

#### If Features Not Working:
- **Stock Trading UI:** Show backend code + API responses in terminal + mock UI wireframe
- **NPC Dialogue UI:** Demonstrate API calls in terminal + show JSON prompts
- **Save/Load UI:** Show JSON save file and explain data structure

#### If Time Runs Over:
- **Skip or abbreviate:** Random events demo, bill payment demo
- **Combine:** Movement and zone navigation into one shorter segment
- **Fast-forward:** Show week ending from Monday to Friday in 30 seconds

---

### Demo Assignments

| Feature | Primary Demo Runner | Narrator | Backup |
|---------|-------------------|----------|---------|
| Startup & Menu | Albert | Albert | Sherry |
| Movement & Zones | Albert | Albert | Sherry |
| Day Progression | Albert | Sherry | Anjani |
| Random Events | Albert | Joshua | Sherry |
| Bill Payment | Albert | Sherry | Anjani |
| Stock Trading | Cynthia | Cynthia | Albert |
| NPC Interactions | Albert | Jayden | Albert |
| Save/Load | Albert | Anjani | Sherry |
| Week Endings | Albert | Sherry | Albert |

---

### Pre-Recording Strategy

**What to Pre-Record:**
1. **Full backup demo** (5-7 minutes) showing all features working
2. **Individual feature clips** (30-60 seconds each) as fallback for specific features:
   - Random event triggering and outcome
   - Stock trading session with profit/loss
   - NPC dialogue exchange
   - Week ending sequence

**Why Pre-Record:**
- Eliminate risk of technical failures during live presentation
- Ensure smooth timing and professional delivery
- Have fallback for incomplete UI features
- Show ideal gameplay flow without interruptions

**When to Use Pre-Recorded:**
- Technical issues during live demo
- Feature UI not fully integrated by presentation date
- Time management (can skip to key moments)
- Network issues (for API-dependent features)

---

## Presentation Day Checklist

### Before Presentation:
- [ ] All team members present and know their slides
- [ ] Laptop/demo computer tested and working
- [ ] Game application tested and launches successfully
- [ ] All external dependencies tested (internet for APIs)
- [ ] Backup recording ready and accessible
- [ ] Slide deck loaded and tested
- [ ] Screen sharing/projector connection tested
- [ ] Timer/stopwatch ready for time management
- [ ] Water bottles for presenters

### Technical Setup:
- [ ] Java 11+ installed
- [ ] Maven dependencies downloaded
- [ ] Game compiled and ready to run
- [ ] API keys configured (Alpha Vantage, Google Gemini)
- [ ] Save files reset to clean state for demo
- [ ] Audio levels tested (game music not too loud)
- [ ] Screen resolution appropriate for projection
- [ ] Backup laptop available

### Presentation Materials:
- [ ] Slide deck (PowerPoint/PDF)
- [ ] Demo video backup (MP4 format)
- [ ] Source code accessible (for code walkthrough if needed)
- [ ] README.md printed/accessible
- [ ] Team contract printed/accessible
- [ ] Note cards with talking points

---

## Time Allocation Summary

| Section | Time | Presenters |
|---------|------|-----------|
| Introduction | 1 min | Albert |
| Project Overview | 2 min | Sherry |
| Technology & Architecture | 3.5 min | Albert, Anjani |
| Use Cases (6 total) | 12 min | All team members |
| Additional Systems | 1.5 min | Albert |
| Testing | 1.5 min | Joshua |
| Design Patterns | 1.5 min | Anjani |
| Challenges & Lessons | 3.5 min | Jayden, Sherry |
| Conclusion | 0.5 min | Albert |
| **Slides Total** | **27 min** | |
| **Demo** | **5-7 min** | Albert, Cynthia (primary) |
| **Q&A Buffer** | **3-5 min** | All |
| **Total Presentation** | **35-40 min** | |

---

## Notes

- Every team member presents at least their use case (requirement met)
- Presentation aligns with Final Project Presentation Expectations rubric
- Demo showcases all major features and Clean Architecture benefits
- Time buffer included for Q&A and transitions
- Backup plans for technical issues
- Professional and engaging delivery planned

---

**Prepared by:** Team TUT0401-14  
**Last Updated:** November 24, 2024
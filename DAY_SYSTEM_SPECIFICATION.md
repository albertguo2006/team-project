# Day System Technical Specification

## Overview
This document specifies the implementation of a day-based progression system where the game runs from Monday through Friday. Players advance days by sleeping in a designated zone within their home, triggering a day summary screen and health restoration.

## System Architecture

### 1. Entity Layer Changes

#### 1.1 Day Enum
**File**: [`src/main/java/entity/Day.java`](src/main/java/entity/Day.java)
```java
public enum Day {
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday", 4),
    FRIDAY("Friday", 5);
    
    private final String displayName;
    private final int dayNumber;
    
    // Methods: getDisplayName(), getDayNumber(), getNext(), isLastDay()
}
```

#### 1.2 Player Entity Updates
**File**: [`src/main/java/entity/Player.java`](src/main/java/entity/Player.java:1)

**New Fields**:
- `Day currentDay` - tracks current game day (starts Monday)
- `boolean hasSleptToday` - prevents multiple sleeps per day
- `int health` - replaces or supplements Energy stat (0-100)
- `double dailyEarnings` - money earned today
- `double dailySpending` - money spent today

**New Methods**:
- `getCurrentDay()` / `setCurrentDay(Day day)`
- `hasSleptToday()` / `setHasSleptToday(boolean slept)`
- `getHealth()` / `setHealth(int health)`
- `getDailyEarnings()` / `addDailyEarnings(double amount)`
- `getDailySpending()` / `addDailySpending(double amount)`
- `resetDailyFinancials()` - called at start of new day
- `advanceDay()` - increments to next day, resets hasSleptToday

#### 1.3 DaySummary Entity (NEW)
**File**: [`src/main/java/entity/DaySummary.java`](src/main/java/entity/DaySummary.java)
```java
public class DaySummary {
    private final Day completedDay;
    private final double earnings;
    private final double spending;
    private final double netChange;
    private final double newBalance;
    
    // Constructor and getters
}
```

#### 1.4 GameEnding Entity (NEW)
**File**: [`src/main/java/entity/GameEnding.java`](src/main/java/entity/GameEnding.java)
```java
public class GameEnding {
    public enum EndingType {
        WEALTHY,    // Balance >= 5000
        COMFORTABLE, // Balance >= 2000
        STRUGGLING,  // Balance >= 1000
        BROKE        // Balance < 1000
    }
    
    private final EndingType type;
    private final double finalBalance;
    private final String message;
    
    // Static factory method: determineEnding(double balance)
}
```

### 2. Use Case Layer

#### 2.1 Sleep Use Case
**Directory**: [`src/main/java/use_case/sleep/`](src/main/java/use_case/sleep/)

**SleepInputBoundary** (interface)
```java
public interface SleepInputBoundary {
    void execute(SleepInputData inputData);
}
```

**SleepInputData**
```java
public class SleepInputData {
    private final Player player;
    // Constructor and getters
}
```

**SleepOutputBoundary** (interface)
```java
public interface SleepOutputBoundary {
    void presentDaySummary(SleepOutputData outputData);
    void presentGameEnding(GameEnding ending);
    void presentSleepError(String errorMessage);
}
```

**SleepOutputData**
```java
public class SleepOutputData {
    private final DaySummary summary;
    private final Day newDay;
    private final boolean isWeekComplete;
    // Constructor and getters
}
```

**SleepDataAccessInterface**
```java
public interface SleepDataAccessInterface {
    void savePlayerState(Player player);
    DaySummary getDaySummary(Day day, double earnings, double spending, double balance);
}
```

**SleepInteractor**
**File**: [`src/main/java/use_case/sleep/SleepInteractor.java`](src/main/java/use_case/sleep/SleepInteractor.java:1)

Logic:
1. Validate player hasn't slept today
2. Restore health to 100
3. Create DaySummary from daily financials
4. Check if Friday (week complete)
5. If week complete → determine ending and call `presentGameEnding()`
6. If not complete → advance day, reset flags, call `presentDaySummary()`
7. Save player state via data access

### 3. Interface Adapter Layer

#### 3.1 Sleep Controller
**File**: [`src/main/java/interface_adapter/sleep/SleepController.java`](src/main/java/interface_adapter/sleep/SleepController.java)
```java
public class SleepController {
    private final SleepInputBoundary sleepInteractor;
    
    public void sleep(Player player) {
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);
    }
}
```

#### 3.2 Sleep Presenter
**File**: [`src/main/java/interface_adapter/sleep/SleepPresenter.java`](src/main/java/interface_adapter/sleep/SleepPresenter.java)
```java
public class SleepPresenter implements SleepOutputBoundary {
    private final ViewManagerModel viewManager;
    private final SleepViewModel sleepViewModel;
    
    @Override
    public void presentDaySummary(SleepOutputData outputData) {
        // Update view model with summary data
        // Trigger view switch to DaySummaryView
    }
    
    @Override
    public void presentGameEnding(GameEnding ending) {
        // Update view model with ending data
        // Trigger view switch to EndGameView
    }
    
    @Override
    public void presentSleepError(String errorMessage) {
        // Show error (already slept today, etc.)
    }
}
```

#### 3.3 Sleep View Model
**File**: [`src/main/java/interface_adapter/sleep/SleepViewModel.java`](src/main/java/interface_adapter/sleep/SleepViewModel.java)
```java
public class SleepViewModel extends ViewModel {
    private DaySummary currentSummary;
    private Day newDay;
    private boolean isWeekComplete;
    private GameEnding ending;
    
    // Getters, setters, property change support
}
```

### 4. View Layer

#### 4.1 GamePanel Updates
**File**: [`src/main/java/view/GamePanel.java`](src/main/java/view/GamePanel.java:1)

**New Constants**:
```java
private static final int SLEEP_ZONE_X = VIRTUAL_WIDTH - 400;  // Top-right X
private static final int SLEEP_ZONE_Y = 0;                     // Top-right Y
private static final int SLEEP_ZONE_WIDTH = 400;
private static final int SLEEP_ZONE_HEIGHT = 400;
```

**New Fields**:
```java
private SleepController sleepController;
private boolean inSleepZone = false;
```

**New Methods**:
- `setSleepController(SleepController controller)` - dependency injection
- `checkSleepZone()` - called in `actionPerformed()`, checks player position
- `drawSleepPrompt(Graphics2D g)` - draws "Press E to Sleep" when `inSleepZone`
- `drawDayDisplay(Graphics2D g)` - shows current day in HUD

**Updates to `drawUI()`**:
- Add health bar display (below or replacing Energy)
- Add current day display (e.g., "Monday - Day 1/5")

#### 4.2 DaySummaryView (NEW)
**File**: [`src/main/java/view/DaySummaryView.java`](src/main/java/view/DaySummaryView.java)

**Purpose**: Shows daily financial summary and prompts for next day

**Layout** (1920x1200 virtual coordinates):
- Fade-from-black animation (0.5 seconds)
- Center panel showing:
  - "Day Complete: [Day Name]"
  - "Earnings: $X.XX"
  - "Spending: $X.XX"
  - "Net Change: ±$X.XX"
  - "New Balance: $X.XX"
  - "Press ENTER to continue to [Next Day]"

**Key Handling**:
- ENTER key → fade to black → return to GamePanel → reset `hasSleptToday`

#### 4.3 EndGameView (NEW)
**File**: [`src/main/java/view/EndGameView.java`](src/main/java/view/EndGameView.java)

**Purpose**: Shows week completion and ending based on balance

**Layout** (1920x1200 virtual coordinates):
- "Week Complete!"
- Ending type title (e.g., "The Wealthy Executive")
- Ending message (different for each ending type)
- Final balance display
- "Press ENTER to return to Main Menu"

**Ending Tiers**:
- **WEALTHY** (≥$5000): "You've mastered the art of financial success!"
- **COMFORTABLE** (≥$2000): "You made it through with money to spare."
- **STRUGGLING** (≥$1000): "You survived, but barely."
- **BROKE** (<$1000): "The city has defeated you... for now."

#### 4.4 PlayerInputController Updates
**File**: [`src/main/java/interface_adapter/events/PlayerInputController.java`](src/main/java/interface_adapter/events/PlayerInputController.java:1)

**New Field**:
```java
private SleepController sleepController;
private Supplier<Boolean> inSleepZoneCheck;  // Callback to check zone
```

**New Methods**:
```java
public void setSleepController(SleepController controller)
public void setInSleepZoneCheck(Supplier<Boolean> check)
```

**Update `keyPressed()`**:
```java
case KeyEvent.VK_E:
    if (inSleepZoneCheck != null && inSleepZoneCheck.get() && sleepController != null) {
        sleepController.sleep(playerMovementUseCase.getPlayer());
    }
    break;
```

### 5. Data Flow Diagram

```
Player presses E in sleep zone
         ↓
PlayerInputController.keyPressed(E)
         ↓
SleepController.sleep(player)
         ↓
SleepInteractor.execute(inputData)
         ↓
[Validates, updates Player, creates DaySummary]
         ↓
    [Is Friday?]
   /            \
 YES            NO
  ↓              ↓
presentGameEnding  presentDaySummary
  ↓              ↓
SleepPresenter   SleepPresenter
  ↓              ↓
EndGameView    DaySummaryView
  ↓              ↓
Main Menu      GamePanel (new day)
```

### 6. Integration Points

#### 6.1 MainGameWindow Updates
**File**: [`src/main/java/view/MainGameWindow.java`](src/main/java/view/MainGameWindow.java)

- Add DaySummaryView to CardLayout
- Add EndGameView to CardLayout
- Register views with ViewManagerModel
- Set up view transitions

#### 6.2 Main.java Wiring
**File**: [`src/main/java/app/Main.java`](src/main/java/app/Main.java)

1. Create SleepViewModel
2. Create SleepDataAccessObject (implements SleepDataAccessInterface)
3. Create SleepPresenter (with ViewManagerModel and SleepViewModel)
4. Create SleepInteractor (with presenter and data access)
5. Create SleepController (with interactor)
6. Inject SleepController into GamePanel
7. Set up `inSleepZoneCheck` callback in PlayerInputController

### 7. Visual Design Specifications

#### 7.1 Sleep Zone Indicator
- **Position**: Top-right 400x400px of Home zone
- **Visual**: Semi-transparent overlay when player enters zone
- **Color**: Light blue with 30% opacity (`new Color(100, 150, 255, 77)`)
- **Prompt**: White text, 32pt font, "Press E to Sleep"
- **Prompt Position**: Center of sleep zone

#### 7.2 Day Display in HUD
- **Position**: Top-left, below position/balance
- **Format**: "Monday - Day 1/5"
- **Font**: Arial Bold, 28pt
- **Color**: White with semi-transparent black background

#### 7.3 Health Bar
- **Position**: Top-left HUD, replacing or below Energy stat
- **Visual**: Horizontal bar, green fill
- **Label**: "Health: 75/100" or bar with percentage

#### 7.4 Fade Transitions
- **Duration**: 500ms each (fade out, fade in)
- **Color**: Pure black (`Color.BLACK`)
- **Implementation**: Timer-based alpha blending in view components

### 8. State Management

#### 8.1 Daily Financial Tracking
When money changes occur:
```java
// On earnings
player.addDailyEarnings(amount);
player.setBalance(player.getBalance() + amount);

// On spending
player.addDailySpending(amount);
player.setBalance(player.getBalance() - amount);
```

#### 8.2 Sleep State Tracking
```java
// On sleep attempt
if (player.hasSleptToday()) {
    presenter.presentSleepError("You've already slept today!");
    return;
}

// After successful sleep
player.setHasSleptToday(true);
player.setHealth(100);
player.advanceDay();
player.resetDailyFinancials();
```

### 9. Error Handling

#### 9.1 Already Slept
- Message: "You've already slept today. Come back tomorrow!"
- Display: Toast/popup in game view
- Duration: 3 seconds

#### 9.2 Not in Sleep Zone
- No error message, E key simply does nothing
- Visual feedback only via presence/absence of prompt

### 10. Testing Considerations

#### 10.1 Unit Tests Needed
- `Day.getNext()` logic
- `Player.advanceDay()` state changes
- `SleepInteractor` validation logic
- `GameEnding.determineEnding()` tier calculation

#### 10.2 Integration Tests
- Full sleep cycle: press E → summary screen → continue
- Week completion flow
- Multiple sleep attempt prevention
- Financial tracking accuracy

### 11. Future Enhancements (Out of Scope)

- Animated sleep sequence
- Dream sequences
- Variable sleep zones in other locations
- Alarm clock mechanic
- Weekend days (Saturday/Sunday)
- Daily events/challenges

## Implementation Order

The todo list follows this logical order:
1. **Entity layer** - Add Day enum, update Player
2. **Use case layer** - Implement sleep interactor
3. **Detection logic** - Sleep zone check in GamePanel
4. **Input handling** - E key in PlayerInputController
5. **Views** - DaySummaryView and EndGameView
6. **Integration** - Wire everything in Main.java
7. **Visual polish** - Transitions, HUD updates

## File Summary

### New Files (11)
1. `src/main/java/entity/Day.java`
2. `src/main/java/entity/DaySummary.java`
3. `src/main/java/entity/GameEnding.java`
4. `src/main/java/use_case/sleep/SleepInputData.java`
5. `src/main/java/use_case/sleep/SleepOutputData.java`
6. `src/main/java/use_case/sleep/SleepDataAccessInterface.java`
7. `src/main/java/interface_adapter/sleep/SleepController.java`
8. `src/main/java/interface_adapter/sleep/SleepPresenter.java`
9. `src/main/java/interface_adapter/sleep/SleepViewModel.java`
10. `src/main/java/view/DaySummaryView.java`
11. `src/main/java/view/EndGameView.java`

### Modified Files (5)
1. [`src/main/java/entity/Player.java`](src/main/java/entity/Player.java:1)
2. [`src/main/java/use_case/sleep/SleepInteractor.java`](src/main/java/use_case/sleep/SleepInteractor.java:1)
3. [`src/main/java/view/GamePanel.java`](src/main/java/view/GamePanel.java:1)
4. [`src/main/java/interface_adapter/events/PlayerInputController.java`](src/main/java/interface_adapter/events/PlayerInputController.java:1)
5. [`src/main/java/app/Main.java`](src/main/java/app/Main.java)

## Clean Architecture Compliance

This design follows Clean Architecture principles:
- **Entities**: Pure business objects (Day, Player, DaySummary, GameEnding)
- **Use Cases**: Business logic (SleepInteractor) depends only on interfaces
- **Interface Adapters**: Controllers/Presenters translate between layers
- **Frameworks**: Views handle UI rendering, depend on view models
- **Dependency Rule**: All dependencies point inward toward entities
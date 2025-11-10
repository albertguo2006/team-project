# GUI Implementation Guide: Use Case 1 - Character Movement

## Overview

This document describes the GUI implementation for **Use Case 1: Environment Interaction** with a focus on character movement in a static 2D environment. The implementation follows **Clean Architecture** principles, separating concerns into distinct layers.

## Architecture Layers

### 1. **Domain Layer** (Pure Business Logic)
- **Player Entity** (`entity/Player.java`)
  - Contains position: `x`, `y` (coordinates in the game world)
  - Contains `speed` (pixels per second)
  - No knowledge of rendering or input handling
  - Purely represents the player's state

### 2. **Application Layer** (Use Cases)
- **PlayerMovementUseCase** (`use_case/PlayerMovementUseCase.java`)
  - Manages the player's movement state (which directions are currently pressed)
  - Calculates position updates based on elapsed time and movement state
  - Implements collision detection logic
  - Completely decoupled from Swing and GUI concerns
  - Exposes methods:
    - `setMovementState(Direction dir, boolean isMoving)` - Called by input controllers
    - `updatePosition(double deltaTime)` - Called each game loop tick
    - `getPlayer()` - Returns the current player entity

### 3. **Presentation Layer** (Controllers & Adapters)
- **PlayerInputController** (`interface_adapter/events/PlayerInputController.java`)
  - Implements `KeyListener` from Swing
  - Translates raw keyboard events into domain concepts
  - Maps WASD keys to `Direction` enum values
  - Calls `playerMovementUseCase.setMovementState()` on key press/release
  - Knows nothing about rendering or game state persistence

### 4. **Infrastructure Layer** (Frameworks & UI)
- **MainGameWindow** (`view/MainGameWindow.java`)
  - Extends `JFrame`
  - Top-level application window
  - Hosts the `GamePanel`
  - Coordinates setup of controllers and panels
  
- **GamePanel** (`view/GamePanel.java`)
  - Extends `JPanel`
  - Contains the **real-time game loop** using `javax.swing.Timer`
  - Implements `paintComponent(Graphics g)` for rendering
  - Updates game state and renders each frame
  - Attaches `PlayerInputController` as a `KeyListener`

## Game Loop Architecture

The game loop is the "heartbeat" of the application, running at ~60 FPS (every 16ms).

```
[Main Thread]
    ↓
[javax.swing.Timer fires every 16ms]
    ↓
[GamePanel.actionPerformed() is called]
    ↓
┌─────────────────────────────────────┐
│ 1. UPDATE PHASE                     │
│   - playerMovementUseCase           │
│      .updatePosition(deltaTime)     │
│   - Updates Player.x, Player.y      │
│   - Handles collision detection     │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ 2. RENDER PHASE                     │
│   - this.repaint()                  │
│   - Schedules paintComponent()      │
└─────────────────────────────────────┘
    ↓
[Swing EDT calls paintComponent()]
    ↓
┌─────────────────────────────────────┐
│ 3. PAINT PHASE                      │
│   - Read Player.getX(), getY()      │
│   - Draw player sprite at (x, y)    │
│   - Draw background and NPCs        │
│   - Draw UI (health, status, etc.)  │
└─────────────────────────────────────┘
    ↓
[Frame rendered to screen]
    ↓
[Wait 16ms, repeat]
```

## Input Handling Flow

```
[User presses 'W' key]
    ↓
[PlayerInputController.keyPressed(KeyEvent e)]
    ↓
[Extract Direction.UP from 'W']
    ↓
[playerMovementUseCase.setMovementState(Direction.UP, true)]
    ↓
[Use case stores: "UP is currently pressed"]
    ↓
[Each game loop tick: updatePosition() calculates movement based on stored state]
```

## Key Design Principles

### Separation of Concerns
- **Domain** doesn't know about Swing or rendering
- **Application** doesn't import any GUI classes
- **Presentation** translates between input/output and use cases
- **Infrastructure** contains all volatile technology (Swing, graphics)

### Dependency Injection
- `GamePanel` receives `PlayerMovementUseCase` and `PlayerInputController` in its constructor
- Controllers receive `PlayerMovementUseCase` in their constructor
- This allows for easy testing and swapping implementations

### Single Responsibility
- `PlayerMovementUseCase` handles ONLY movement logic
- `PlayerInputController` handles ONLY input translation
- `GamePanel` handles ONLY rendering and loop coordination

## Class Interfaces Summary

### Direction Enum
```java
public enum Direction {
    UP, DOWN, LEFT, RIGHT
}
```

### PlayerMovementUseCase Interface
```java
public class PlayerMovementUseCase {
    public void setMovementState(Direction direction, boolean isMoving) { /* ... */ }
    public void updatePosition(double deltaTime) { /* ... */ }
    public Player getPlayer() { /* ... */ }
}
```

### PlayerInputController Interface
```java
public class PlayerInputController implements KeyListener {
    public void keyPressed(KeyEvent e) { /* ... */ }
    public void keyReleased(KeyEvent e) { /* ... */ }
    public void keyTyped(KeyEvent e) { /* not used */ }
}
```

### GamePanel Interface (Key Methods)
```java
public class GamePanel extends JPanel implements ActionListener {
    public GamePanel(PlayerMovementUseCase useCase, PlayerInputController controller) { /* ... */ }
    protected void paintComponent(Graphics g) { /* ... */ }
    public void actionPerformed(ActionEvent e) { /* loop tick */ }
}
```

## Data Flow Diagram: A Frame Update

```
INPUT PHASE
─────────────────────────────────────────────
User Input (WASD)
    ↓ [PlayerInputController.keyPressed/Released]
    ↓
PlayerMovementUseCase.setMovementState()
    ↓ [Stores direction state]
    ↓

UPDATE PHASE
─────────────────────────────────────────────
GamePanel.actionPerformed()  [called every 16ms]
    ↓
PlayerMovementUseCase.updatePosition(deltaTime)
    ↓ [Reads movement state]
    ↓ [Calculates: newX = oldX + (speedX * deltaTime)]
    ↓ [Calculates: newY = oldY + (speedY * deltaTime)]
    ↓ [Checks collision]
    ↓ [Updates Player.x and Player.y]
    ↓

RENDER PHASE
─────────────────────────────────────────────
GamePanel.repaint()  [signals need for redraw]
    ↓
GamePanel.paintComponent(Graphics g)
    ↓ [Reads Player.x, Player.y]
    ↓ [Draws background]
    ↓ [Draws player sprite at (x, y)]
    ↓ [Draws UI overlays]
    ↓
Display updated to screen
```

## Collision Detection Strategy

The `PlayerMovementUseCase.updatePosition()` method handles collision detection:

1. **Calculate desired position**: `newX = x + (vx * deltaTime)`
2. **Check against world bounds**: Ensure player stays within screen bounds
3. **Check against solid objects**: Verify no collision with static obstacles
4. **Only update if valid**: If collision detected, revert to previous position

This keeps collision logic in the application layer, testable and independent of rendering.

## Threading Considerations

- **Swing EDT (Event Dispatch Thread)**: All GUI operations happen here
- **Timer Callbacks**: `javax.swing.Timer` fires on the EDT (thread-safe)
- **Input Events**: Keyboard events arrive on the EDT
- **No Manual Threading**: We don't create additional threads (yet), keeping it simple

## Extension Points for Future Use Cases

This architecture is designed to support additional use cases:

1. **NPCs**: Add `List<NPC>` to domain; render in `GamePanel.paintComponent()`
2. **Interactions**: `PlayerInputController` can detect 'E' key and call `NpcInteractionUseCase`
3. **Random Events**: Can be triggered from the game loop or time-based
4. **Stock Minigame**: Can be rendered as a separate `JPanel`, swapped via `CardLayout`
5. **Saving/Loading**: `GamePanel` can serialize/deserialize `Player` state

## Summary

This GUI implementation provides:
- ✅ Real-time 2D character movement
- ✅ Clean separation between game logic and presentation
- ✅ Extensible architecture for additional features
- ✅ Testable business logic (use cases are POJOs)
- ✅ Responsive input handling at 60 FPS

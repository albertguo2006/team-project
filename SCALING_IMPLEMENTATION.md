# Display Scaling Implementation - 16:10 Aspect Ratio

## Summary

This implementation adds a complete virtual viewport system with 16:10 aspect ratio support, resizable window, and Wayland compatibility to the California Prop. 65 game.

## Key Features Implemented

### 1. Virtual Resolution System (1920x1200)
- **Internal Resolution**: All game logic operates in a fixed 1920x1200 virtual coordinate space
- **Viewport Scaling**: Graphics are automatically scaled to fit any window size while maintaining 16:10 aspect ratio
- **Letterboxing/Pillarboxing**: Black bars are added automatically when window aspect ratio doesn't match 16:10

### 2. Resizable Window
- **Minimum Size**: 640x400 pixels (maintains 16:10 ratio)
- **Initial Size**: 1280x800 pixels (half of virtual resolution)
- **Dynamic Scaling**: Window can be resized to any size, game scales automatically

### 3. Wayland Support (Linux)
- **Automatic Detection**: Detects Wayland vs X11 session
- **HiDPI Support**: Enables proper scaling on high-DPI displays
- **Fallback**: Gracefully falls back to X11 if Wayland unavailable

### 4. Coordinate Translation
- **Screen to Virtual**: Helper method converts mouse coordinates from screen space to virtual 1920x1200 space
- **Scale Factor**: Dynamically calculated based on current window size

## Files Modified

### 1. [`GamePanel.java`](src/main/java/view/GamePanel.java)
**Changes**:
- Added virtual resolution constants (VIRTUAL_WIDTH=1920, VIRTUAL_HEIGHT=1200)
- Implemented `calculateViewport()` method for aspect ratio maintenance
- Added `screenToVirtual()` coordinate translation method
- Updated `paintComponent()` to use viewport with Graphics2D transform
- Scaled all rendering constants (player size, fonts, UI elements) for 1920x1200
- Updated zone transition logic to use virtual dimensions instead of actual window size

**Key Methods**:
```java
private void calculateViewport()  // Maintains 16:10 aspect ratio
public Point screenToVirtual(int screenX, int screenY)  // Coordinate conversion
private void drawGame(Graphics2D g)  // Renders in virtual space
```

### 2. [`MainGameWindow.java`](src/main/java/view/MainGameWindow.java)
**Changes**:
- Enabled window resizing (`setResizable(true)`)
- Set minimum size to 640x400 (16:10 ratio)
- Set preferred size to 1280x800 (half of virtual resolution)
- Added `java.awt.*` import for Dimension class

### 3. [`Main.java`](src/main/java/app/Main.java)
**Changes**:
- Enabled HiDPI/scaling support with system property
- Added Wayland detection and logging
- Prints session type (Wayland or X11) on startup

### 4. [`Player.java`](src/main/java/entity/Player.java)
**Changes**:
- Updated default starting position to center of virtual resolution (960, 600)
- Scaled player speed from 150 to 360 pixels/second (proportional to resolution increase)

## How It Works

### Rendering Pipeline

```
Window (any size)
    ↓
Calculate viewport dimensions maintaining 16:10
    ↓
Add black bars (letterbox/pillarbox) if needed
    ↓
Clip rendering to viewport
    ↓
Translate and scale Graphics2D context
    ↓
Draw all game elements in virtual 1920x1200 space
    ↓
Automatically scaled to fit viewport
```

### Aspect Ratio Logic

```java
double windowAspect = windowWidth / windowHeight;

if (windowAspect > 16/10) {
    // Window wider than 16:10 → Pillarbox (vertical black bars)
    viewportHeight = windowHeight;
    viewportWidth = (int)(windowHeight * 16/10);
    viewportX = (windowWidth - viewportWidth) / 2;
} else {
    // Window taller than 16:10 → Letterbox (horizontal black bars)
    viewportWidth = windowWidth;
    viewportHeight = (int)(windowWidth / (16/10));
    viewportY = (windowHeight - viewportHeight) / 2;
}
```

### Coordinate Translation

For mouse input or other screen-space coordinates:

```java
// Convert screen coordinates to virtual coordinates
Point virtualPos = gamePanel.screenToVirtual(mouseX, mouseY);
// Use virtualPos.x and virtualPos.y for game logic
```

## Testing Instructions

### Using IntelliJ IDEA

1. **Open Project**: Open the project in IntelliJ IDEA
2. **Build Project**: Build → Build Project (Ctrl+F9 / Cmd+F9)
3. **Run Main**: Right-click on [`Main.java`](src/main/java/app/Main.java) → Run 'Main.main()'

### What to Test

1. **Initial Display**:
   - Window should open at 1280x800 centered on screen
   - Game should render with proper aspect ratio
   - Player should be centered in the game area

2. **Resizing**:
   - Drag window corners to resize
   - Game should scale smoothly
   - Aspect ratio should remain 16:10
   - Black bars should appear if window aspect ratio differs from 16:10

3. **Minimum Size**:
   - Try to make window smaller than 640x400
   - Should not go below minimum size

4. **Player Movement**:
   - Use WASD to move player
   - Movement speed should feel appropriate
   - Zone transitions should work at edges (1920 width, 1200 height boundaries)

5. **UI Scaling**:
   - HUD (position, balance) should scale with window
   - Zone name at top should remain centered and readable
   - Instructions at bottom should scale properly

6. **Linux Wayland** (if applicable):
   - Run on Linux with Wayland session
   - Check console output for "Detected Wayland session"
   - Window should render properly with native Wayland support

## Submenu Implementation Guide

For future submenu implementations (e.g., Pay Bills, Sleep), use one of these approaches:

### Option 1: JDialog (Recommended)

```java
public void showBillPaymentDialog() {
    JDialog dialog = new JDialog(mainWindow, "Pay Bills", true);
    dialog.setLayout(new BorderLayout());
    
    // Add your UI components
    JPanel panel = new JPanel();
    // ... build panel with bill list, buttons, etc.
    
    dialog.add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(mainWindow);
    dialog.setVisible(true); // Blocks until closed
}
```

**Advantages**: Clean separation, standard behavior, automatic focus management

### Option 2: Virtual Space Overlay

Render directly in the virtual 1920x1200 space:

```java
private boolean showingBillMenu = false;

private void drawGame(Graphics2D g) {
    // ... normal game rendering ...
    
    if (showingBillMenu) {
        drawBillMenu(g, 460, 300, 1000, 600); // Centered
    }
}

private void drawBillMenu(Graphics2D g, int x, int y, int width, int height) {
    // Semi-transparent background
    g.setColor(new Color(240, 240, 240, 230));
    g.fillRoundRect(x, y, width, height, 20, 20);
    
    // Draw UI elements in virtual coordinates
    // All scaling handled automatically
}
```

**Advantages**: No separate window, part of game rendering, scales automatically

## Technical Details

### Scaling Factor Calculation

```java
scale = viewportWidth / VIRTUAL_WIDTH;  // e.g., 1280 / 1920 = 0.667
```

### Font Scaling

All fonts were scaled by approximately 2x for the 1920x1200 resolution:
- Zone name: 24pt → 48pt
- HUD text: 14pt → 28pt
- Instructions: 12pt → 24pt

### Player Rendering

Player size doubled: 32x32 → 64x64 pixels in virtual space

### Performance

The Graphics2D affine transform has minimal overhead. Expected frame rate should remain at target 60 FPS on modern hardware.

## Troubleshooting

### Issue: Game appears blurry
**Solution**: Ensure HiDPI is enabled in Main.java. On Linux, check display scaling settings.

### Issue: Black bars too large
**Solution**: This is expected when window aspect ratio differs from 16:10. Resize window closer to 16:10 ratio.

### Issue: Player moves too fast/slow
**Solution**: Adjust `speed` value in Player constructor. Current value: 360 pixels/second in virtual space.

### Issue: Zone transitions not working
**Solution**: Verify zone boundaries are using VIRTUAL_WIDTH (1920) and VIRTUAL_HEIGHT (1200), not window dimensions.

## Future Enhancements

1. **Fullscreen Mode**: Add toggle for borderless fullscreen
2. **Settings Menu**: Let users choose preferred window size presets
3. **Resolution Options**: Allow switching between different virtual resolutions
4. **Save Window Size**: Remember user's preferred window size between sessions
5. **Aspect Ratio Lock**: Add option to force window to specific aspect ratios

## Compatibility

- **Java Version**: Requires Java 11+ (as per pom.xml)
- **Wayland Support**: Java 17+ recommended for full Wayland support
- **Operating Systems**: Windows, macOS, Linux (X11 and Wayland)
- **Display Scaling**: Supports HiDPI/Retina displays

## Architecture Compliance

This implementation maintains Clean Architecture principles:
- **Entity Layer**: Player coordinates remain in domain model
- **Use Case Layer**: Movement logic unchanged
- **Interface Adapter**: No changes needed
- **Infrastructure**: All scaling handled in View layer (GamePanel, MainGameWindow)
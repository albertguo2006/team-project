# Game Map Design

This document describes the layout and structure of the game world for California Prop. 65. The world is divided into multiple screens (zones), each representing a distinct area. When the player reaches the edge of a screen, the game transitions to the adjacent zone. x denotes two zones are not connected

## Map Overview

```
+-------------------+-------------------+
| Home              x Subway Station 1  |
|                   x  (connects to 2)  |
+-------------------+-------------------+
| Street 1          | Street 2          |
|                   |                   |
+-------------------+-------------------+
|                   |                   |
|                   | Grocery Store     |
+-------------------+-------------------+

+-------------------+-------------------+
|                   x Office            |
|                   x (Your Cubicle)    |
+-------------------+-------------------+
| Street 3          | Office Lobby      |
+-------------------+-------------------+
| Subway Station 2  |                   |
+-------------------+-------------------+
```

## Zone List & Connections

### Zone 1: Home
- Top-left screen
- Exits:
  - Down: Street 1
  - Right is BLOCKED

### Zone 2: Subway Station 1
- Top-right screen
- Exits:
  - Down: Street 2
  - **Up (Special): Subway Tunnel to Subway Station 2**

### Zone 3: Street 1
- Bottom-left screen
- Exits:
  - Up: Home
  - Right: Street 2
  - Down: Subway Station 2

### Zone 4: Street 2 / Grocery Store
- Bottom-right screen
- Exits:
  - Up: Subway Station 1
  - Left: Street 1
  - Right: Grocery Store
  - Down is BLOCKED

### Zone 4b: Grocery Store
- Connects only to Street 2 (LEFT)
- Dead-end destination

### Zone 5: Subway Station 2
- Lower-left screen (second map row)
- Exits:
  - Up: Street 1
  - **Down (Special): Subway Tunnel to Subway Station 1**
  - Down-exit: Street 3 (blocked by tunnel)

### Zone 6: Office (Your Cubicle)
- Lower-right screen (second map row)
- Exits:
  - Down: Office Lobby ONLY
  - Up is BLOCKED
  - Left is BLOCKED
  - Right is BLOCKED

### Zone 7: Street 3
- Bottom-left screen (third map row)
- Exits:
  - Up: Subway Station 2
  - Right: Office Lobby

### Zone 8: Office Lobby
- Bottom-right screen (third map row)
- Exits:
  - Up: Office (Your Cubicle)
  - Left: Street 3

## Special Transitions

Special transitions allow travel between non-adjacent zones, useful for implementing tunnels, elevators, and fast travel.

### Subway Tunnel
- **Route**: Subway Station 1 ↔ Subway Station 2
- **Activation**: 
  - From Subway Station 1: Move UP
  - From Subway Station 2: Move DOWN
- **Purpose**: Connects the two subway stations that are not physically adjacent on the map
- **Future use**: Can add visual feedback, travel time, or special events

## Transition Logic
- The game world is a grid of screens (zones).
- When the player moves to the edge of the current screen, the game checks:
  1. First for special transitions (tunnels, elevators)
  2. Then for normal zone connections
- If a valid transition exists, the player is moved to the adjacent zone at the corresponding entry edge
- If no transition exists, the player is blocked from moving beyond the edge

## Example Transitions
- Player moves right at the edge of "Home" → BLOCKED (no connection)
- Player moves down at the edge of "Street 1" → loads "Subway Station 2" at top edge
- Player moves UP at the edge of "Subway Station 1" → loads "Subway Station 2" at bottom edge (via subway tunnel)

## Future Extensions
- Add more zones for additional gameplay areas.
- Add interactive objects and NPCs to each zone.
- Implement fast travel between subway stations.

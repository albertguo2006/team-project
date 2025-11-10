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
| Subway Station 2  x Office            |
|  (connects to 1)  x (Your Cubicle)    |
+-------------------+-------------------+
| Street 3          | Office Lobby      |
+-------------------+-------------------+
```

## Zone List & Connections

### Zone 1: Home
- Top-left screen
- Exits:
  - Right: Subway Station 1
  - Down: Street 1

### Zone 2: Subway Station 1
- Top-right screen
- Exits:
  - Left: Home
  - Down: Street 2 / Grocery Store

### Zone 3: Street 1
- Bottom-left screen
- Exits:
  - Up: Home
  - Right: Street 2 / Grocery Store
  - Down: Subway Station 2

### Zone 4: Street 2 / Grocery Store
- Bottom-right screen
- Exits:
  - Up: Subway Station 1
  - Left: Street 1
  - Down: Office

### Zone 5: Subway Station 2
- Lower-left screen (second map row)
- Exits:
  - Up: Street 1
  - Right: Office
  - Down: Street 3

### Zone 6: Office (Your Cubicle)
- Lower-right screen (second map row)
- Exits:
  - Up: Street 2 / Grocery Store
  - Left: Subway Station 2
  - Down: Office Lobby

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

## Transition Logic
- The game world is a grid of screens (zones).
- When the player moves to the edge of the current screen, the game loads the adjacent zone and places the player at the corresponding entry edge.
- Each zone has its own background, objects, and NPCs.

## Example Transition
- Player moves right at the edge of "Home" → loads "Subway Station 1" and places player at the left edge of that screen.
- Player moves down at the edge of "Street 1" → loads "Subway Station 2" and places player at the top edge of that screen.

## Future Extensions
- Add more zones for additional gameplay areas.
- Add interactive objects and NPCs to each zone.
- Implement fast travel between subway stations.

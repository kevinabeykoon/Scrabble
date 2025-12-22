# Scrabble Game

## Overview
This project is a GUI based Scrabble game implemented in Java using the Swing Library and follows the MVC pattern.  
It demonstrates object-oriented programming principles including encapsulation, modularity, and composition.  
The system allows multiple players to take turns placing words on a shared board, validates them using a dictionary, and calculates scores based on Scrabble letter values.

The repository includes the following deliverables:
1. Source Code  
2. UML Diagrams  
3. Documentation (Design Decisions and Javadoc)  
4. User Manual  

---
## Demo  
Vid to come

## 1. Source Code

### Class Descriptions

**Board**  
- Represents the 15x15 Scrabble board using a two-dimensional `char[][]` array.  
- Handles word placement, collision checking, and validation using the dictionary.  
- Stores a list of valid words to calculate the turn score.

**Scoring**  
- Uses a static `HashMap<Character, Integer>` to store letter point values.  
- Provides methods to calculate individual letter and total word scores.

**WordDictionary**  
- Loads valid words from a text file into a `HashSet<String>` for O(1) lookup.  
- Validates if a given word exists in the dictionary.

**Player**  
- Stores the player's name and cumulative score.  
- Handles turn logic and interacts with the board to place words.

**Game** (legacy, replaced by ScrabbleModel)
- Manages overall gameplay: player turns, score updates, and game termination.  
- Initializes the board, dictionary, and player objects.

**BoardPanel** 
- Displays the 15x15 Scrabble board as a grid of `JButton`s.
- Each button represents a board square and supports tile placement.
- Highlights selected squares in cyan when a player chooses them for word placement.
- Provides `getTileButtonGrid()` to allow the controller to map buttons to coordinates.

**RackPanel**
- Displays the current player’s 7-tile rack using `JButton`s.
- Updates tile text via `updateRack(Tile[] hand)` to reflect the player’s current hand.
- Supports tile selection with yellow highlighting.
- Returns the rack button array via `getTileRack()` for controller identification.

**ScorePanel**
- Shows each player’s name and current cumulative score.
- Updates individual scores through `setScore(String playerName, int score)`.
- Arranged in a vertical layout within the north region of the main frame.

**ControlPanel**
- Contains the three main action buttons: **Place Word**, **Swap**, and **Pass**.
- Each button is wired to the `ScrabbleController` via `ActionListener`.
- Positioned on the east side of the main game window.

**ScrabbleModel**
- Central model class managing all game state and logic.
- Initializes the `Board`, `TileBag`, `WordDictionary`, and `Player` objects.
- Handles turn progression with `nextPlayer()` and `passTurn()`.
- Processes tile swaps via `swapTiles()` and word placement via `placeWordOnBoard()`.
- Fires `ScrabbleEvent` objects to notify the view of state changes.

**ScrabbleController**
- Implements `ActionListener` to handle all user interactions.
- Identifies whether a clicked button belongs to the rack or board.
- Toggles selection states (yellow for rack, cyan for board).
- Delegates game actions (`swapTiles`, `passTurn`, `placeWordOnBoard`) to `ScrabbleModel`.

**ScrabbleEvent**
- Custom event class extending `EventObject` for MVC communication.
- Defines event types: `PASS_TURN`, `SWAP_TILES`, `TILE_PLACEMENT`, `UNSUCCESSFUL_TILE_PLACEMENT`, `GAME_OVER`.
- Carries relevant data (player, next player, message, coordinates, score, etc.) to the view.

**ScrabbleViewFrame**
- Main `JFrame` implementing the `ScrabbleView` interface.
- Assembles all panels (`BoardPanel`, `RackPanel`, `ScorePanel`, `ControlPanel`) using `BorderLayout`.
- Prompts user for number of players and names at startup.
- Updates UI in response to `ScrabbleEvent` via `handleScrabbleUpdate()`.
- Displays messages using `JOptionPane` and updates the current player label.

**ScrabbleView**
- Interface defining the contract for view components.
- Requires implementation of `handleScrabbleUpdate(ScrabbleEvent e)` and `startGame(Tile[] hand)`.

**TileBag**
- Manages the pool of 100 Scrabble tiles with correct letter distribution and point values.
- Supports drawing tiles via `pickFromBag()` and returning tiles via `returnTileToBag(Tile t)`.
- Tracks remaining tile count with `size()`.

**Tile**
- Represents a single Scrabble tile with a letter (`char`) and point value (`int`).
- Includes special handling for blank tiles (`'?'`).
- Provides `toString()` for display (e.g., `"A"`, `"?"`).

---

## 2. UML Diagrams

The UML documentation illustrates the structure and behavior of the system.

**Included Diagrams:**
- **Class Diagram:** Shows relationships between `Board`, `Game`, `Player`, `Scoring`, and `WordDictionary`.  
- **Sequence Diagram:** Demonstrates the flow of important scenarios  


## 3. Documentation (further explained in documentation document)

### Design Decisions
- **Board:** Implemented as a 2D `char[][]` array for efficient random access and easy visualization of positions.  
- **WordDictionary:** Implemented using a `HashSet` for O(1) lookups when validating words.  
- **Scoring:** Implemented using a static `HashMap` for fast and centralized letter value access.  
- **Player and Game:** Use simple data types and lists for clarity and modularity.

### Javadoc Documentation
Each class and method contains complete Javadoc comments describing:
- Functionality  
- Parameters and return values  

### User Manual

#### How to Play
1. Start by running the **ScrabbleModel** Class.  
2. Enter the amount of players and their names.  
3. Now each player can play their turn, interacting with the buttons on the **control panel**, by:   
   a. **Place Word:** select the letters on the rack in the order you want to place them on the board, then select the tiles on the board you'd like to place them on. Make sure to also select the tiles on the board in the same order. Note, if its the first turn of the game, the middle square must be included. Finally press the place word button to confirm your word.    
    
   b. **Swap:** Select the tile you want to swap and then click the Swap button.    
   c. **Pass:** Simply click the Pass button  
   
4. Read scores that are displayed on the **score panel** to see who's winning or has won

### Rules of Scrabble

The objective is to score the most points by creating words on the board using given letter tiles.

#### Gameplay
Each player is given **7 letter tiles**. 

1. The **first player** forms a word that crosses the **center board tile**.  
2. Players then **add new words** connected to existing ones — like a crossword.
3. First play must utilize the centre square and be at least two letters long.
4. After playing, **draw tiles** to return to 7 tiles in your rack.  
5. Words must appear in a **standard dictionary**. The dictionary used in this game has been defined in "ValidWords.txt". 

#### Scoring
- The program adds up all the played **letter values**. Each letter has an associated point value.  

|  Value | Letters                      |
| :----: | :--------------------------- |
|  **1** | A, E, I, L, N, O, R, S, T, U |
|  **2** | D, G                         |
|  **3** | B, C, M, P                   |
|  **4** | F, H, V, W, Y                |
|  **5** | K                            |
|  **8** | J, X                         |
| **10** | Q, Z                         |


#### Game End
The game ends when:
- All tiles are used and no moves remain, **or**
- All players **pass twice in a row**.
- Players choose to end the game

# Authors
Created for a software engineering course at Carleton University.

Alex Rusu  
Kevin Abeykoon    
Selvia Osahon   
Casey Ramanampanoharana 

*Note: This game is based off of Scrabble, we do not claim any intellectual rights to this game idea. Although all work was orginial, created by this team.*


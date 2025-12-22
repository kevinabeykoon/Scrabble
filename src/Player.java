import java.util.*;
import java.io.Serializable;
import javax.swing.JButton;

/**
 * Represents a player in the Scrabble game
 *
 * Each player has a name, a cumulative score, and keeps track of the words placed during the current turn. The Player Class handles user
 * input for word placement and update the board and score accordingly
 *
 * @version 27/10/2025
 **/
public class Player implements Serializable
{
    final private String name;
    private int score;
    private List<String> wordsThisTurn;
    private Tile[] availableTiles;
    int passCounter;

    private static final int HAND_SIZE = 7;

    /**
     * Constructs a new Player with the specified name.
     *
     * @param name the name of the player
     **/
    public Player(String name)
    {
        this.name = name;
        this.score = 0;
        this.wordsThisTurn = new ArrayList<>();
        this.availableTiles = new Tile[HAND_SIZE];
        this.passCounter = 0;
    }

    /**
     * Returns thet iles currently in the player's rack
     * @return an array containing the player's available tiles
     */
    public Tile[] getAvailableTiles()
    {
        /**List<Tile> notNullTiles = new ArrayList<>();

        for (int i = 0 ; i < availableTiles.length ; i++)
        {
            if  (availableTiles[i] != null){
                notNullTiles.add(this.availableTiles[i]);
            }
        }

        return notNullTiles.toArray(new Tile[0]);*/

        return availableTiles;
    }

    /**
     * Get a tile from a rack button
     * @param tilesToUse
     * @return the corresponding tile
     */
    public Tile getSelectedTileFromButton(JButton button) 
    {
    char letter = button.getText().charAt(0);  // get letter displayed on button
    
    for (Tile t : availableTiles) 
        {
        if (t != null && t.getEffectiveLetter() == letter) {
            return t;
        }
    }
    return null;
}

    /**
     * Removes the specified tiles from the player's rack
     * @param tilesUsed an array of tiles that were played and must be removed
     */
    public void useTile(Tile[] tilesToUse) 
    {
    for (Tile tile : tilesToUse) 
        {
        if (tile == null) { continue;} 

        for (int i = 0; i < availableTiles.length; i++) 
            {
            if (availableTiles[i] == tile) {  // object equality
                availableTiles[i] = null;
                break;
            }
        }
    }
}


    /**
     * Getter for pass counter if player passes more than 2 times = game over
     * @return count of pass
     */
    public int getPassCounter() {
        return passCounter;
    }

    /**
     * Passes has been done by player
     */
    public void addToPassCounter() {
        passCounter++;
    }

    /**
     * Reset passCounter move has been done
     */
    public void resetPassCounter() {
        passCounter = 0;
    }
    /**
     * Returns the player's name
     *
     * @return the name of the player
     **/
    public String getName(){ return name; }

    /**
     * Returns the player's cumulative score
     *
     * @return the score of the player
     **/
    public int getScore() {return score;}
    /**
     * Adds points to the player's cumulative score
     *
     * @param points the number of points to add
     **/
    public void addScore(int points) {score += points;}

    /**
     * Sets the player's score to a given value
     * @param points
     */
    public void setScore(int points) {this.score = points;}

    /**
     * Attempts to add a tile to the player's rack in the first available empty slot
     * @param tile the tile to add
     * @return true if the tile was added, false if the rack is full
     */
    public boolean setAvailableTile(Tile tile){
        for (int i = 0; i < availableTiles.length; i++){
            if(availableTiles[i] == null){
                availableTiles[i] = tile;
                return true;
            }
        }
        return false;
    }

    /**
     * Updates all of a player's tiles after a redo/undo
     * @param tiles the player's hand
     */
    public void setAvailableTiles(Tile[] tiles)
    {
        availableTiles = tiles;
    }

    /**
    * Sets a tile at the given rack index, replacing any tile already there
    * @param index the position in the rack to place the tile
    * @param tile the tile to place
    */
     public void setAvailableTileAt(int index, Tile tile) 
     {
        if (index >= 0 && index < availableTiles.length) 
        {
            availableTiles[index] = tile;
        }
    }

    /**
     * Returns a tile that was in hand specified by letter
     * @param letter letter we want to return
     * @return Tile of the letter if in hand else null
     */
    public Tile getTile(char letter){
         for (int i = 0; i < availableTiles.length; i++) {
             if (availableTiles[i].getEffectiveLetter() == letter) {
                 return availableTiles[i];
             }
         }
         return null;
    }

    /**
    * Adds a tile to the player's rack
    * @param tile the tile to add
    * @return true if the tile was added, false otherwise
    */
    public boolean addTile(Tile tile){
        for (int i = 0; i < availableTiles.length; i++){
            if (availableTiles[i] == null){
                availableTiles[i] = tile;
                return true;
            }
        }
        return false;
    }

    /**
     * Builds a string representation of all non-null tiles on the player's rack
     *
     * @return a space-separated string of tile letters
     */
    public String displayAvailableTile() {
        StringBuilder sb = new StringBuilder();

        for (Tile availableTile : availableTiles) {
            if (availableTile == null) {
                continue;
            }
            sb.append(availableTile.toString());
            sb.append(" ");
        }
        return sb.toString();
    }


    public void playTurn(Board board, Scanner scanner, TileBag tileBag) {
        boolean played = false;

        System.out.println(name + " available Tiles: " + this.displayAvailableTile() + "\n");

        while (!played) {
            System.out.println("Pick an action for this turn: PLACE, SWAP, or SKIP");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "PLACE": {
                    int turnScore = this.placeWordOnBoard(board, scanner, tileBag);
                    if (turnScore > 0) {
                        System.out.println("Word placed successfully! You earned " + turnScore + " points.\n");
                        played = true;
                    } else {
                        System.out.println("Word placement failed or invalid. Try again or choose another action.\n");
                    }
                    break;
                }

                case "SWAP": {
                    boolean swapped = this.swapTiles(scanner, tileBag);
                    if (swapped) {
                        System.out.println("Tiles swapped successfully.\n");
                        played = true;
                    } else {
                        System.out.println("Swap failed or canceled. Try again.\n");
                    }
                    break;
                }

                case "SKIP":
                    System.out.println("Skipping " + name + "'s turn.\n");
                    played = true;
                    break;

                default:
                    System.out.println("Invalid input. Please enter PLACE, SWAP, or SKIP.\n");
                    break;
            }
        }
    }

    /**
     * @param scanner
     * @param tileBag
     */

    public boolean swapTiles(Scanner scanner, TileBag tileBag) {
        System.out.println("Choose lettersToSwap to swap: (seperated by ,)");

        String response = scanner.nextLine().toUpperCase();

        String[] lettersToSwap = response.split(",");

        if (lettersToSwap.length > tileBag.size()) {
            System.out.println("There are not enough tiles in the bag\n");
            return false;
        }

        if (lettersToSwap.length > availableTiles.length) {
            System.out.println("You cant swap more than what you have\n");
            return false;
        }

        for (String letterToSwap : lettersToSwap) {
            boolean found = false;
            for (Tile availableTile : availableTiles) {
                if (availableTile.getEffectiveLetter() == letterToSwap.charAt(0)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("You cant swap what you dont have >:( :" + letterToSwap);
                return false;
            }
        }

        for (String letterToSwap : lettersToSwap) {
            for (int i = 0; i < availableTiles.length; i++) {
                if (availableTiles[i].getEffectiveLetter() == letterToSwap.charAt(0)) {
                    Tile temp = availableTiles[i];
                    availableTiles[i] = tileBag.pickFromBag();
                    tileBag.returnTileToBag(temp);

                    break;
                }
            }
        }

        System.out.println("Your available letters are now: " + displayAvailableTile()+"\n");

        return true;
    }

    /**
     * Allows the player to take a turn by place a word on the board.
     * The method prompts the user for input in the following format:
     * WORD ROW COL H/V
     * where:
     *    WORD is the word to place
     *    ROW is the starting row index
     *    COL is the starting column index
     *    H/V is the direction: 'H' for horizontal, 'V' for vertical
     * If the placement is valid, the word is added to the board and the player's score is update based on the Scoring
     * Invalid input or placement will result in an error message and the turn is not scored.
     *
     * @param board the game board where the word should be placed
     * @param scanner a Scanner object for reading user input
     *
     **/

    public int placeWordOnBoard(Board board,Scanner scanner, TileBag tileBag){
        wordsThisTurn.clear();

        System.out.println(name + ", enter your move (format: WORD ROWS COLS): ");
        //EXAMPLE INPUT: "(WORD) A,E,F,G (ROWS) 3,3,3,3 (COLS) 2,5,6,7"
        //WORD ROWS AND COL ARE SEPERATED BY A SPACE
        //A is put at spot (3,2), E at (3,5), F at (3,6), G at (3,7)
        
        String input = scanner.nextLine();
        String[] parts = input.split(" ");

        if (parts.length != 3)
        {
            System.out.println("Invalid input format.");
        }

        String word = parts[0].toUpperCase();
        String[] rowParts = parts[1].split(",");
        String[] colParts = parts[2].split(",");
       

        if (rowParts.length != word.length() || colParts.length != word.length()) 
        {
            System.out.println("\nNumber of coordinates does not match the number of letters in the word.");
            return 0; 
        }

        int length = word.length();
        int[] rows = new int[length];
        int[] cols = new int[length];
        char[] tiles = new char[length];

        for (int i = 0; i < length; i++) {
            tiles[i] = word.charAt(i);

            rows[i] = Integer.parseInt(rowParts[i].trim());  //Gets character and converts it to an int
            cols[i] = Integer.parseInt(colParts[i].trim());
        }

        Tile[] tilesUsedFromRack = new Tile[tiles.length];

        for (int i = 0; i < tiles.length; i++) 
        {
            Tile t = null;
            for (Tile tile : availableTiles) 
                {
                if (tile != null && tile.getEffectiveLetter() == tiles[i]) 
                {
                    t = tile;
                    break;
                }
            }
            tilesUsedFromRack[i] = t;
        }

        int turnScore = board.placeTilesAndValidate(rows, cols, tilesUsedFromRack);

        if(turnScore > 0)
        {
            wordsThisTurn.add(word);
            addScore(turnScore);

            System.out.println("Word placed! Score for this turn: " + turnScore);
            return turnScore;

        }
        else
        {
            System.out.println("Word placement failed. Try again.");
            return -1;
        }

    }

    /**
    * Replaces the tile at the specified index with a new tile
    * @param index the rack position to update
    * @param newTile the new tile to place in the rack
    */
    public void replaceTileAt(int index, Tile newTile)
    {
        if (index >= 0 && index < availableTiles.length)
        {
            availableTiles[index] = newTile;
        }
    }

    /**
     * Represent name
     */
    @Override
    public String toString()
    { return name +", Score "+ score;}

}

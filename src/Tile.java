import java.util.*;
import java.io.Serializable;

/**
 * Tile class -> tiles that will be placed on the board i.e the game pieces to make the words
 * Each Letter hold a value as set by the rules (Wiki)
 *
 */


public class Tile implements Serializable {
    final private char Letter;
    final private int Value;
    private boolean isBlank;
    private char assignedLetter = ' ';

    private static final Map<Character, Tile> ALL_TILES = new HashMap<>();

    static {
        ALL_TILES.put('A', new Tile('A', 1));
        ALL_TILES.put('B', new Tile('B', 3));
        ALL_TILES.put('C', new Tile('C', 3));
        ALL_TILES.put('D', new Tile('D', 2));
        ALL_TILES.put('E', new Tile('E', 1));
        ALL_TILES.put('F', new Tile('F', 4));
        ALL_TILES.put('G', new Tile('G', 2));
        ALL_TILES.put('H', new Tile('H', 4));
        ALL_TILES.put('I', new Tile('I', 1));
        ALL_TILES.put('J', new Tile('J', 8));
        ALL_TILES.put('K', new Tile('K', 5));
        ALL_TILES.put('L', new Tile('L', 1));
        ALL_TILES.put('M', new Tile('M', 3));
        ALL_TILES.put('N', new Tile('N', 1));
        ALL_TILES.put('O', new Tile('O', 1));
        ALL_TILES.put('P', new Tile('P', 3));
        ALL_TILES.put('Q', new Tile('Q', 10));
        ALL_TILES.put('R', new Tile('R', 1));
        ALL_TILES.put('S', new Tile('S', 1));
        ALL_TILES.put('T', new Tile('T', 1));
        ALL_TILES.put('U', new Tile('U', 1));
        ALL_TILES.put('V', new Tile('V', 4));
        ALL_TILES.put('W', new Tile('W', 4));
        ALL_TILES.put('X', new Tile('X', 8));
        ALL_TILES.put('Y', new Tile('Y', 4));
        ALL_TILES.put('Z', new Tile('Z', 10));
    }

    /**
     * Tile made up of letter, value, and if it is a blank tile
     * @param letter, letter that will be used to make words
     * @param value, the value of the points of the given tile
     * @param isBlank, whether the tile is a blank tile
     */

    Tile(char letter, int value, boolean isBlank) 
    {
        Letter = letter;
        this.Value = isBlank? 0 : value;
        this.isBlank = isBlank;
        this.assignedLetter = isBlank ? ' ' : letter;
    }

    
    /**
     * Tile made up of letter, value
     * @param letter, letter that will be used to make words
     * @param value, the value of the points of the given tile
     *
     */
    public Tile(char letter, int value) 
    {
        this.Letter = Character.toUpperCase(letter);
        this.Value = value;
        this.isBlank = false;
    }

    public static Tile createLetter(char c, int value)
    {
        return new Tile(c, value, false);
    }

    public static Tile createBlank()
    {
        return new Tile(' ', 0, true);
    }

    /**
     * Get corresponding tile given a letter
     * @param letter to be made into a tile
     * @return corresponding tile
     */

    public static Tile getTile(char letter){
        return ALL_TILES.get(letter);
    }

    /**
     * Getter of the letter on the tile
     * @return letter on the tile
     */
    public char getEffectiveLetter() 
    {
        return isBlank ? assignedLetter : Letter;
    }

    /**
     * Getter of the type of tile (blank or not)
     * @return whether the tile is blank
     */
    public boolean isBlank()
    {
        return isBlank;
    }

    /**
     * Setter of the chosen letter for the blank tile
     * @param chosenLetter letter
     */
    public void setAssignedLetter(char chosenLetter)
    {
        if (isBlank)
        {
            this.assignedLetter = Character.toUpperCase(chosenLetter);
        }
    }

    /**
    * Getter of the assigned letter for the blank tile
    * @return the assigned letter 
    */
    public char getAssignedLetter()
    {
        return assignedLetter;
    }



    /**
     * Getter of values
     * @return value of the letter
     */
    public int getValue() {
        return Value;
    }

    /**
     * String representatino of Tile
     * @return string representation of letter in uppercase
     */
    @Override
    public String toString() {
        return "" + Letter;
    }

    private Object readResolve() {
        if (isBlank) {
            Tile blank = Tile.createBlank();
            blank.setAssignedLetter(this.assignedLetter);
            return blank;
        }
        return ALL_TILES.get(this.Letter);
    }
}

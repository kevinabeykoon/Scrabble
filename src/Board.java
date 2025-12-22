import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;


import javax.swing.JButton;

public class Board  implements Serializable{
    /**
     * This class represents the board for the Scrabble Game.
     * It validates the tiles placed down and calculates their score.
     * It is also in charge of printing the board/game to the terminal
     */

    private static class WordScore{
        String word;
        int score;
        WordScore(String word, int score){
            this.word = word;
            this.score = score;
        }
    }

    //deals with word placement and validation, along with printing the board
    private char[][] scrabbleBoard;   //2D array representing the board
    protected static final int SIZE = 15;
    private WordDictionary dictionary;  //the scrabble board has its own dictionary of valid words
    private List<WordScore> validWordsWithScores; //the list of valid words to calculate the turn score
    private int[][] tileLetterMultiplier;
    private int[][] tileWordMultiplier;
    private List<Integer> letterMultipliers;
    private static final long serialVersionUID = 1L;

    /**
     * Constructor creates the Scrabble board and creates + sets the dictionary that contains
     * all legal words for the game.
     *
     * @param dictionary The dictionary of valid words that can be used
     */
    public Board(WordDictionary dictionary){
        scrabbleBoard = new char[SIZE][SIZE];
        tileLetterMultiplier = new int[SIZE][SIZE];
        tileWordMultiplier = new int[SIZE][SIZE];
        letterMultipliers = new ArrayList<>();

        validWordsWithScores = new ArrayList<>();
        this.dictionary = dictionary;

        // Loop below fills each row with an empty space
        for (int i = 0; i < SIZE; i++){
            Arrays.fill(scrabbleBoard[i], ' ');

            for (int j = 0; j < SIZE; j++){
                tileLetterMultiplier[i][j] = 1; //initialize tile multiplier with base multiplier for all
                tileWordMultiplier[i][j] = 1;
            }
        }

        // Two Point Letter Multipliers - LIGHT BLUE ON THE BOARD GUI
        tileLetterMultiplier[0][3] = 2;
        tileLetterMultiplier[0][11] = 2;
        tileLetterMultiplier[2][6] = 2;
        tileLetterMultiplier[2][8] = 2;
        tileLetterMultiplier[3][0] = 2;
        tileLetterMultiplier[3][7] = 2;
        tileLetterMultiplier[3][14] = 2;
        tileLetterMultiplier[6][2] = 2;
        tileLetterMultiplier[6][6] = 2;
        tileLetterMultiplier[6][8] = 2;
        tileLetterMultiplier[6][12] = 2;
        tileLetterMultiplier[7][3] = 2;
        tileLetterMultiplier[7][11] = 2;
        tileLetterMultiplier[8][2] = 2;
        tileLetterMultiplier[8][6] = 2;
        tileLetterMultiplier[8][8] = 2;
        tileLetterMultiplier[8][12] = 2;
        tileLetterMultiplier[11][0] = 2;
        tileLetterMultiplier[11][7] = 2;
        tileLetterMultiplier[11][14] = 2;
        tileLetterMultiplier[12][6] = 2;
        tileLetterMultiplier[12][8] = 2;
        tileLetterMultiplier[14][3] = 2;
        tileLetterMultiplier[14][11] = 2;

        //Three Point Letter Multipliers - DARK BLUE ON THE GUI
        tileLetterMultiplier[1][5] = 3;
        tileLetterMultiplier[1][9] = 3;
        tileLetterMultiplier[5][1] = 3;
        tileLetterMultiplier[5][5] = 3;
        tileLetterMultiplier[5][9] = 3;
        tileLetterMultiplier[5][13] = 3;
        tileLetterMultiplier[9][1] = 3;
        tileLetterMultiplier[9][5] = 3;
        tileLetterMultiplier[9][9] = 3;
        tileLetterMultiplier[9][13] = 3;
        tileLetterMultiplier[13][5] = 3;
        tileLetterMultiplier[13][9] = 3;

        //Two Point Word Multipliers - PINK ON THE BOARD GUI
        tileWordMultiplier[1][1] = 2;
        tileWordMultiplier[1][13] = 2;
        tileWordMultiplier[2][2] = 2;
        tileWordMultiplier[2][12] = 2;
        tileWordMultiplier[3][3] = 2;
        tileWordMultiplier[3][11] = 2;
        tileWordMultiplier[4][4] = 2;
        tileWordMultiplier[4][10] = 2;
        tileWordMultiplier[7][7] = 2;
        tileWordMultiplier[10][4] = 2;
        tileWordMultiplier[10][10] = 2;
        tileWordMultiplier[11][3] = 2;
        tileWordMultiplier[11][11] = 2;
        tileWordMultiplier[12][2] = 2;
        tileWordMultiplier[12][12] = 2;
        tileWordMultiplier[13][1] = 2;
        tileWordMultiplier[13][13] = 2;

        //Three Point Word Multipliers - RED ON THE BOARD GUI
        tileWordMultiplier[0][0] = 3;
        tileWordMultiplier[0][7] = 3;
        tileWordMultiplier[0][14] = 3;
        tileWordMultiplier[7][0] = 3;
        tileWordMultiplier[7][14] = 3;
        tileWordMultiplier[14][0] = 3;
        tileWordMultiplier[14][7] = 3;
        tileWordMultiplier[14][14] = 3;
    }

    /**
     * Displays the current game board to the terminal.
     * Should be called by the Game class.
     */
    public void display(){
        System.out.println("     0   1   2   3   4   5   6   7   8   9  10   11  12  13  14  ");
        System.out.println("----------------------------------------------------------------");
        for(int i = 0; i < SIZE ; i++){
            System.out.print((i < 10 ? " " : "") + i + " | ");    //prints column numbers
            for(int j = 0; j < SIZE ; j++){
                System.out.print(scrabbleBoard[i][j] + " | "); // prints out rows
            }
            System.out.println("\n----------------------------------------------------------------");
        }
        System.out.println();
    }

   
 



    /**
     * Places the tiles that the player used in their respective squares and then validates
     * the placements.
     * If placements are invalid in any way, the tiles are removed from the board and a score
     * of 0 is returned.
     * A placement is invalid if:
     *      The word created is not found in the dictionary of words (This goes for every word made)
     *      The tiles played are not adjacent to an existing word (at least one tile must be touching
     *      one already on the board)
     *      The first play does not include the center square or is not at least 2 letters long
     *
     * @param rows An array of what rows the player placed their tiles into
     * @param cols  An array of what columns the player placed their tiles into
     * @param tilesUsedFromRack An array of what Letters/Tiles were played
     * @return The total score for the turn
     *
     * Should be called by the Game Class.
     */
    public int placeTilesAndValidate(int[] rows, int[] cols, Tile[] tilesUsedFromRack){
        if (rows.length != cols.length || rows.length != tilesUsedFromRack.length){
            System.out.println("Number of coordinates does not match the number of letters in the word. Please try again");
        }

        //First play must include the center square
        if (isFirstPlay()){
            boolean centerSquareUsed = false;
            if(rows.length < 2){
                System.out.println("Invalid play. First play must be at least 2 letters long. Please try again.");
            }
            
            for (int i = 0; i < rows.length; i++){
                if(rows[i] == 7 && cols[i] == 7){
                    centerSquareUsed = true;
                    break;
                }
            }

            if (!centerSquareUsed){
                System.out.println("Invalid play. First play must include the center square. Please try again.");
                return 0;
            }
        }

        //All played tiles must be in the same column or row, only becomes an issue if there are 2+ tiles played
        if (rows.length >= 2){
            for (int i = 0; i < (rows.length - 1); i++){
                if ((rows[i] != rows[i+1]) && (cols[i] != cols[i+1])){
                    System.out.println("Invalid play. All tiles played must be in the same row or column. Please try again.");
                    return 0;
                }
            }
        }

        //Temporarily place the tiles
        for (int i = 0; i < tilesUsedFromRack.length; i++)
        {
            if (tilesUsedFromRack[i] != null)
            {
                scrabbleBoard[rows[i]][cols[i]] = tilesUsedFromRack[i].getEffectiveLetter();
            }
        }

        // Check if all tiles placed are adjacent to existing words
        if (!checkAdjacency(rows, cols)){
            System.out.println("Invalid play. Tiles must be adjacent to existing words. All placed tiles removed. Please try again.");
            removeTiles(rows, cols);
            return 0;
        }

        // Validate all words formed by tile placements
        if(!validateWords(rows, cols))
            {  //future method
            removeTiles(rows, cols);
            letterMultipliers.clear();
            return 0;
        }

        int score = getTurnScore(rows, cols);
        return score;
    }

    /**
     * Checks if the current play is the first play of the game
     * @return True, if the centre square is empty
     */
    private boolean isFirstPlay(){
        return scrabbleBoard[7][7] == ' ';
    }

    /**
     * Getter for the char[][] board
     * @return the char[][] board
     */
    public char[][] getCharBoard()
    {
        return scrabbleBoard;
    }

    /**
     * setter for the char[][] board
     * @param newBoard the char[][] board to replace/update the current one
     */
    public void setCharBoard(char[][] newBoard)
    {
        this.scrabbleBoard = newBoard;
    }

    /**
     * Checks if all tiles played are adjacent (touching) at least one other.
     *
     * @param rows An array of what rows were played by the user.
     * @param cols An array of what columns were used by each user.
     * @return True is all tiles are adjacent to at least one other, False otherwise.
     */
    public boolean checkAdjacency(int[] rows, int[] cols){
        boolean adjacent = false;

        for(int i = 0; i < rows.length; i++){
            int row = rows[i];
            int col = cols[i];

            //Check the square above
            if(row > 0 && scrabbleBoard[row - 1][col] != ' ') adjacent = true;
            // Checks the square below
            if(row < SIZE - 1 && scrabbleBoard[row + 1][col] != ' ') adjacent = true;
            //Checks the square to the left
            if(col > 0 && scrabbleBoard[row][col - 1] != ' ') adjacent = true;
            //Check the square to the right
            if(col < SIZE - 1 && scrabbleBoard[row][col + 1] != ' ') adjacent = true;
        }
        return adjacent;
    }

    /**
     * Checks if all words created by played tiles, horizontal and vertical, are valid
     *
     * @param rows Array of what rows were used by the player
     * @param cols Array of what columns were used by the player
     * @return True if all words created are valid, false if at least one is invalid in any way
     */
    public boolean validateWords(int[] rows, int[] cols){
        boolean allWordsValid = true;
        validWordsWithScores.clear();

        // Check horizontal and vertical words formed by new tiles
        for (int i = 0; i < rows.length; i++) {
            if (cols[i] == cols[0]) {
                extractWordAndCalculateScore(rows[i], cols[i], false, rows, cols);
            }
            // validate the horizontal words
            if (rows[i] == rows[0]) {
                extractWordAndCalculateScore(rows[i], cols[i], true, rows, cols);
            }
        }


        // Now validate all words
        for (WordScore ws : validWordsWithScores) {
            if(!dictionary.isValidWord(ws.word)){
                System.out.println("Invalid word: " + ws.word + ". All placed tiles removed. Please try again.");
                allWordsValid = false;
            }
        }
        return allWordsValid;
    }



    /**
     * Extracts the longest word created in the row or column and places it into a string.
     *
     * @param startRow A square containing a letter in the word
     * @param startCol A square containing a letter in the word
     * @param horizontal True if the word is horizontal, false if vertical
     * @return String of the word created in the given direction (horizontal or vertical)
     */
    public String extractWordAndCalculateScore(int startRow, int startCol, boolean horizontal, int[] placedRows, int[] placedCols){
        StringBuilder word = new StringBuilder();
        int wordScore = 0;
        int row = startRow;
        int col =startCol;

        //moves backwards to the start of the word (we end up at the very first empty space before the word)
        while(row >= 0 && col >= 0 && scrabbleBoard[row][col] != ' '){
            if(horizontal){
                col--;
            } else{
                row--;
            }
        }

        //moves us to the square with a letter
        if (horizontal) col++; else row++;

        //builds the word by moving forward and tracking multipliers
        while(row < SIZE && col < SIZE && scrabbleBoard[row][col] != ' '){
            word.append(scrabbleBoard[row][col]);

            boolean isNewlyPlaced = false;
            for (int i = 0; i < placedRows.length; i++){
                if (placedRows[i] == row && placedCols[i] == col){
                    isNewlyPlaced = true;
                    break;
                }
            }

            //Calculate word score immediately
            int letterValue = Scoring.getLetterValue(scrabbleBoard[row][col]);
            if (isNewlyPlaced){
                letterValue *= tileLetterMultiplier[row][col];
            }

            wordScore += letterValue;

            if(horizontal){
                col++;
            }
            else{
                row++;
            }
        }

        if (word.length() > 1){
            validWordsWithScores.add(new WordScore(word.toString(), wordScore));
        }

        return word.toString();
    }

    /**
     * Removes all played tiles.
     *
     * @param rows Rows of the tiles played
     * @param cols Columns of the tiles played
     */
    public void removeTiles(int[] rows, int[] cols){
        for(int i = 0; i < rows.length; i++){
            scrabbleBoard[rows[i]][cols[i]] = ' ';
        }
    }

    /**
     * Calculates turn score based on the words created.
     *
     * @return Total score as an integer value
     */
    public int getTurnScore(int[] rows, int[] cols){
        int score = 0;

        // Sum up all word scores
        for (WordScore ws : validWordsWithScores) {
            score += ws.score;
        }

        // Apply word multipliers to the total
        int totalWordMultiplier = 1;
        for(int i = 0; i < rows.length; i++){
            totalWordMultiplier *= tileWordMultiplier[rows[i]][cols[i]];
        }
        score *= totalWordMultiplier;

        validWordsWithScores.clear();
        return score;
    }

/**
 * Returns the letter at the specified cell.
 *
 * @param row The row index.
 * @param col The column index.
 * @return The letter at the specified cell or a blank space (' ') if out of bounds.
 */
    public char getLetterAt(int row, int col){
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE){
            return scrabbleBoard[row][col];
        }
        System.out.println("Row or column out of bounds: (" + row + ", " + col + ")");
        return ' ';
    }

    /**
     * Sets a letter at the specified position on the board.
     *
     * @param row  The row index on the board.
     * @param col  The column index on the board.
     * @param tile The tile (character) to place on the board.
     */
    public void setLetterAt(int row, int col, char tile){
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE){
            scrabbleBoard[row][col] = tile;
        } else {
            System.out.println("Row or column out of bounds: (" + row + ", " + col + ")");
        }
    }

    /**
     * Sets the multipliers
     * @param letterMultipliers the multipliers for the letter
     * @param wordMultipliers the multipliers for the whole word
     */
    public void setTileMultipliers(int[][] letterMultipliers, int[][] wordMultipliers){
        this.tileWordMultiplier = wordMultipliers;
        this.tileLetterMultiplier = letterMultipliers;
    }

    /**
     * Returns the letter multipliers
     * @return the letter multipliers
     */
    public int[][] getTileLetterMultiplier(){
        return tileLetterMultiplier;
    }

    /**
     * Returns the word multipliers
     * @return the word multipliers
     */
    public int[][] getTileWordMultiplier(){
        return tileWordMultiplier;
    }

    /**
     * Getter for wordDictionnary
     * @return the wordDictionnary
     */
    public WordDictionary getWordDictionary(){
        return dictionary;
    }

    /**
     * Setter for wordDictionnary
     * @param wordDictionary wordDictionnary
     */
    public void setWordDictionary(WordDictionary wordDictionary) {
        dictionary = wordDictionary;
    }

}

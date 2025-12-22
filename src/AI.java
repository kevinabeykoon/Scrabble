import java.util.*;
import java.awt.Point;
import java.io.Serializable;

/**
 * AI class is the against computer function of the game, it is in charge of making the computer move based on the current game status
 * @author Casey Ramanampanoharana, 101233513
 * @version 1.0
 */
public class AI extends Player implements BoardObserver, Serializable {
    //keeps coordinates of where we start placing a new word --> either in center or is adjacent to a existing word
    static Set<Point> legalPlacements = new HashSet<Point>();
    //keeps the actual board state in map for easy lookup
    static Map<Point, Character> board = new HashMap<Point, Character>();
    static WordDictionary dictionary;
    boolean shouldPlace;

    public static class PlaceWord {
        int[] rows;
        int[] cols;
        Tile[] tilesNeeded;
        int score;
        char[] word;

        public PlaceWord(int[] rows, int[] cols, Tile[] tilesNeeded, int score, char[] word) {
            this.rows = rows;
            this.cols = cols;
            this.tilesNeeded = tilesNeeded;
            this.score = score;
            this.word = word;
        }
    }

    /**
     * Constructor for making new Ai player, all AI player will have a tag to show it is AI
     * @param name of the Ai player Suffix tag [AI]
     */
    public AI(String name) {
        super(name + " [AI]");
        shouldPlace = true;
    }

    /**
     * Toggle Should swap so AI alternates between swapping and placing words
     */
    public void togglePlace() {
        shouldPlace = !shouldPlace;
    }

    /**
     * Called at start of game for the first move needing to be in the center
     * clearing to remove any old data because it is static
     */
    public static void setUp(WordDictionary wordDictionary) {
        legalPlacements.clear();
        board.clear();
        dictionary = wordDictionary;
        legalPlacements.add(new Point(7,7));
    }

    /**
     * Updates both legalPlacements and board with the new word added to the game board
     * @param rows of each letter placed
     * @param cols of each letter placed
     * @param sortedTiles the letter placed
     */
    @Override
    public void boardChanged(int[] rows, int[] cols, char[] sortedTiles) {
        for (int i = 0; i < sortedTiles.length; i++) {
            Point position = new Point(rows[i], cols[i]);

            //add new word to board (letter by letter)
            board.put(position, sortedTiles[i]);

            //four adjacent places surrounding this letter
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] place : directions) {
                int x = rows[i] + place[0];
                int y = cols[i] + place[1];

                //only add to legalPlacement if its a actual coordinate on the board and if its not occupied by a word on the board
                if ((x >= 0 && x < 15 && y >= 0 && y < 15)  && !board.containsKey(new Point(x, y))) {
                    legalPlacements.add(new Point(x, y));
                }
            }
            //mark as occupied space now, remove it from legalPlacement
            legalPlacements.remove(position);

        }
    }

    /**
     * Returns a all the possible words that can be placed based on tiles in hand and on board
     * @return subset of dictionnary that ai can place on board returns null if nothing is found
     */
    public Set<String> findPossibleWord(){
        List<Character> availableLetters =  new ArrayList<>();
        Set<String> possibleWords = new HashSet<>();

        //NOTE: the ai cant use blank tiles... `\_('^')_/`
        for (Tile tile: this.getAvailableTiles()){
            if (!tile.isBlank()) {
                availableLetters.add(tile.getEffectiveLetter());
            }
        }

        availableLetters.addAll(board.values());

        for (String word : dictionary.getWords()) {
            if (this.haveEnoughLetters(word,availableLetters)){
                possibleWords.add(word);
            }
        }
        return possibleWords.isEmpty()? null: possibleWords;
    }

    /**
     * helper functions to see if with avaialable letters we can make the word given
     * duplicates are counted for
     * @param word given to see if we have enough letters
     * @param availableLetters all letter avaialable
     * @return true is we do else false
     */
    private boolean haveEnoughLetters(String word, List<Character> availableLetters) {
        if (word.length() > availableLetters.size()){
            return false;
        }

        List<Character> temp = new ArrayList<>(availableLetters);

        //check for every letter in word if we have in available letters
        for (char letter: word.toCharArray()) {
            if (!temp.remove((Character)letter)) {
               return false; // ran out of letters or not in word
            }
        }
        return true;
    }

    /**
     * Find best scoring words by brute forcing subset of some words from dictionnary
     * @param board board of the game
     * @param possibleWords subset of possible words
     * @return type for command in model for validwordandplace
     */
    public PlaceWord findBestWord(Board board, Set<String> possibleWords) {
        if (possibleWords == null || possibleWords.isEmpty()) return null;
        int bestMove = 0;
        PlaceWord bestWord = null;

        for (Point position: legalPlacements){
            for (String word: possibleWords) {
                if (AI.board.isEmpty() && word.length() < 2) { continue;}
                for (int offset = -(word.length()-1); offset <= 0; offset++) {
                    int x = position.x + offset;
                    if (x >= 0 && x + word.length() <= 15) {
                        PlaceWord vertical = tryWordPlace(word, x, position.y, false, board);
                        if (vertical != null && vertical.score > bestMove){
                            bestWord = vertical;
                            bestMove = vertical.score;
                        }
                    }

                    int y =  position.y + offset;
                    if (y >= 0 && y + word.length() <= 15) {
                        PlaceWord horizontal = tryWordPlace(word, position.x, y, true, board);
                        if (horizontal != null && horizontal.score > bestMove){
                            bestWord = horizontal;;
                            bestMove = horizontal.score;
                        }
                    }
                }
            }
        }
        return bestWord;
    }

    /**
     * helper function to see if the word can be placed --> brute force
     *
     * @param word           were trying to place
     * @param row            starting point x
     * @param col            starting point y
     * @param horizontal     if horizontal placement = true else false
     * @return score given to placed word 0 if no word placed
     */
    private PlaceWord tryWordPlace(String word, int row, int col, boolean horizontal, Board board) {
        if (horizontal && col + word.length() > 15) return null;
        if (!horizontal && row + word.length() > 15) return null; //word placed outside board

        List<Integer> newRows =  new ArrayList<>();
        List<Integer> newCols =  new ArrayList<>();
        List<Tile> needed = new ArrayList<>();
        boolean canConnect = false;

        //checking if we can create from existing board letters
        for (int i = 0; i < word.length(); i++) {
            int currentRow = horizontal? row: row + i;
            int currentCol = horizontal? col+i: col;

            Point position = new Point(currentRow, currentCol);
            if (AI.board.containsKey(position)) {
                char letter = AI.board.get(position);
                if (letter != word.charAt(i)) {
                    return null;
                }
                canConnect = true;
            } else {
                if (board.getLetterAt(currentRow,currentCol) == ' ') {
                    Tile tile = this.getTile(word.charAt(i));
                    if (tile == null) {return null;}
                    needed.add(tile); // else we need the char from our hand
                    newRows.add(currentRow);
                    newCols.add(currentCol);
                }
            }
        }

        if ((!AI.board.isEmpty() && !canConnect)|| !this.hasNeeded(needed)){
            return null;
        }

        Board copy = this.copyBoard(board);

        int[] rows = newRows.stream().mapToInt(Integer::intValue).toArray();
        int[] cols = newCols.stream().mapToInt(Integer::intValue).toArray();

        int score = copy.placeTilesAndValidate(rows, cols, needed.toArray(new Tile[needed.size()]));

        return score == 0? null : new PlaceWord(rows,cols, needed.toArray(new Tile[needed.size()]), score, tileTochar(needed.toArray(new Tile[needed.size()])));
    }

    /**
     * Helper function Tile[] into Char[]
     */
    private char[] tileTochar(Tile[] tiles){
        char[] chars = new char[tiles.length];

        for (int i = 0; i < tiles.length; i++) {
            chars[i] = tiles[i].getEffectiveLetter();
        }

        return chars;
    }

    /**
     * Helper funciton to find if the needed tiles are in our actual hand
     * @param needed tiles we need for the word
     * @return true is we have else false
     */
    private boolean hasNeeded(List<Tile> needed){
        //need more tiles than whats in hand
        if (needed.size() > 7) {
            return false;
        }

        List<Tile> temp = new ArrayList<>(List.of(this.getAvailableTiles()));


        for (Tile tile: needed) {
            if (!temp.remove(tile)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper function to make copy of board to test placement of word and its score
     * @param ogBoard orginial model board
     * @return copy of board
     */
    private Board copyBoard(Board ogBoard){
        Board copy = new Board(ogBoard.getWordDictionary());

        for (Map.Entry<Point, Character> entry: board.entrySet()){
            Point position = entry.getKey();
            Character character = entry.getValue();
            copy.setLetterAt(position.x,position.y,character);
        }
        return copy;
    }

    /**
     * Represnt Ai player by name
     * @return string version of name
     */
    @Override
    public String toString() {
        return getName();
    }
}

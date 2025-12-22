import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.JButton;


/**
 * The ScrabbleModel class manages the core game logic for Scrabble.
 * It handles players, the tile bag, board interactions, and communication 
 * between the controller and view. This class maintains the current game state 
 * and processes player actions such as placing words, swapping tiles, and passing turns.
 */
public class ScrabbleModel implements Serializable
{
    private static final int HAND_SIZE = 7;

    public transient ScrabbleView view;
    public transient ScrabbleController controller;
    
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private Player currentPlayer;
    private Player lastPlayerWhoMoved;
    private TileBag tileBag;
    private transient List<BoardObserver> boardObservers;


    private Map<Player, Stack<MoveState>> undoStacks = new HashMap<>();
    private Map<Player, Stack<MoveState>> redoStacks = new HashMap<>();
    private static final long serialVersionUID = 1L;


    /**
     * Constructs a new ScrabbleModel with a board, an empty player list,
     * a tile bag, and initializes the current player index to zero.
     */
    public ScrabbleModel()
    {
        this.board = new Board(new WordDictionary("ValidWords.txt"));
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.tileBag = new TileBag();
        currentPlayer = null;
        boardObservers = new ArrayList<>();
    }

    public void setBoardLayout(BoardLayout layout) {
        board = new Board(new WordDictionary("ValidWords.txt"));
        board.setTileMultipliers(layout.getLetterMultipliers(), layout.getWordMultipliers());
    }

    /**
     * An inner class that is used to store turn states 
     */
    public static class MoveState implements Serializable {
    private int[] rows;       // rows of tiles placed in this move
    private int[] cols;       // columns of tiles placed
    private Tile[] tiles;     // tiles placed
    private Tile[] rackSnapshot;  // player's rack after move
    private int scoreSnapshot;    // player's score after move

    public MoveState(int[] rows, int[] cols, Tile[] tiles, Tile[] rackSnapshot, int scoreSnapshot) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = tiles;
        this.rackSnapshot = rackSnapshot;
        this.scoreSnapshot = scoreSnapshot;
    }

    public int[] getRows() { return rows; }
    public int[] getCols() { return cols; }
    public Tile[] getTiles() { return tiles; }
    public Tile[] getRackSnapshot() { return rackSnapshot; }
    public int getScoreSnapshot() { return scoreSnapshot; }
}









    /**
     *  Creates a copy of a given board
     * @param board the current board 
     * @return copy a copy of the board
     */
    private char[][] copyBoard(char[][] board) 
    {
        char[][] copy = new char[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) 
        {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }

    /**
     * Creats an array copy of the players hand
     * @param hand the player's hand
     * @return a copy of the hand
     */
    private Tile[] copyRack(Tile[] hand)
    {
        return Arrays.copyOf(hand, hand.length);
    }

    /**
     * Adds a new player to the game and initializes their starting rack.
     * @param name the name of the player to add
     */
    public void addPlayer(String name) 
{
    Player newPlayer = new Player(name);
    players.add(newPlayer);

    // Fill rack
    for (int i = 0; i < HAND_SIZE; i++) { 
        newPlayer.setAvailableTile(tileBag.pickFromBag());
    }

    // Initialize undo/redo stacks for this player
    Stack<MoveState> undo = new Stack<>();
    Stack<MoveState> redo = new Stack<>();
    undoStacks.put(newPlayer, undo);
    redoStacks.put(newPlayer, redo);

    // Push initial empty MoveState (no tiles placed yet)
    undo.push(new MoveState(
        new int[0],         // no rows
        new int[0],         // no cols
        new Tile[0],        // no tiles placed
        copyRack(newPlayer.getAvailableTiles()), // rack snapshot
        newPlayer.getScore()                     // initial score
    ));
}





    

    /**
     * Adds a new AI player to the game and initializes their starting rack.
     * @param name the name of player to add
     */
    public void addAIPlayer(String name)
{
    AI newAI = new AI(name);
    players.add(newAI);
    this.addBoardObserver(newAI);

    // Fill rack
    for (int i = 0; i < HAND_SIZE; i++) {
        newAI.setAvailableTile(tileBag.pickFromBag());
    }

    // Initialize undo/redo stacks for this AI
    Stack<MoveState> undo = new Stack<>();
    Stack<MoveState> redo = new Stack<>();
    undoStacks.put(newAI, undo);
    redoStacks.put(newAI, redo);

    // Push initial empty MoveState
    undo.push(new MoveState(
        new int[0],
        new int[0],
        new Tile[0],
        copyRack(newAI.getAvailableTiles()),
        newAI.getScore()
    ));
}


    
    /**
     * Starts a new Scrabble game by creating players, initializing their racks, 
     * and setting the first active player.
     *
     * @param controller the controller managing player actions
     * @param numOfPlayers number of players in the game
     */
    public void game(ScrabbleController controller, int numOfPlayers)
    {
        AI.setUp(board.getWordDictionary()); // set up AI

        currentPlayerIndex = 0;   
        currentPlayer = players.get(currentPlayerIndex);
        
        view.startGame(currentPlayer.getAvailableTiles());
    }

    /**
     * Handles swapping selected tiles from the current player's rack.
     * Replaces chosen tiles with new ones from the tile bag and passes the turn.
     */
    public void swapTiles()
    {
        List<JButton> selectedButtons = controller.getSelectedRackButtons();

        if (selectedButtons.isEmpty())
        {
            view.handleScrabbleUpdate(new ScrabbleEvent(this, "No tiles selected to swap!" ,currentPlayer, null, null, null));
            return;
        }

        if (tileBag.size() == 0)
        {
            view.handleScrabbleUpdate(new ScrabbleEvent(this, "TileBag is empty!" ,currentPlayer, null, null, null));
            return;

        }

        Tile[] hand = currentPlayer.getAvailableTiles();
        for (JButton button : selectedButtons)
        {
            String letter = button.getText();
            for (int i = 0; i < hand.length; i++)
            {
                if (hand[i] != null && hand[i].getEffectiveLetter() == letter.charAt(0))
                {
                    Tile oldTile = hand[i];
                    Tile newTile = tileBag.pickFromBag();
                    currentPlayer.setAvailableTileAt(i, newTile);
                    tileBag.returnTileToBag(oldTile);
                    break;
                }
            }
        }

        Player swappedPlayer = currentPlayer;
        Player nextPlayer = getNextPlayer();
        currentPlayer.resetPassCounter();
        
        controller.resetButtons(controller.getSelectedRackButtons());
        controller.getSelectedRackButtons().clear();

        view.handleScrabbleUpdate(new ScrabbleEvent(this, swappedPlayer, nextPlayer,
            "Player swapped Tiles", getCurrentPlayerIndex()));

        advanceTurn();
    }   

    /**
     * Handles the process of placing a word on the board.
     * Validates the selection, calculates the score, updates the player's rack,
     * and triggers the appropriate ScrabbleEvent.
     */
    public void placeWordOnBoard() 
    {
        List<JButton> selectedBoardButtons = controller.getSelectedBoardButtons();
        List<JButton> selectedRackButtons = controller.getSelectedRackButtons();

        if (selectedBoardButtons.isEmpty()) {
           view.handleScrabbleUpdate(new ScrabbleEvent(this, "No board tiles selected!", 
            getCurrentPlayer(), null, null, null));
           return;
       }
       JButton[][] grid = view.getBoardPanel().getTileButtonGrid();

        // Count empty squares
        int emptySquares = 0;
        for (JButton boardButton : selectedBoardButtons) {
            boolean found = false;
            for (int r = 0; r < grid.length && !found; r++) {
                for (int c = 0; c < grid[r].length && !found; c++) {
                    if (grid[r][c] == boardButton) {
                        char boardLetter = view.getBoardPanel().getEffectiveLetterAt(r, c);
                        if (boardLetter == ' ') 
                        {
                            emptySquares++;
                        }
                        found = true;
                    }
                }
            }
        }

        // Validate rack selection matches empty squares
        if (selectedRackButtons.size() != emptySquares) {
            controller.resetButtons(controller.getSelectedBoardButtons());
            controller.resetButtons(controller.getSelectedRackButtons());
            controller.getSelectedBoardButtons().clear();
            controller.getSelectedRackButtons().clear();
            ScrabbleEvent e = new ScrabbleEvent(this, "You must select a rack tile for each empty board square!",
                    getCurrentPlayer(), null, null, null);
            view.handleScrabbleUpdate(e);
            return;
        }

        int length = selectedBoardButtons.size();
        int[] rows = new int[length];
        int[] cols = new int[length];
        char[] tiles = new char[length];
        Tile[] tilesUsedFromRack = new Tile[selectedRackButtons.size()];

        int rackIndex = 0;

        for (int i = 0; i < selectedBoardButtons.size(); i++) 
        {
            JButton boardButton = selectedBoardButtons.get(i);
            int row = -1, col = -1;

            // Find board coordinates
            outerLoop:
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    if (grid[r][c] == boardButton) {
                        row = r;
                        col = c;
                        break outerLoop;
                    }
                }
            }

            rows[i] = row;
            cols[i] = col;

            char boardLetter = view.getBoardPanel().getEffectiveLetterAt(row, col);

            if (boardLetter != ' ') 
                {
                // Existing board letter, no tile taken from rack
                tiles[i] = boardLetter;
                tilesUsedFromRack[i] = null;
            } else 
                {
                // Take next selected rack tile object
                Tile rackTile = currentPlayer.getSelectedTileFromButton(selectedRackButtons.get(rackIndex));

                if (rackTile == null) 
                    {
                    // Error handling
                    view.handleScrabbleUpdate(new ScrabbleEvent(
                        this, "Error: Tile not found in rack!", currentPlayer, null, null, null));
                    return;
                }

                tiles[i] = rackTile.getEffectiveLetter();
                tilesUsedFromRack[i] = rackTile;

                rackIndex++; // move to next selected rack tile
            }
        }


        // Check orientation
        boolean horizontal = true;
        boolean vertical = true;
        for (int i = 1; i < length; i++) {
            if (rows[i] != rows[0]) horizontal = false;
            if (cols[i] != cols[0]) vertical = false;
        }

        if (!horizontal && !vertical) {
            controller.resetButtons(controller.getSelectedBoardButtons());
            controller.resetButtons(controller.getSelectedRackButtons());
            selectedBoardButtons.clear();
            selectedRackButtons.clear();
            ScrabbleEvent e = new ScrabbleEvent(this, "Tiles must be in a straight line!",
            getCurrentPlayer(), getCurrentPlayer().getAvailableTiles(), rows, cols);
            view.handleScrabbleUpdate(e);
            return;
        }

        // Sort tiles for placement
        int[] sortedRows = Arrays.copyOf(rows, length);
        int[] sortedCols = Arrays.copyOf(cols, length);
        char[] sortedTiles = Arrays.copyOf(tiles, length);
        for (int i = 0; i < length - 1; i++) {
            for (int j = i + 1; j < length; j++) {
                if ((horizontal && sortedCols[i] > sortedCols[j]) || (vertical && sortedRows[i] > sortedRows[j])) {
                    // Swap rows
                    int tmpRow = sortedRows[i];
                    sortedRows[i] = sortedRows[j];
                    sortedRows[j] = tmpRow;
                    // Swap cols
                    int tmpCol = sortedCols[i];
                    sortedCols[i] = sortedCols[j];
                    sortedCols[j] = tmpCol;
                    // Swap tiles
                    char tmpTile = sortedTiles[i];
                    sortedTiles[i] = sortedTiles[j];
                    sortedTiles[j] = tmpTile;
                }
            }
        }

       
        // Only save a state if at least one tile is being placed
        int tilesPlaced = 0;
        for (Tile t : tilesUsedFromRack) 
        {
            if (t != null) tilesPlaced++;
        }

        if (tilesPlaced > 0) 
        {
            undoStacks.get(currentPlayer).push(
                new MoveState(
                    rows,
                    cols,
                    tilesUsedFromRack,
                    copyRack(currentPlayer.getAvailableTiles()),
                    currentPlayer.getScore()
                )
            );
            redoStacks.get(currentPlayer).clear();
        }


        // Place tiles and validate
        int score = board.placeTilesAndValidate(sortedRows, sortedCols, tilesUsedFromRack);

        if (score > 0) 
        {
            
            currentPlayer.addScore(score);
            currentPlayer.useTile(tilesUsedFromRack);
            refillRack(currentPlayer);
            currentPlayer.resetPassCounter();

            lastPlayerWhoMoved = currentPlayer;
            Player previousPlayer = currentPlayer;
            Player nextPlayer = getNextPlayer();

            controller.resetButtons(controller.getSelectedBoardButtons());
            controller.resetButtons(controller.getSelectedRackButtons());
            selectedBoardButtons.clear();
            selectedRackButtons.clear();

            ScrabbleEvent e = new ScrabbleEvent(this, previousPlayer, nextPlayer, score, sortedTiles, sortedRows, sortedCols);

            this.notifyBoardChanged(sortedRows, sortedCols, sortedTiles);
            view.handleScrabbleUpdate(e);

            advanceTurn();
        } 
        else 
        {
            controller.resetButtons(controller.getSelectedBoardButtons());
            controller.resetButtons(controller.getSelectedRackButtons());
            selectedBoardButtons.clear();
            selectedRackButtons.clear();

            ScrabbleEvent e = new ScrabbleEvent(this, "Invalid word placement!", currentPlayer,
            currentPlayer.getAvailableTiles(), sortedRows, sortedCols);
            view.handleScrabbleUpdate(e);
        }  
    }

    /**
     * A function to undo a move, stored in a stack to allow multiple undos
     * @param player
     * @return true or false, whether undo was a success
     */
    public boolean undo(Player player) 
    {
        Stack<MoveState> undo = undoStacks.get(player);
        Stack<MoveState> redo = redoStacks.get(player);

        if (undo.size() <= 1) {return false;}

        MoveState lastMove = undo.pop(); 
        redo.push(new MoveState(
            lastMove.getRows(),
            lastMove.getCols(),
            lastMove.getTiles(),
            copyRack(player.getAvailableTiles()),
            player.getScore()
        ));

        restoreMoveState(player, lastMove);

        view.handleScrabbleUpdate(new ScrabbleEvent(this, player, player.getScore()));
        return true;
    }

    /**
     * A function to redo a move, stored in a stack to allow multiple redos, if there are moves to redo
     * @param player
     * @return true or false, whether redo was a success
     */
    public boolean redo(Player player) 
    {
        Stack<MoveState> undo = undoStacks.get(player);
        Stack<MoveState> redo = redoStacks.get(player);

        if (redo.isEmpty()) {return false;}

        MoveState move = redo.pop();
        undo.push(new MoveState(
            move.getRows(),
            move.getCols(),
            move.getTiles(),
            copyRack(player.getAvailableTiles()),
            player.getScore()
        ));

        // Reapply the tiles
        Tile[] tiles = move.getTiles();
        int[] rows = move.getRows();
        int[] cols = move.getCols();

        for (int i = 0; i < tiles.length; i++) 
        {
            if (tiles[i] != null) 
            {
                board.setLetterAt(rows[i], cols[i], tiles[i].getEffectiveLetter());
            }
        }

        // Restore rack and score
        player.setAvailableTiles(copyRack(move.getRackSnapshot()));
        player.setScore(move.getScoreSnapshot());

        view.handleScrabbleUpdate(new ScrabbleEvent(this, player, player.getScore()));
        return true;
    }



    /**
     * A function to restore a previous state, clearing
     * @param player the player's move to changee
     * @param state a snapshot of the board
     */
    private void restoreMoveState(Player player, MoveState state) 
    {
        // Remove the tiles that were placed in this move
        Tile[] tiles = state.getTiles();
        int[] rows = state.getRows();
        int[] cols = state.getCols();

        for (int i = 0; i < tiles.length; i++) 
        {
            if (tiles[i] != null) 
            {
                board.setLetterAt(rows[i], cols[i], ' '); // clear just this move's tiles
            }
        }

        // Restore rack and score
        player.setAvailableTiles(copyRack(state.getRackSnapshot()));
        player.setScore(state.getScoreSnapshot());
    }


    /**
     * Passes the current player's turn, clears selections,
     * and advances to the next player.
     */
    public void passTurn()
    {
        controller.resetButtons(controller.getSelectedBoardButtons());
        controller.resetButtons(controller.getSelectedRackButtons());
        controller.getSelectedBoardButtons().clear();
        controller.getSelectedRackButtons().clear();
        currentPlayer.addToPassCounter();

        int allPassed = 0;

        for (int i = 0 ; i < players.size(); i++) {
            if (players.get(i).getPassCounter() >= 2){
                allPassed++;
            }
        }

        if (allPassed == players.size()) {
            view.handleScrabbleUpdate(new ScrabbleEvent(this, "\n"+ "Everyone passed their turn twice!", true));
            return;
        }

        Player previousPlayer = currentPlayer;

        Player nextPlayer = getNextPlayer();

        view.handleScrabbleUpdate(new ScrabbleEvent(this, previousPlayer, nextPlayer));

        advanceTurn();

        //DEBUG
        System.out.println("prev: " + previousPlayer);
        System.out.println("next: " + nextPlayer);
    }

    /**
     * Refills a player's rack from the tile bag until it is full.
     * @param player the player whose rack should be refilled
     */
    private void refillRack(Player player) 
    {
        Tile[] hand = player.getAvailableTiles();
        for (int i = 0; i < hand.length; i++) 
        {
            if (hand[i] == null) 
            { 
                Tile newTile = tileBag.pickFromBag();
                if (newTile != null) 
                {
                    player.setAvailableTileAt(i, newTile);
                } 
                
            }
        }
    } 

   /**
    * Passes the current player's turn, clears selections,
    * and advances to the next player.
    */
    public void addView(ScrabbleView view) 
    { 
        this.view = view; 
    }

    /**
     * Connects the controller to the model.
     * @param controller the ScrabbleController instance to associate with the model
     */
    public void addController(ScrabbleController controller)
    {
        this.controller = controller;
    }

    /**
     * Add board obsevers this is for the AI players
     * @param observer the Ai player to be notified
     */
    public void addBoardObserver(BoardObserver observer){
        this.boardObservers.add(observer);
    }

    /**
     * Notify observer that the board has changed this is for the AI players
     */
    private void notifyBoardChanged(int[] rows, int[] cols, char[] sortedTiles) {
        for (BoardObserver observer : boardObservers) {
            observer.boardChanged(rows, cols, sortedTiles);
        }
    }



    /**
     * Returns the current player's available tiles (hand).
     * @return an array of Tile objects in the current player's rack
     */
    public Tile[] getHand() {return currentPlayer.getAvailableTiles();}
    
    /**
     * Returns the player whose turn is next.
     * @return the next Player object
     */
    protected Player getNextPlayer() 
    { 
        return players.get((currentPlayerIndex + 1) % players.size());    
    }

    /**
     * Advances whose turn it is
     */
    protected void advanceTurn() 
    {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        if (currentPlayer instanceof AI) {
            this.AIplays((AI)players.get(currentPlayerIndex));
        }
    }

    /**
     * Plays AI turn
     */
    private void AIplays(AI currentAIPlayer){
        Set<String> possiblewords = currentAIPlayer.findPossibleWord();
        AI.PlaceWord move = currentAIPlayer.findBestWord(board,possiblewords);

        if (move != null && currentAIPlayer.shouldPlace) {
            currentAIPlayer.togglePlace();

            int score = board.placeTilesAndValidate(move.rows,move.cols,move.tilesNeeded);
            currentPlayer.addScore(score);
            currentPlayer.useTile(move.tilesNeeded);
            refillRack(currentPlayer);
            currentPlayer.resetPassCounter();

            lastPlayerWhoMoved = currentPlayer;

            Player previousPlayer = currentPlayer;
            Player nextPlayer = getNextPlayer();

            ScrabbleEvent e = new ScrabbleEvent(this, previousPlayer, nextPlayer, score, move.word, move.rows, move.cols);

            this.notifyBoardChanged(move.rows, move.cols, move.word);
            view.handleScrabbleUpdate(e);

            advanceTurn();

        } else if (tileBag.size() > 7 && !currentAIPlayer.shouldPlace) {
            currentAIPlayer.togglePlace();
            currentPlayer.resetPassCounter();

            Tile[] hand = currentPlayer.getAvailableTiles();

            for (int i = 0; i < 7; i++) {
                Tile oldTile = hand[i];
                Tile newTile = tileBag.pickFromBag();
                currentPlayer.setAvailableTileAt(i, newTile);
                tileBag.returnTileToBag(oldTile);
            }

            lastPlayerWhoMoved = currentPlayer;

            Player swappedPlayer = currentPlayer;
            Player nextPlayer = getNextPlayer();

            view.handleScrabbleUpdate(new ScrabbleEvent(this, swappedPlayer, nextPlayer,
                    "Player swapped Tiles", getCurrentPlayerIndex()));

            advanceTurn();
        } else {
            this.passTurn();
        }
    }

    /**
     * Method to serialize the game
     * @param filename the filename to save the game to
     */
    public void serializeToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    /**
     * Method to deserialize the game and load it
     * @param filename the filename to upload the game from
     */
    public void deserializeFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            loadGame((ScrabbleModel) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error opening file.");
        }
    }
    
    /**
    * A method to load the game from a serialized state
    * @param m the loaded ScrabbleModel
    **/
    public void loadGame(ScrabbleModel m){
        this.board = m.board;
        this.players = m.players;
        this.currentPlayerIndex = m.currentPlayerIndex;
        this.currentPlayer = m.currentPlayer;
        this.lastPlayerWhoMoved = m.lastPlayerWhoMoved;
        this.tileBag = m.tileBag;
        this.undoStacks = m.undoStacks;
        this.redoStacks = m.redoStacks;

        this.boardObservers = new ArrayList<>();
        for (Player p : players) {
            if (p instanceof AI) {
                this.addBoardObserver((AI)p);
            }
        }

        board.setWordDictionary(new WordDictionary(("src/ValidWords.txt")));

        AI.legalPlacements.clear();
        AI.board.clear();

        // rebuild AI board from actual Scrabble board
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                char letter = board.getLetterAt(r, c);
                if (letter != ' ') {
                    AI.board.put(new Point(r, c), letter);

                    // recompute legal placements
                    int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
                    for (int[] d : dirs) {
                        int x = r + d[0];
                        int y = c + d[1];
                        if (x >= 0 && x < 15 && y >= 0 && y < 15 && board.getLetterAt(x, y) == ' ') {
                            AI.legalPlacements.add(new Point(x, y));
                        }
                    }
                }
            }
        }

        AI.dictionary = new WordDictionary("ValidWords.txt");
        
        // Refresh the GUI
        if (this.view != null) {
            ScrabbleEvent e = new ScrabbleEvent(this, players, board);
            view.handleScrabbleUpdate(e);
           this.view.getRackPanel().updateRack(this.currentPlayer.getAvailableTiles());
        }

        System.out.println("Board uploaded and GUI refreshed");
    }

    /**
     * Returns the index of the current player.
     * @return the current player's index
     */
    protected int getCurrentPlayerIndex() {return currentPlayerIndex;}

    /**
     * Returns the list of all players in the game.
     * @return a list of Player objects
     */
    protected List<Player> getPlayers() {return players; }

    /**
     * Return the player names in a string[]
     * @return string array of player names 
     */
    public String[] getPlayerNames()
    {
        String[] playerNames = new String[players.size()]; 

        for (int i = 0; i < players.size(); i++)
        {
            playerNames[i] = players.get(i).getName();
        }

        return playerNames;
    }
    

    /**
     * Returns the player whose turn it currently is.
     * @return the current Player object
     */
    protected Player getCurrentPlayer() {return players.get(currentPlayerIndex);}

    protected Board getBoard(){
        return board;
    }

    /**
     * Main method to start and test the Scrabble game.
     * Creates the model, view, and controller, and initializes gameplay.
     * 
     * @throws IOException if the dictionary file cannot be loaded
     */
    public static void main(String[] args) throws IOException 
    {
        ScrabbleModel model = new ScrabbleModel();
        ScrabbleViewFrame view = new ScrabbleViewFrame(model);
        

        model.addView(view);
        model.addController(view.getController());

        int numOfPlayers = view.getNumPlayers();  
        

        model.game(view.getController(), numOfPlayers);

        // Refresh the rack
        view.getRackPanel().updateRack(model.getCurrentPlayer().getAvailableTiles());
    }

    /**
     * Getter for undo stack
     * @param player we want the undo stack from
     * @return the undo stack of the player
     */
    public Stack<MoveState> getUndoStack(Player player) {
        return undoStacks.get(player);
    }

    /**
     * Getter for redo stack
     * @param player we want the redo stack from
     * @return the redo stack of the player
     */
    public Stack<MoveState> getRedoStack(Player player) {
        return redoStacks.get(player);
    }


}

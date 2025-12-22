import java.util.*;
import java.util.EventObject;

/**
 * This class models an event for the scrabble game
 * 
 **/
public class ScrabbleEvent extends EventObject {
    // Enum to represent the type of event
    public enum EventType{
        PASS_TURN,
        UNSUCCESSFUL_TILE_PLACEMENT,
        TILE_PLACEMENT,
        SWAP_TILES,
        GAME_OVER,
        UNDO_REDO,
        LOAD_GAME,
    }

    private final EventType eventType; //the type of event
    private Player player;  //the player associated with the event
    private Player nextPlayer; // the next player
    private int playerNumber;
    private Tile[] playerHand;
    private Tile[] nextPlayerHand;
    private boolean gameOver; //is the game over
    private int newScore; //change in the players score if any
    private String message; //message to be displayed in the game
    private char[] tilesUsed; //the tiles used in the move if any
    private int[] row;  //Row coordinates for tile placement (if any)
    private int [] col; //Column coordinates for tile placement (if any)
    private List<Player> players;

    /**
     * Constructor for undo / redo action
     */
    public ScrabbleEvent(ScrabbleModel model, Player player, int newScore)
    {
        super(model);
        this.eventType = EventType.UNDO_REDO;
        this.player = player;
        this.newScore = newScore;
        this.playerHand = player.getAvailableTiles(); 
        this.row = new int[0];
        this.col = new int[0];
    }

    /**
     * Constructor for a Passed Turn
     */
    public ScrabbleEvent(ScrabbleModel model, Player player, Player nextPlayer){
        super(model);
        this.eventType = EventType.PASS_TURN;
        this.player = player;
        this.nextPlayer = nextPlayer;
        this.playerHand = player.getAvailableTiles();
        this.nextPlayerHand = nextPlayer.getAvailableTiles();


        //debugging line
        System.out.println("Event Created: PASS_TURN,  Player: " + player.getName());

    }

    /**
     * Constructor for successful tile placement.
     */
    public ScrabbleEvent(ScrabbleModel model, Player player, Player nextPlayer, int newScore, char[] tilesUsed, int[] row, int[] col){
        super(model);
        this.eventType = EventType.TILE_PLACEMENT;
        this.player = player;
        this.nextPlayer = nextPlayer;
        this.playerHand = player.getAvailableTiles();
        this.newScore = newScore;
        this.tilesUsed = tilesUsed;
        this.row = row;
        this.col = col;

        System.out.println("Event Created: TILE_PLACEMENT, Player: " + player.getName());
    }

    /**
     * Constructor for unsuccessful tile placement.
     **/
    public ScrabbleEvent(ScrabbleModel model, String message, Player player, Tile[] hand, int[] row, int[] col){
        super(model);
        this.eventType = EventType.UNSUCCESSFUL_TILE_PLACEMENT;
        this.message = message;
        this.player = player;
        this.playerHand = hand;
        this.row = row;
        this.col = col;

        System.out.println("Event Created: UNSUCCESSFUL_TILE_PLACEMENT, Player: " + player.getName());
    }

    /**
     * Constructor for game over.
     */
    public ScrabbleEvent(ScrabbleModel model, String message, boolean gameOver){
        super(model);
        this.eventType = EventType.GAME_OVER;
        this.message = message;
        this.gameOver = gameOver;

        System.out.println("Event Created: GAME_OVER");
    }

    /**
     * Constructor for tile swapping.
     */
    public ScrabbleEvent(ScrabbleModel model, Player player, Player nextPlayer, String message, int playerNumber){
        super(model);
        this.eventType = EventType.SWAP_TILES;
        this.player = player;
        this.playerNumber = playerNumber;
        this.message = message;
        this.playerHand = player.getAvailableTiles();
        this.nextPlayer = nextPlayer;
        this.nextPlayerHand = nextPlayer.getAvailableTiles();

        System.out.println("Event Created: SWAP_TILES, Player: " + player.getName());
    }

    /**
    *  Old swap constructor, used for validating that there are in fact selected swap tiles 
    */
    public ScrabbleEvent(ScrabbleModel model, Player player, String message, int playerNumber)
    {
        super(model);
        this.eventType = EventType.SWAP_TILES;
        this.player = player;
        this.playerNumber = playerNumber;
        this.message = message;
        this.playerHand = player.getAvailableTiles();
        this.nextPlayer = null;
        this.nextPlayerHand = null;
    }

    /**
     * Constructor for loading a saved game.
     */
    public ScrabbleEvent(ScrabbleModel model, List<Player> currentPlayers, Board board) {
        super(model);
        this.eventType = EventType.LOAD_GAME;
        this.players = currentPlayers;
        this.nextPlayer = null;

        List<Integer> rowsList = new ArrayList<>();
        List<Integer> colsList = new ArrayList<>();
        List<Character> lettersList = new ArrayList<>();

        char[][] boardChars = board.getCharBoard();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                char letter = boardChars[r][c];
                if (letter != ' ') {
                    rowsList.add(r);
                    colsList.add(c);
                    lettersList.add(letter);
                }
            }
        }

        this.row = rowsList.stream().mapToInt(Integer::intValue).toArray();
        this.col = colsList.stream().mapToInt(Integer::intValue).toArray();
        this.tilesUsed = new char[lettersList.size()];
        for (int i = 0; i < lettersList.size(); i++) {
            this.tilesUsed[i] = lettersList.get(i);
        }

        System.out.println("Event Created: LOAD_GAME, Current Player: ");
    }


    /**
     * Getters.
     */
    public EventType getEventType(){
        return eventType;
    }

    public Player getPlayer(){
        return player;
    }

    public Player getNextPlayer(){
        return nextPlayer;
    }

    public int getNewPlayerScore(){
        return newScore;
    }

    public char[] getTilesUsed(){
        return tilesUsed;
    }

    public int[] getRow(){
        return row;
    }

    public int[] getCol(){
        return col;
    }

    public Tile[] getPlayerHand(){
        return playerHand;
    }

    public String getMessage(){
        return message;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public int getPlayerNumber(){
        return playerNumber;
    }

}

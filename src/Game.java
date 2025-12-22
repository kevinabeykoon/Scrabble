import java.io.IOException;
import java.util.*;

/**
 * This class represents the entire Scrabble game. To start
 * a game of Scrabble, create an instance of this Game class
 * and then invoke the start() method. This class is reponsible
 * for moving through the game by keeping track of the players,
 * keeping track of who's turn it is, and storing the Scrable
 * board. The game is text-based, thus the board and scores will
 * be printed to the standard output stream.
 *
 * @version 27/10/2025
 */
public class Game
{
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private TileBag tileBag;

    /**
     * The constructor used to create the Game object
     * and sets the board, list of players, and who's
     * turn it is.
     */
    public Game() throws IOException {
        this.board = new Board(new WordDictionary("ValidWords.txt"));
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.tileBag = new TileBag();
    }


    /**
     * Adds new player to the game
     * @param name
     */
    public void addPlayer(String name) {
        Player newPlayer = new Player(name);
        players.add(newPlayer);
        for (int i=0; i < 7; i++){
            newPlayer.setAvailableTile(tileBag.pickFromBag());
        }
    }

    /**
     * This method is used to start the game. It is
     * responsible for continously running the game
     * until it terminates.
     *
     * The method:
     *   1. It displays the current board
     *   2. Allows the current player to play their turn
     *   3. Displays the player scores
     *   4. Prompts the user if they want to keep playing
     *   5. Moves to the next player's turn
     *   6. Displays the final scores and terminates the game
     */
    public void start()
    {
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;
        boolean playersSet = false;

        System.out.println("Welcome to Scrabble! Let's play!\nHow many players are you? (2-4 players)\n");
        String response = scanner.nextLine();

        while (!playersSet) {
            try {
                int numPlayers = Integer.parseInt(response);
                if (numPlayers > 1 && numPlayers <= 4){
                    for (int i = 0; i < numPlayers; i++){
                        System.out.println("Player " + (i+1) + " "+ "enter your name!:");
                        addPlayer(scanner.nextLine());
                    }
                    playersSet = true;
                }
                else{
                    System.out.println(numPlayers < 2? "Awww go get some friends to play this with you :(":"Max is 4 players...someone needs to go");
                    System.out.println("\nHow many players are you? (2-4 players)");
                    response = scanner.nextLine();
                }
            }
            catch (NumberFormatException e){
                System.out.println("Please enter a number between 1 and 7");
            }}

        System.out.println("___ Game Starting ___\n\n");

        while(!gameOver)
        {
            board.display();
            Player currentPlayer = players.get(currentPlayerIndex);
            currentPlayer.playTurn(board, scanner, tileBag);

            displayScores("Current Scores: ");

            System.out.println("Continue? (Y/N): ");
            response = scanner.nextLine();
            if(response.equalsIgnoreCase("N"))
            {
                gameOver = true;
            }
            else
            {
                nextPlayer();
            }
        }

        System.out.println("Game over!");
        displayScores("Final Scores: ");

    }

    /**
     * This method advances the game by switching
     * to the next player. It does this by looping
     * over the list of players.
     */
    protected void nextPlayer() { currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); }

    /**
     * This method displays the  scores of
     * the players on the standard input stream.
     * 
     * @param message the header for the scores, ex. Current Scores: , Final Scores: 
     */
    protected void displayScores(String message)
    {
        System.out.println(message);
        for (Player p : players)
        {
            System.out.println(p.getName() + ": " + p.getScore());
        }
    }

    protected int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game();
        game.start();
    }
}

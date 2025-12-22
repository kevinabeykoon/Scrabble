import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Player class.
 *
 * Tests Player functionality, including:
 * - Adding tiles to the player rack
 * - Using and swapping tiles
 * - Displaying available tiles
 * - Updating and retrieving scores
 * - Placing a word on the board
 *
 */

class PlayerTest {

    private Player player;
    private TileBag tileBag;
    private Board board;

    @BeforeEach
    public void setUp() throws Exception {
        player = new Player("TestPlayer");
        tileBag = new TileBag();
        board = new Board(new WordDictionary("ValidWords.txt"));

        for (char c = 'A'; c <= 'G'; c++) {
            player.addTile(Tile.getTile('C'));
        }
    }

    @Test
    public void testAddAndUseTile() {
        Player player = new Player("Alice");
        assertEquals(7, player.getAvailableTiles().length);
        Tile tile = Tile.getTile('A');
        assertTrue(player.addTile(tile));
        player.useTile(new Tile[]{tile});
        assertEquals(7, player.getAvailableTiles().length);
    }

    @Test
    public void testAddScore() {
        Player player = new Player("Bob");
        player.addScore(10);
        assertEquals(10, player.getScore());
    }

    @Test
    public void testDisplayAvailableTile() {
        Player player = new Player("Charlie");
        player.addTile(Tile.getTile('B'));
        String tiles = player.displayAvailableTile();
        assertTrue(tiles.contains("B"));
    }

    @Test
    public void testPlaceWordOnBoard() {
        Player player2 = new Player("TestPlayer");

        player2.addTile(Tile.getTile('T'));
        player2.addTile(Tile.getTile('E'));
        player2.addTile(Tile.getTile('S'));
        player2.addTile(Tile.getTile('T'));

        String input = "TEST 7,7,7,7 7,8,9,10\n";
        Scanner scanner = new Scanner(input);

        int score = player2.placeWordOnBoard(board, scanner, tileBag);

        assertTrue(score >= 0);
        assertEquals('T', board.getLetterAt(7, 7));
        assertEquals('T', board.getLetterAt(7, 10));
    }

}

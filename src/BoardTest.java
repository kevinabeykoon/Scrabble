import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


/**
 * JUnit tests for the Board class.
 *
 * Tests the core functionality of the Board class, including:
 * - Setting and getting letters on the board
 * - Placing tiles and validating word placement rules
 * - Extracting words horizontally and vertically
 * - Checking adjacency of placed tiles
 * - Calculating turn score
 *
 *
 */
class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() throws Exception {
        WordDictionary dict = new WordDictionary("ValidWords.txt");
        board = new Board(dict);
    }

    @Test
    public void testSetAndGetLetterAt() {
        board.setLetterAt(0, 0, 'A');
        assertEquals('A', board.getLetterAt(0, 0));
    }

    @Test
    void testPlaceTilesAndValidateValidWord() {
        int[] rows = {7, 7, 7, 7};
        int[] cols = {7, 8, 9, 10};
        Tile[] tiles = {Tile.getTile('T'), Tile.getTile('E'),
                Tile.getTile('S'), Tile.getTile('T')};

        int score = board.placeTilesAndValidate(rows, cols, tiles);
        assertTrue(score >= 0);
        assertEquals('T', board.getLetterAt(7, 7));
        assertEquals('T', board.getLetterAt(7, 10));
    }

    @Test
    public void testExtractWordHorizontal() {
        board.setLetterAt(7, 7, 'C');
        board.setLetterAt(7, 8, 'A');
        board.setLetterAt(7, 9, 'T');

        String word = board.extractWordAndCalculateScore(
                7, 7, true,
                new int[]{7, 8, 9},
                new int[]{7, 8, 9}
        );
        assertEquals("CAT", word);
    }

    @Test
    void testCheckAdjacency() {
        int[] rows = {7, 7};
        int[] cols = {7, 8};
        Tile[] tiles = {Tile.getTile('H'), Tile.getTile('I')};
        board.placeTilesAndValidate(rows, cols, tiles);

        int[] newRows = {7};
        int[] newCols = {9};
        Tile[] newTiles = {Tile.getTile('S')};
        assertTrue(board.checkAdjacency(newRows, newCols));
    }
}

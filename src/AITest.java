import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the AI class.
 *
 * Tests AI functionality, including:
 * - Legal word placements
 * - Helper functions like setUp() and togglePlace()
 *
 */

class AITest {

    private AI ai;
    private Board board;
    private WordDictionary dictionary;

    @BeforeEach
    public void setUp() throws Exception {
        dictionary = new WordDictionary("ValidWords.txt");
        board = new Board(dictionary);
        AI.setUp(dictionary);

        ai = new AI("AI");

        ai.addTile(Tile.getTile('T'));
        ai.addTile(Tile.getTile('E'));
        ai.addTile(Tile.getTile('S'));
        ai.addTile(Tile.getTile('T'));

        for (char c = 'A'; c <= 'C'; c++) {
            ai.addTile(Tile.getTile(c));
        }
    }

    @Test
    public void testAIInitialization() {
        assertEquals("AI [AI]", ai.getName());
        assertTrue(ai.getAvailableTiles().length > 0);
        assertTrue(ai.shouldPlace);
    }

    @Test
    public void testTogglePlace() {
        boolean initial = ai.shouldPlace;
        ai.togglePlace();
        assertNotEquals(initial, ai.shouldPlace);
        ai.togglePlace();
        assertEquals(initial, ai.shouldPlace);
    }

    @Test
    public void testBoardChangedAllowsPlacement() {
        int[] rows = {7};
        int[] cols = {7};
        char[] letters = {'T'};
        ai.boardChanged(rows, cols, letters);

        Set<String> possible = ai.findPossibleWord();
        AI.PlaceWord bestWord = ai.findBestWord(board, possible);

        assertNotNull(bestWord);

        // Check if adjacency of placed word is valid
        boolean placedAdjacent = false;
        for (int i = 0; i < bestWord.rows.length; i++) {
            if ((bestWord.rows[i] == 7 && Math.abs(bestWord.cols[i] - 7) == 1) ||
                    (bestWord.cols[i] == 7 && Math.abs(bestWord.rows[i] - 7) == 1)) {
                placedAdjacent = true;
            }
        }
        assertTrue(placedAdjacent);
    }


    @Test
    public void testFindPossibleWord() {
        Set<String> possible = ai.findPossibleWord();
        assertNotNull(possible);
        assertTrue(possible.contains("TEST"));
    }

    @Test
    public void testFindBestWordUsesAvailableTiles() {
        Set<String> possible = ai.findPossibleWord();
        AI.PlaceWord bestWord = ai.findBestWord(board, possible);

        assertNotNull(bestWord);

        // The word must consist of letters AI actually has
        for (char c : bestWord.word) {
            boolean hasTile = false;
            for (Tile t : ai.getAvailableTiles()) {
                if (!t.isBlank() && t.getEffectiveLetter() == c) {
                    hasTile = true;
                    break;
                }
            }
            assertTrue(hasTile || board.getLetterAt(7,7) == c);
        }
    }

}

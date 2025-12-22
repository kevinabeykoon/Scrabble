import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit tests for the Game class.
 *
 * Tests the Game class behavior, including:
 * - Adding players
 * - Switching to the next player
 * - Tracking current player
 * - Displaying player scores
 *
 */
class GameTest {

    @Test
    void testAddPlayers() throws IOException {
        Game game = new Game();
        game.addPlayer("Alice");
        game.addPlayer("Bob");
        
    }

    @Test
    public void testAddPlayerAndNextPlayer() throws IOException {
        Game game = new Game();
        game.addPlayer("Alice");
        game.addPlayer("Bob");

        game.addPlayer("Charlie");
        game.addPlayer("Dana");

        game.nextPlayer();
        assertEquals(1, game.getCurrentPlayerIndex());
    }

    @Test
    public void testDisplayScores() throws IOException {
        Game game = new Game();
        game.addPlayer("Alice");
        game.addPlayer("Bob");
        game.displayScores("Current Scores:");

    }

    
}

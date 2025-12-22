import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;

/**
 * Test Class for ScrabbleModel
 *
 */
public class ScrabbleModelTest {

    private ScrabbleModel model;
    private MockView view;
    private ScrabbleController controller;


    @BeforeEach
    public void setUp() {
        model = new ScrabbleModel();
        view = new MockView();
        controller = new ScrabbleController(model, null);
        model.addView(view);
        model.addController(controller);
    }

    @Test
    public void testGameInitialization() {
        model.addPlayer("Alice");
        model.addPlayer("Bob");
        model.game(controller, 2);

        List<Player> players = model.getPlayers();
        assertEquals(2, players.size());
        assertEquals("Alice", players.get(0).getName());
        assertEquals("Bob", players.get(1).getName());

        assertEquals(7, countTiles(players.get(0).getAvailableTiles()));
        assertEquals(7, countTiles(players.get(1).getAvailableTiles()));
    }

    @Test
    public void testPassTurn() {
        model.addPlayer("P1");
        model.addPlayer("P2");
        model.addPlayer("P3");
        model.game(controller, 3);


        assertEquals("P1", model.getCurrentPlayer().getName());
        model.passTurn();
        assertEquals("P2", model.getCurrentPlayer().getName());
        assertEquals(ScrabbleEvent.EventType.PASS_TURN, view.lastEvent.getEventType());
        assertEquals("P1", view.lastEvent.getPlayer().getName());
        assertEquals("P2", view.lastEvent.getNextPlayer().getName());

        model.passTurn();
        assertEquals("P3", model.getCurrentPlayer().getName());

        model.passTurn();
        assertEquals("P1", model.getCurrentPlayer().getName());
    }

    @Test

    public void testSwapWithNoSelection() {
        model.addPlayer("Alice");
        model.game(controller, 1);

        model.swapTiles();

        assertEquals(ScrabbleEvent.EventType.UNSUCCESSFUL_TILE_PLACEMENT, view.lastEvent.getEventType());
        assertTrue(view.lastEvent.getMessage().contains("No tiles selected"));
        assertNull(view.lastEvent.getNextPlayer()); // Turn doesn't change
    }

    @Test
    public void testSwapClearsSelection() {
        model.addPlayer("Alice");
        model.game(controller, 1);

        // Simulate selecting one tile
        RackPanel rackPanel = new RackPanel(controller);
        JButton[] rack = rackPanel.getTileRack();
        rack[0].setText("A");
        controller.getSelectedRackButtons().add(rack[0]);

        model.swapTiles();

        assertTrue(controller.getSelectedRackButtons().isEmpty());
    }

    @Test
    public void testPassClearsSelection() {
        model.addPlayer("Alice");
        model.game(controller, 1);


        RackPanel rackPanel = new RackPanel(controller);
        JButton[] rack = rackPanel.getTileRack();
        rack[0].setText("A");
        controller.getSelectedRackButtons().add(rack[0]);

        model.passTurn();

        assertTrue(controller.getSelectedRackButtons().isEmpty());
    }

    @Test
    public void testFullTurnCycle() {
        model.addPlayer("A");
        model.addPlayer("B");
        model.addPlayer("C");
        model.addPlayer("D");
        model.game(controller, 4);


        String[] expected = {"A", "B", "C", "D", "A"};
        for (int i = 0; i < 5; i++) {
            assertEquals(expected[i], model.getCurrentPlayer().getName());
            model.passTurn();
        }
    }

    @Test
    public void testFirstPlayerIsCurrent() {
        model.addPlayer("First");
        model.addPlayer("Second");
        model.game(controller, 2);

        assertEquals("First", model.getCurrentPlayer().getName());
    }

    private int countTiles(Tile[] hand) {
        int count = 0;
        for (Tile t : hand) {
            if (t != null) count++;
        }
        return count;
    }

}



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Undo Redo Functionality.
 *
 * Testing Undo Redo:
 * - Undo then redo = undo last move then redo newest move (current player)
 * - Undo when theres nothing to undo = no effect
 * - Redo when theres nothing to redo = no effect
 * - Undo undo = undo something twice both undos should work
 *
 */
public class UndoRedoTest {
    private ScrabbleModel model;
    private MockView view;
    private ScrabbleController controller;
    private Player player;


    @BeforeEach
    public void setUp() {
        model = new ScrabbleModel();
        view = new MockView();
        controller = new ScrabbleController(model, null);
        model.addView(view);
        model.addController(controller);

        model.addPlayer("TestPlayer1");
        model.addPlayer("TestPlayer2");
        player = model.getPlayers().get(0);
    }

    @Test
    public void testUndoRedo() {
        int[] rows = {7, 7, 7};
        int[] cols = {7, 8, 9};
        Tile[] tiles = { new Tile('C', 3), new Tile('A', 1), new Tile('T', 1) };
        Tile[] oldRack = new Tile[7];

        //word is cat
        ScrabbleModel.MoveState state = new ScrabbleModel.MoveState(
                rows, cols, tiles, oldRack, 0
        );

        model.getUndoStack(player).push(state);

        model.getBoard().setLetterAt(7, 7, 'C');
        model.getBoard().setLetterAt(7, 8, 'A');
        model.getBoard().setLetterAt(7, 9, 'T');
        player.setScore(10);

        //undo = empty
        boolean undoResult = model.undo(player);
        assertTrue(undoResult, "Undo should return true");
        assertEquals(' ', model.getBoard().getLetterAt(7, 7),"Board 7,7 should be empty");
        assertEquals(0, player.getScore(),"Score should be 0");

        //redo = cat is back
        boolean redoResult = model.redo(player);
        assertTrue(redoResult , "Redo should return true");
        assertEquals('C', model.getBoard().getLetterAt(7, 7),"Board 7,7 should have C");
        assertEquals(10, player.getScore(),"Score should be 10");
    }

    @Test
    public void testUndoNothing() {
        boolean result = model.undo(player);

        assertFalse(result,"Nothing to undo at start of game");
        assertEquals(0, player.getScore(),"Score should be 0");
    }

    @Test
    public void testRedoNothing() {
        boolean result = model.redo(player);

        assertFalse(result, "Redo should return false if nothing is undone");
    }

    @Test
    public void testUndoUndo() {
        int[] r1 = {7, 7}; int[] c1 = {7, 8};
        Tile[] t1 = { new Tile('H', 4), new Tile('A', 1) };

        //first word ha
        ScrabbleModel.MoveState move1 = new ScrabbleModel.MoveState(
                r1, c1, t1, new Tile[7], 0
        );
        model.getUndoStack(player).push(move1);

        int[] r2 = {7}; int[] c2 = {9};
        Tile[] t2 = { new Tile('T', 1) };

        //turn into hat
        ScrabbleModel.MoveState move2 = new ScrabbleModel.MoveState(
                r2, c2, t2, new Tile[7], 5
        );
        model.getUndoStack(player).push(move2);

        model.getBoard().setLetterAt(7, 7, 'H');
        model.getBoard().setLetterAt(7, 8, 'A');
        model.getBoard().setLetterAt(7, 9, 'T');
        player.setScore(10);

        //first undo = HA
        model.undo(player);
        assertEquals(' ',model.getBoard().getLetterAt(7, 9),"Board 7,9 should be empty");
        assertEquals('H', model.getBoard().getLetterAt(7, 7),"Board 7,7 should be H");
        assertEquals(5, player.getScore(),"Score should be 5");

        //second undo = empty
        model.undo(player);
        assertEquals(' ', model.getBoard().getLetterAt(7, 7),"Board 7,7 should be empty");
        assertEquals(0, player.getScore(),"Score should be 0");
    }
}

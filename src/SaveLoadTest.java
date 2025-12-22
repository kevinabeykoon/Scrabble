import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.Stack;

/**
 * JUnit tests for the save load Functionality.
 *
 * Testing Save load:
 * - Player state = make sure after saving and loading the state of the players is correct, points, turn, name, count
 * - Undo Stack = make sure after saving and laoding the undo stack has been saved, correct moves
 * - Board state = make sure after saving anf loading that the board has the correct tiles on it.
 *
 */

public class SaveLoadTest {
    private ScrabbleModel model;
    private MockView view;
    private ScrabbleController controller;
    private final String TEST_FILENAME = "TestScrabbleSave.ser";

    private class TestSafeMockView extends MockView {
        private ScrabbleController testcontroller;

        public TestSafeMockView(ScrabbleController c) {
            this.testcontroller = c;
        }

        @Override
        public RackPanel getRackPanel() {
            return new RackPanel(testcontroller) {
                @Override
                public void updateRack(Tile[] tiles) {}
            };
        }

    }

    @BeforeEach
    public void setUp() {
        model = new ScrabbleModel();
        controller = new ScrabbleController(model, null);
        view = new TestSafeMockView(controller);
        model.addView(view);
        model.addController(controller);
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEST_FILENAME);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testSaveLoadPlayers() {
        model.addPlayer("Bob");
        model.addPlayer("Marley");

        Player bob = model.getPlayers().getFirst();
        bob.setScore(50);

        model.passTurn();
        model.serializeToFile(TEST_FILENAME);

        ScrabbleModel newModel = new ScrabbleModel();

        ScrabbleController newController = new ScrabbleController(newModel, null);
        newModel.addView(new TestSafeMockView(newController));
        newModel.addController(newController);

        newModel.deserializeFromFile(TEST_FILENAME);

        assertEquals(2, newModel.getPlayers().size(), "Should be 2 players");
        assertEquals(50, newModel.getPlayers().get(0).getScore(), "Should be 50 points");
        assertEquals("Joe", newModel.getCurrentPlayer().getName(), "Should be Joe's turn");
    }


    @Test
    public void testSaveLoadUndoStack() {
        model.addPlayer("Bob");

        model.game(controller, 1);

        Player player = model.getPlayers().get(0);

        int[] rows = {7}; int[] cols = {7};
        Tile[] tiles = { new Tile('H', 4) };
        ScrabbleModel.MoveState move = new ScrabbleModel.MoveState(
                rows, cols, tiles, new Tile[7], 0
        );
        model.getUndoStack(player).push(move);

        model.serializeToFile(TEST_FILENAME);

        ScrabbleModel newModel = new ScrabbleModel();
        ScrabbleController newController = new ScrabbleController(newModel, null);
        newModel.addView(new TestSafeMockView(newController));
        newModel.addController(newController);

        newModel.deserializeFromFile(TEST_FILENAME);

        Player loadedPlayer = newModel.getPlayers().get(0);
        Stack<ScrabbleModel.MoveState> loadedStack = newModel.getUndoStack(loadedPlayer);

        assertFalse(loadedStack.isEmpty(), "Undo stack should not be empty");
        assertEquals('H', loadedStack.peek().getTiles()[0].getEffectiveLetter(), "Saved move should be 'H'");
    }

    @Test
    public void testSaveLoadBoard() {
        model.addPlayer("Bob");
        model.game(controller, 1);

        model.getBoard().setLetterAt(7, 7, 'H');
        model.getBoard().setLetterAt(0, 0, 'I');

        model.serializeToFile(TEST_FILENAME);

        ScrabbleModel newModel = new ScrabbleModel();
        ScrabbleController newController = new ScrabbleController(newModel, null);
        newModel.addView(new TestSafeMockView(newController));
        newModel.addController(newController);

        newModel.deserializeFromFile(TEST_FILENAME);

        Board loadedBoard = newModel.getBoard();
        assertEquals('H', loadedBoard.getLetterAt(7, 7), "Center should be H");
        assertEquals('I', loadedBoard.getLetterAt(0, 0), "Top Left should be I");
    }
}

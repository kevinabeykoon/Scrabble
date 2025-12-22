import java.util.ArrayList;

/**
 * This class models an event for the scrabble game
 *
 **/
public interface ScrabbleView {
    public void handleScrabbleUpdate(ScrabbleEvent e);

    void startGame(Tile[] hand);

    BoardPanel getBoardPanel();

    RackPanel getRackPanel();
}


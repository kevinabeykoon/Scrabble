import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class sets up the GUI Scrabble Board
 *
 * @author Selvia Osahon (101255573)
 * @version 04//11/2025
 */
public class ScrabbleViewFrame extends JFrame implements ScrabbleView {
    private final BoardPanel boardPanel;
    private final RackPanel rackPanel;
    private final ScorePanel scorePanel;
    private final ControlPanel controlPanel;
    private final ScrabbleModel model;
    private final ScrabbleController controller;
    private int numOfPlayers;

    private JLabel currentPlayerLabel;

    public ScrabbleViewFrame(ScrabbleModel model) {

        this.model = model;
        controller = new ScrabbleController(model, this);

        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        

        //get amount of players and their names from the user
        numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(this, "How many players are in this game?"));

        while (numOfPlayers < 2)
        {
            JOptionPane.showMessageDialog(this, "Must have at least 2 Players");

            numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(this, "How many players are in this game?"));


            if (numOfPlayers > 1)
            {
                break;
            }
        }

        for(int i = 0; i < numOfPlayers; i++){
            String playerName = JOptionPane.showInputDialog(this, ("Enter a name for Player " + (i + 1)));
            model.addPlayer(playerName);
        }

        currentPlayerLabel.setText("Current Player: " + model.getCurrentPlayer().getName());

        

        setTitle("Scrabble");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //initialize the panels
        boardPanel = new BoardPanel(controller, model);
        rackPanel = new RackPanel(controller);
        scorePanel = new ScorePanel(model.getPlayerNames());
        controlPanel = new ControlPanel(controller);        

        //add panels to the main frame
        add(boardPanel, BorderLayout.CENTER);
        add(rackPanel, BorderLayout.SOUTH);
        add(scorePanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(currentPlayerLabel, BorderLayout.NORTH);
        topPanel.add(scorePanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        pack();
        setVisible(true);

        // Calls to Game (now called ScrabbleModel)
        model.addView(this);
        model.game(controller, numOfPlayers);
    }

    //CALLED BY MODEL FOR THE START OF THE GAME
    @Override
    public void startGame(Tile[] hand){
        rackPanel.updateRack(hand);
    }

    //Methods below are for the CONTROLLER. Allows it to access to the panels
    //gives the controller access to the update methods in each panel

    /**
     * Returns the board panel, called by Controller for access.
     * @return the board panel
     */
    public BoardPanel getBoardPanel(){
        return boardPanel;
    }
    /**
     * Returns the rack panel, called by Controller for access.
     * @return the rack panel
     */
    public RackPanel getRackPanel() {
        return rackPanel;
    }
    /**
     * Returns the score panel, called by Controller for access.
     * @return the score panel
     */
    public ScorePanel getScorePanel() {
        return scorePanel;
    }
    /**
     * Returns the control panel, called by Controller for access.
     * @return the control panel
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }


    /**
     * Returns the number of players
     * @return number of players
     */
    public int getNumPlayers()
    {
        return numOfPlayers;
    }

    public ScrabbleController getController() { return controller;}

    //handles the status updates from the events
    @Override
    public void handleScrabbleUpdate(ScrabbleEvent e) {
        ScrabbleEvent.EventType eventType = e.getEventType();
        int[] rows;
        int[] cols;

        switch(eventType){
            case PASS_TURN:
                JOptionPane.showMessageDialog(this, e.getPlayer().getName() + " passed their turn.", "Game Message", JOptionPane.INFORMATION_MESSAGE);
                rackPanel.updateRack(e.getNextPlayer().getAvailableTiles());
                currentPlayerLabel.setText("Current Player: " + e.getNextPlayer().getName());
                break;

            case TILE_PLACEMENT:
                rows = e.getRow();
                cols = e.getCol();
                char[] letters = e.getTilesUsed();
                
                for(int i = 0; i < rows.length; i++){
                    boardPanel.placeTile(rows[i], cols[i], letters[i]);
                }
                // boardPanel.placeTile(e.getRow(), e.getCol(), e.getTilesUsed()); // getTilesUsed should only return a single char, not a list
                JOptionPane.showMessageDialog(this, e.getPlayer().getName() + " placed a word.", "Place Word Message", JOptionPane.INFORMATION_MESSAGE);
                currentPlayerLabel.setText("Current Player: " + e.getNextPlayer().getName());
                rackPanel.updateRack(e.getNextPlayer().getAvailableTiles());
                scorePanel.setScore(e.getPlayer().getName(), e.getNewPlayerScore());
                break;

            case UNSUCCESSFUL_TILE_PLACEMENT:
                
            /*rows = e.getRow();
                cols = e.getCol();
                for(int i = 0; i < rows.length; i++){
                    boardPanel.removeTile(rows[i], cols[i]);
                }*/

                JOptionPane.showMessageDialog(this, e.getMessage());
                JOptionPane.showMessageDialog(this, "Unsuccessful tile placement made by " + e.getPlayer().getName(), "Game Message", JOptionPane.INFORMATION_MESSAGE);

                break;

            /**
            case SUCCESSFUL_TILE_PLACEMENT:
                JOptionPane.showMessageDialog(this, e.getMessage());
                JOptionPane.showMessageDialog(this, "Successful tile placement made by " + e.getPlayer().getName(), "Game Message", JOptionPane.INFORMATION_MESSAGE);
                boardPanel.confirmPlacedTiles(e.getRows(), e.getCols());
                break;
            */

            case SWAP_TILES:
                
                JOptionPane.showMessageDialog(this, "Player " + e.getPlayer().getName() + " swapped tiles.", "Game Message", JOptionPane.INFORMATION_MESSAGE);
                
                if (e.getNextPlayer() != null) 
                { 
                    rackPanel.updateRack(e.getNextPlayer().getAvailableTiles());
                    currentPlayerLabel.setText("Current Player: " + e.getNextPlayer().getName());
                }
                break;

            case GAME_OVER:
                JOptionPane.showMessageDialog(this, "Game Over!" + e.getMessage(), "Game Message", JOptionPane.INFORMATION_MESSAGE);
                rackPanel.gameOver();
                boardPanel.disableBoard();
                break;

            default:
                break;
        }
    }

    public static void main(String[] args) {
        
    }
}

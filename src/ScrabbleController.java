import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * The ScrabbleController class handles user input and interaction logic
 * between the view and the model. It processes button events from the GUI,
 * manages tile and board selections, and triggers game actions.
 *
 * @version Nov 10, 2025
 */
public class ScrabbleController implements ActionListener
{
    private ScrabbleModel model;
    private ScrabbleViewFrame view;

    private List<JButton> selectedRackButtons = new ArrayList<>();
    private List<JButton> selectedBoardButtons = new ArrayList<>();

    /**
     * Creates a new ScrabbleController that connects the model and the view.
     * @param model the ScrabbleModel that contains the game logic
     * @param view the ScrabbleViewFrame that displays the game interface
     */
    public ScrabbleController(ScrabbleModel model, ScrabbleViewFrame view)
    {
        this.model = model;
        this.view = view;        
    }

    /**
     * Handles all button actions in the game, including placing words,
     * swapping tiles, passing turns, and selecting rack or board tiles.
     * @param e the ActionEvent triggered by a button press
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source instanceof JButton)
        {
            ControlPanel controlPanel = view.getControlPanel(); 

            RackPanel rackPanel = view.getRackPanel();
            JButton[] playerTileRack = rackPanel.getTileRack();

            BoardPanel boardPanel = view.getBoardPanel();
            JButton[][] grid = boardPanel.getTileButtonGrid();
            int size = boardPanel.getBoardSize();
            Player player = model.getCurrentPlayer();

            Player currentPlayer = model.getCurrentPlayer();

            Board board = model.getBoard();

            if (source == controlPanel.getPlaceWordButton()) 
            {   
                model.placeWordOnBoard();
            } 
            else if (source == controlPanel.getSwapButton()) 
            {
                model.swapTiles();
            } 
            else if (source == controlPanel.getPassButton()) 
            {
                model.passTurn();
            }
            else if (source == controlPanel.getUndoButton()) 
            {
                if (model.undo(currentPlayer)) 
                {
                    boardPanel.refreshBoard(board);
                }
            } 
            else if (source == controlPanel.getRedoButton()) 
            {
                if (model.redo(currentPlayer)) 
                {
                    boardPanel.refreshBoard(board);
                }
            }
            else if (source == controlPanel.getSaveButton())
            {
                model.serializeToFile(view.requestUserInformation("What file to save the game under?"));
            }
            else if (source == controlPanel.getUploadButton())
            {
                model.deserializeFromFile(view.requestUserInformation(
                        "What game file to upload? Note the # and type of players should match."));
                boardPanel.refreshBoard(model.getBoard());
            }
                
            else if (source instanceof JButton && isRackButton((JButton) source)) 
            {
                toggleTileSelection((JButton) source);
               
            }   
            else if (source instanceof JButton && isBoardButton((JButton) source, boardPanel)) 
            {
                toggleBoardSelection((JButton) source);
                
            }
        }  
    }

    /**
     * Checks if a given button belongs to the player's rack.
     * @param button the JButton to check
     * @return true if the button is part of the rack, false otherwise
     */
    private boolean isRackButton(JButton button)
    {
        RackPanel rackPanel = view.getRackPanel();
        JButton[] rack = rackPanel.getTileRack();

        for (int i = 0; i < rack.length; i++)
        {
            if (rack[i] == button)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given button belongs to the Scrabble board.
     * @param btn the JButton to check
     * @param boardPanel the BoardPanel containing the board grid
     * @return true if the button is part of the board, false otherwise
     */
    private boolean isBoardButton(JButton btn, BoardPanel boardPanel)
    {
        JButton[][] grid = boardPanel.getTileButtonGrid();
        for (int i = 0; i < grid.length; i++) 
        {
            for (int j = 0; j < grid[i].length; j++) 
            {
                if (grid[i][j] == btn) {return true;}
            }
        }
        return false;
    }

    /**
     * Toggles selection for a rack tile. Selected tiles are highlighted in yellow,
     * and deselected tiles revert to default appearance.
     * @param button the rack JButton to toggle
     */
     private void toggleTileSelection(JButton button) 
     {
        Tile[] hand = model.getCurrentPlayer().getAvailableTiles();
        int index = -1;

        JButton[] rack = view.getRackPanel().getTileRack();
        for (int i = 0; i < rack.length; i++) 
        {
            if (rack[i] == button) 
            {
                index = i;
                break;
            }
        }

        if (index == -1) return; 

        Tile clickedTile = hand[index];

        // Handle blank tile prompt
        if (!selectedRackButtons.contains(button) 
            && clickedTile.isBlank() 
            && clickedTile.getEffectiveLetter() == ' ') 
        {
            String input = javax.swing.JOptionPane.showInputDialog(
                view.getBoardPanel(), "Choose a letter for the blank tile (A-Z):");

            if (input != null && input.length() == 1 && Character.isLetter(input.charAt(0))) 
            {
                char chosenLetter = Character.toUpperCase(input.charAt(0));
                clickedTile.setAssignedLetter(chosenLetter);
                view.getRackPanel().updateRack(hand); 
            } 
            else 
            {
                return; 
            }
        }

        if (selectedRackButtons.contains(button)) 
        {
            selectedRackButtons.remove(button);
            button.setBackground(null); 
            button.setOpaque(false); 
            button.setFocusPainted(true);    

        } 
        else if (selectedRackButtons.size() < 7) 
        { 
            selectedRackButtons.add(button);
            button.setBackground(Color.YELLOW);
            button.setOpaque(true);
            button.setFocusPainted(false);            
        }
    }

    /**
     * Toggles selection for a board tile. Selected tiles are highlighted in cyan,
     * and deselected tiles revert to default appearance.
     * @param button the board JButton to toggle
     */
    private void toggleBoardSelection(JButton button) 
    {
        BoardPanel boardPanel = view.getBoardPanel();
        JButton[][] grid = boardPanel.getTileButtonGrid();

        // Find which grid cell this button is
        int row = -1, col = -1;
        for (int r = 0; r < grid.length; r++) 
            {
            for (int c = 0; c < grid[r].length; c++)
            {
                if (grid[r][c] == button) 
                {
                    row = r; col = c;
                    break;
                }
            }
        }

        if (row == -1) return;

        Color originalColor = boardPanel.getOriginalColor(row, col);

        if (selectedBoardButtons.contains(button)) 
        {
            selectedBoardButtons.remove(button);

            button.setBackground(originalColor);
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(true);
        } 
        else if (selectedBoardButtons.size() < model.getHand().length)
        {
            selectedBoardButtons.add(button);

            button.setBackground(Color.GREEN);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
        }
    }


    /**
     * Returns the list of currently selected rack buttons.
     * @return a list of JButtons representing selected rack tiles
     */
    public List<JButton> getSelectedRackButtons() { return selectedRackButtons; }

    /**
     * Returns the list of currently selected board buttons.
     * @return a list of JButtons representing selected board positions
     */
    public List<JButton> getSelectedBoardButtons() { return selectedBoardButtons; }

    /**
     * Resets the visual state of a list of buttons by clearing their background color.
     * @param buttons the list of JButtons to reset
     */
    public void resetButtons(List<JButton> buttons)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).setBackground(null);
        }
    }

}

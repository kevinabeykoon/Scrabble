import javax.swing.*;
import java.awt.*;

/**
 * RackPanel contains all the available player tiles in form of tiles
 *
 * @version 1.0.0
 */

public class RackPanel extends JPanel{
    private final int size = 7;
    private ScrabbleController controller;
    private JButton[] tiles;

    /**
     * Construct of panel for tiles
     * @param controller listener to connect these buttons to model
     */
    public RackPanel(ScrabbleController controller) {
        this.controller = controller;

        this.setLayout(new GridLayout(1, size, 5, 0));

        tiles = new JButton[size];

        for(int i = 0; i < size; i++){
            tiles[i] = new JButton("");
            tiles[i].addActionListener(controller);
            tiles[i].setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
            add(tiles[i]);
        }
    }

    /**
     * Show player current hand as tiles
     * @param hand playes current hand
     */
    public void updateRack(Tile[] hand) 
    {
        for (int i = 0; i < size; i++) 
        {
            if (hand != null && i < hand.length && hand[i] != null)
            {
                Tile tile = hand[i];
                
                if (tile.isBlank() && tile.getEffectiveLetter() == ' ') 
                {
                    tiles[i].setText("_"); 
                    tiles[i].setForeground(Color.BLACK);      
                }
                else 
                {
                    tiles[i].setText(String.valueOf(tile.getEffectiveLetter()));
                    tiles[i].setForeground(tile.isBlank() ? Color.RED : Color.BLACK);           
                }
            }
            else
            {
                tiles[i].setText(" ");
                tiles[i].setForeground(Color.BLACK);
            }
        }
    }

    /**
     * Getter of tileRack
     * @return JButton[] tiles available to current player
     */
    public JButton[] getTileRack() {
        return tiles;
    }

    /**
     * Makes all tiles disabled when game is over
     * displays ENDED:) on tiles <3
     */
    public void gameOver() {
        String[] endMessages = {"E", "N", "D", "E", "D", ":", ")"};

        for(int i = 0; i < size; i++){
            tiles[i].setText(endMessages[i]);
            tiles[i].setEnabled(false);
        }
    }
}

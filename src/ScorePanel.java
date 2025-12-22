import java.awt.GridLayout;
import java.util.Map;
import javax.swing.*;
/**
 * This class is a JPanel that consists of the 
 * Score Panel. It displays the scores of the players.
 * 
 * @version November 03, 2025
 */
public class ScorePanel extends JPanel {

    private JTextField[] playerScoreFields;
    private JLabel[] playerLabels;
    private String[] playerNames;

    /**
     * Constructor that creates a JPanel with
     * all the player's names
     * @param playerNames list that contains player names
     */
    public ScorePanel(String[] playerNames) {
    	setLayout(new GridLayout(playerNames.length, 2, 10, 5));
        setBorder(BorderFactory.createTitledBorder("Scores"));
        
        playerLabels = new JLabel[playerNames.length];
        for(int i = 0; i < playerNames.length; i++) {
        	playerLabels[i] = new JLabel(playerNames[i] + "'s Score: ");
        }
        
        playerScoreFields = new JTextField[playerNames.length];
        for(int i = 0; i < playerNames.length; i++) {
        	playerScoreFields[i] = new JTextField("0");
        	playerScoreFields[i].setEditable(false);
        	playerScoreFields[i].setHorizontalAlignment(JTextField.CENTER);
        	
        	add(playerLabels[i]);
        	add(playerScoreFields[i]);
        }
        
        this.playerNames = playerNames;
    }
    
    
    /**
     * Setting the score label of a player
     * 
     * @param playerName player's name
     * @param score new score
     * @return successfully set score
     */
    public boolean setScore(String playerName, int score) {
    	for(int i = 0; i < playerNames.length; i++) {
    		if(playerName.equals(playerNames[i])) {
    			playerScoreFields[i].setText(String.valueOf(score));
    			return true;
    		}
    	}
    	return false;
    }

    /**
    * Get the names of the players on the score panel
    * @return player names
    **/
    public String[] getPlayerNames()
    {
        return playerNames;
    }

    /*
    * Loop through the provided scores map and update each player's score
    **/
    public void refreshAllScores(Map<String, Integer> scores) {
        for (int i = 0; i < playerNames.length; i++) {
            String playerName = playerNames[i];
            if (scores.containsKey(playerName)) {
                playerScoreFields[i].setText(String.valueOf(scores.get(playerName)));
            }
        }
    }
}

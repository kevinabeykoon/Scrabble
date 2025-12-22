import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class is a JPanel that consists of the 
 * Board Panel. It contains a 15x15 grid of buttons.
 * When a tile (button) is pressed, it is selected.
 * Once the selected buttons are confirmed, they 
 * become disabled.
 * 
 * @version November 03, 2025
 *
 */
public class BoardPanel extends JPanel{
	JButton[][] tileButtonGrid;
	ScrabbleController controller;
	private int[][] tileLetterMultiplier;
	private int[][] tileWordMultiplier;

	private List<Integer> playerScores;

	private Color[][] originalColors;

	/**
	 * Constructor that creates a BoardPanel with the 
	 * related controller.
	 * 
	 * @param scrabbleController controller
	 */
	public BoardPanel(ScrabbleController scrabbleController, ScrabbleModel model) {
		controller = scrabbleController;

		Board board = model.getBoard();
		setLayout(new GridLayout(Board.SIZE,Board.SIZE));

		tileLetterMultiplier = board.getTileLetterMultiplier();
		tileWordMultiplier = board.getTileWordMultiplier();

		tileButtonGrid = new JButton[Board.SIZE][Board.SIZE];
		for(int i = 0; i < Board.SIZE; i++) {
			for(int j = 0; j < Board.SIZE; j++) {
				tileButtonGrid[i][j] = new JButton(" ");
				tileButtonGrid[i][j].setActionCommand(i+","+j);
				tileButtonGrid[i][j].addActionListener(controller);
				// Set colours based on multipliers
				if(tileLetterMultiplier[i][j] == 2){
					tileButtonGrid[i][j].setBackground(Color.CYAN);
				} else if (tileLetterMultiplier[i][j] == 3){
					tileButtonGrid[i][j].setBackground(Color.BLUE);
				} else if (tileWordMultiplier[i][j] == 2){
					tileButtonGrid[i][j].setBackground(Color.PINK);
				} else if(tileWordMultiplier[i][j] == 3){
					tileButtonGrid[i][j].setBackground(Color.RED);
				}
				add(tileButtonGrid[i][j]);
			}
		}

		originalColors = new Color[Board.SIZE][Board.SIZE];

		for (int r = 0; r < Board.SIZE; r++) 
		{
			for (int c = 0; c < Board.SIZE; c++) 
			{
				originalColors[r][c] = tileButtonGrid[r][c].getBackground();
			}
		}
	}

	/**
	 * Return the color for a button
	 * @param row the button's row
	 * @param col the button's col
	 * @return color corresponding to button
	 */
	public Color getOriginalColor(int row, int col) 
	{
    	return originalColors[row][col];
	}


	/**
	 * Update the display board with a new board
	 * @param board the board to replace
	 */
	public void refreshBoard(Board board) 
	{
		JButton[][] grid = this.getTileButtonGrid();


		for (int r = 0; r < Board.SIZE; r++) 
		{
			for (int c = 0; c < Board.SIZE; c++) 
			{
				char letter = board.getLetterAt(r, c);

				JButton btn = grid[r][c];

				if (letter == ' ' || letter == '\0') 
				{
					btn.setText(""); // clear the letter
				} 
				else 
				{
					btn.setText(String.valueOf(letter)); // restore letter
				}

				
			}
		}

		this.revalidate();
		this.repaint();
	}



	
	/**
	 * This functions places a tile on the screen by
	 * selecting a button (visual effect) and setting
	 * the letter onto the button.
	 * 
	 * @param x x-coordinate on grid
	 * @param y y-coordinate on grid
	 * @param letter letter to place on grid
	 */
	public void placeTile(int x, int y, char letter) 
	{
		JButton b = tileButtonGrid[x][y];

		char displayLetter = letter == ' ' ? ' ' : letter;


		b.setText(String.valueOf(displayLetter));
		b.setForeground(letter == ' ' ? Color.GRAY : Color.BLACK);

		b.setOpaque(true);
		b.setContentAreaFilled(true);
	}
	
	/**
	 * This functions removes a tile on the screen by
	 * unselecting a button (visual effect) and clearing
	 * the letter off the button.
	 * 
	 * @param x x-coordinate on grid
	 * @param y y-coordinate on grid
	 */
	public void removeTile(int x, int y) {
		tileButtonGrid[x][y].setText("");
		tileButtonGrid[x][y].setBackground(null);
	}
	
	/**
	 * This function confirms the placement
	 * of the selected tiles by disabling the
	 * tiles (buttons)
	 * 
	 * @param wordX x-coordinates of each character in the word
	 * @param wordY y-coordinates of each character in the word
	 */
	public void confirmPlacedTiles(int[] wordX, int[] wordY) {
		for(int i = 0; i < wordX.length; i++) {
			tileButtonGrid[wordX[i]][wordY[i]].setEnabled(false);
			tileButtonGrid[wordX[i]][wordY[i]].setBackground(null);
		}
	}
	
	/**
	 * Disables the entire board by
	 * disabling all the buttons.
	 */
	public void disableBoard() {
		for(int i = 0; i < Board.SIZE; i++) {
			for(int j = 0; j < Board.SIZE; j++) {
				tileButtonGrid[i][j].setEnabled(false);
			}
		}
	}

	/**
	 * Returns the tile button grid
	 * 
	 * @return the button tile grid
	 */
	public JButton[][] getTileButtonGrid()
	{
		return tileButtonGrid;
	}

	/**
	 * Return the board size
	 * 
	 * @return constant board size
	 */
	public int getBoardSize()
	{
		return Board.SIZE;
	}

	/**
	 * Returns the letter currently on the board at the given coordinates.
	 * If the square is empty, returns '\0'.
	 *
	 * @param x x-coordinate on the grid
	 * @param y y-coordinate on the grid
	 * @return the character on the board or '\0' if empty
	 */
	public char getEffectiveLetterAt(int x, int y) 
	{
		String text = tileButtonGrid[x][y].getText().trim();
		if (text.isEmpty()) 
		{
			return ' '; 
		}
		return text.charAt(0);
	}

	
	/**
	 * Clears the background colors and borders of all the buttons and restores them to their original state.
	 */
	public void clearButtonColors() {
		for (int i = 0; i < Board.SIZE; i++) {
			for (int j = 0; j < Board.SIZE; j++) {
				tileButtonGrid[i][j].setBackground(originalColors[i][j]);

				tileButtonGrid[i][j].setOpaque(false);
				tileButtonGrid[i][j].setContentAreaFilled(false);
			}
		}
	}

	/**
	 * Highlights the placed tiles on the board by restoring their original color
	 * based on the stored originalColors and making them opaque.
	 */
	public void highlightPlacedTiles() {
		for (int i = 0; i < tileButtonGrid.length; i++) {
			for (int j = 0; j < tileButtonGrid[i].length; j++) {
				String buttonText = tileButtonGrid[i][j].getText().trim();

				if (!buttonText.isEmpty()) { 
					char letter = buttonText.charAt(0);  

					if (letter != ' ' && letter != '\0') { 
						JButton btn = tileButtonGrid[i][j];

						btn.setBackground(originalColors[i][j]);

						btn.setOpaque(true);
						btn.setContentAreaFilled(true);
					}
				}
			}
		}
	}
}

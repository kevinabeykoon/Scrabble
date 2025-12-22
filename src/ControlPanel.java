import javax.swing.*;
import java.awt.*;

/**
 * ControlPanel contains all the available player moves in form of buttons
 *
 * @version 1.0.0
 */

public class ControlPanel extends JPanel {
    private ScrabbleController controller;
    private JButton placeWordButton;
    private JButton swapButton;
    private JButton passButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton saveButton;
    private JButton uploadButton;

    /**
     * Construct panel of buttons
     * @param controller listener to connect these buttons to model
     */
    public ControlPanel(ScrabbleController controller) {
        this.controller = controller;
        placeWordButton = new JButton("Place Word");
        swapButton = new JButton("Swap");
        passButton = new JButton("Pass");
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        saveButton = new JButton("Save Game");
        uploadButton = new JButton("Upload Game");

        this.setLayout(new BorderLayout(0, 10));

        JPanel mainActions = new JPanel(new GridLayout(1, 3, 10, 0));
        mainActions.add(placeWordButton);
        mainActions.add(swapButton);
        mainActions.add(passButton);

        JPanel undoRedoPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        undoRedoPanel.add(undoButton);
        undoRedoPanel.add(redoButton);
        undoRedoPanel.add(saveButton);
        undoRedoPanel.add(uploadButton);

        undoRedoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        this.add(mainActions, BorderLayout.CENTER);
        this.add(undoRedoPanel, BorderLayout.SOUTH);


        commonButtonSetUp(placeWordButton);
        commonButtonSetUp(swapButton);
        commonButtonSetUp(passButton);
        commonButtonSetUp(undoButton);
        commonButtonSetUp(redoButton);
        commonButtonSetUp(saveButton);
        commonButtonSetUp(uploadButton);

        swapButton.setForeground(new Color(199, 154, 0));
        placeWordButton.setForeground(new Color(47, 117, 0));
        passButton.setForeground(new Color(154, 38, 38));

        /** 
        this.add(placeWordButton);
        this.add(swapButton);
        this.add(passButton);
        this.add(undoButton);
        this.add(redoButton); */

        placeWordButton.addActionListener(controller);
        swapButton.addActionListener(controller);
        passButton.addActionListener(controller);
        undoButton.addActionListener(controller);
        redoButton.addActionListener(controller);
        uploadButton.addActionListener(controller);
        saveButton.addActionListener(controller);

    }

    /**
     * Set up common button visuals
     * @param button that needs edit
     */
    private void commonButtonSetUp(JButton button) {
        button.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * getter for swap button
     * @return Swap button
     */
    public JButton getSwapButton() {
        return swapButton;
    }

    /**
     * getter for pass button
     * @return pass button
     */
    public JButton getPassButton() {
        return passButton;
    }

    /**
     * getter for place button
     * @return place button
     */
    public JButton getPlaceWordButton() {
        return placeWordButton;
    }

    /**
     * getter for undo button
     * @return undo button
     */
    public JButton getUndoButton()
    {
        return undoButton;
    }

    /**
     * getter for redo button
     * @return redo button
     */
    public JButton getRedoButton()
    {
        return redoButton;
    }

    /**
     * getter for save button
     * @return save button
     */
    public JButton getSaveButton()
    {
        return saveButton;
    }

    /**
     * getter for upload button
     * @return upload button
     */
    public JButton getUploadButton()
    {
        return uploadButton;
    }
}

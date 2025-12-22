/**
 * BoardObserver class is charge of making sure that the model notifies the Ai class that the board has changed
 * @version 1.0
 */
public interface BoardObserver {
    void boardChanged(int[] rows, int[] cols, char[] sortedTiles);
}

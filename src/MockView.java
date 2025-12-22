public class MockView implements ScrabbleView {

    public ScrabbleEvent lastEvent = null;

    @Override
    public void handleScrabbleUpdate(ScrabbleEvent e) {
        this.lastEvent = e;
    }

    @Override
    public void startGame(Tile[] hand) {
        
    }
    @Override 
    public BoardPanel getBoardPanel() { return null;}

    @Override
    public RackPanel getRackPanel() { return null;}
}

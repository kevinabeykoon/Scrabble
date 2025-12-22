import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the tile Class
 *
 * 
 */
class TileTest {

    @Test
    public void testGetTileValidLetterA() {
        Tile tile = Tile.getTile('A');
        assertNotNull(tile,"Should not be null");
        assertEquals('A', tile.getEffectiveLetter(),"Should be A");
        assertEquals(1, tile.getValue(),"Should be 1");
    }
    @Test
    public void testGetTileValidLetterZ() {
        Tile tile = Tile.getTile('Z');
        assertNotNull(tile,"Should not be null");
        assertEquals('Z', tile.getEffectiveLetter(),"Should be Z");
        assertEquals(10, tile.getValue(),"Should be 10");
    }

    @Test
    public void testGetTileInvalidLetter() {
        Tile tile = Tile.getTile('.');
        assertNull(tile,"Should be null, not in ALL_TILES");
    }

    @Test
    public void testGetTileSameInstance() {
        Tile tile = Tile.getTile('A');
        Tile tileTwin = Tile.getTile('A');
        assertSame(tile, tileTwin,"Should be same -> same letter");
    }

    @Test
    public void testGetTileValidLetter() {
        Tile tile = Tile.getTile('A');
        assertNotNull(tile,"Should not be null");
        assertEquals('A', tile.getEffectiveLetter(),"Should be A");
        assertEquals(1, tile.getValue(),"Should be 1");
    }

    @Test
    public void getLetter() {
        Tile tile = Tile.getTile('C');
        assertEquals('C', tile.getEffectiveLetter(),"Should be C");
    }

    @Test
    public void getValue() {
        Tile tile = Tile.getTile('D');
        assertEquals(2, tile.getValue(),"Should be 2 for D");
    }

    @Test
    public void testToString() {
        Tile tile = Tile.getTile('E');
        assertEquals("E", tile.toString(),"Should be E");
    }

    @Test
    public void testConstructorBlankTile() {
        Tile tile = new Tile('n', 8, true);
        assertEquals(' ', tile.getAssignedLetter(),"Should be ' '");
        assertEquals(0, tile.getValue(),"Should be 0");

    }

    @Test
    public void testConstructorNoIsBlankParam() {
        Tile tile = new Tile('n', 8);
        assertEquals("N", tile.toString(),"Should be N");
    }

    @Test
    public void testCreateLetter() {
        Tile tile = Tile.createLetter('N', 8);
        assertEquals("N", tile.toString(),"Should be N");
    }

    @Test
    public void testCreateBlank() {
        Tile tile = Tile.createBlank();
        assertEquals(" ", tile.toString(),"Should be ' '");
    }

    @Test
    public void testSetAssignedLetterOfNotBlankTile() {
        Tile tile = Tile.createLetter('N',2);
        tile.setAssignedLetter('A');
        assertEquals("N", tile.toString(),"Should be A");
    }

    @Test
    public void testSetAssignedLetterOfBlankTile() {
        Tile tile = Tile.createBlank();
        tile.setAssignedLetter('A');
        assertEquals('A', tile.getAssignedLetter(),"Should be A");
    }

    @Test
    public void testGetAssignedLetter() {
        Tile tile = Tile.createBlank();
        assertEquals(' ', tile.getAssignedLetter(),"Should be ' '");
    }
}

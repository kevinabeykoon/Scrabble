import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the tileBag Class
 *
 */

class TileBagTest {

    private TileBag tileBag;

    @BeforeEach
    public void setUp() {
        tileBag = new TileBag();
    }

    @Test
    public void TileBag_initialSize() {
        assertEquals(102,tileBag.size(), "Should be 102");
    }

    @Test
    public void PickFromBag_nonEmptyBag() {
        int initialSize = tileBag.size();
        Tile pickedTile = tileBag.pickFromBag();

        assertNotNull(pickedTile, "Picked tile should not be null");
        assertEquals(initialSize-1,tileBag.size(), "Bag size should decrease (before:"+ initialSize+")");
    }

    @Test
    public void PickFromBag_emptyBag() {
        int initialSize = tileBag.size();
        for (int i=0;i<initialSize;i++) {
            assertNotNull(tileBag.pickFromBag(), "Picked tiles should not be null");
        }

        assertEquals(0,tileBag.size(), "Bag size should be 0 since now empty");

        Tile pickedTile = tileBag.pickFromBag();
        assertNull(pickedTile, "Picked tile should be null since from empty bag");
        assertEquals(0,tileBag.size(), "Bag size should remain 0 since now empty");
    }

    @Test
    public void ReturnTileToBag() {
        int initialSize = tileBag.size();
        Tile pickedTile = tileBag.pickFromBag();
        Tile pickedTile2 = tileBag.pickFromBag();
        Tile pickedTile3 = tileBag.pickFromBag();
        int sizeAfterPick = tileBag.size();
        assertNotNull(pickedTile, "Picked tile should not be null");
        assertEquals(sizeAfterPick,tileBag.size(), "Bag size should decrease (before:"+ initialSize+")");
        tileBag.returnTileToBag(pickedTile);
        tileBag.returnTileToBag(pickedTile2);
        tileBag.returnTileToBag(pickedTile3);

        assertEquals(initialSize,tileBag.size(), "Bag size should be back to initial size (before:"+ initialSize+")");
    }

    @Test
    public void Size() {
       assertEquals(102,tileBag.size(), "Bag size should be 102");

        Tile pickedtile = tileBag.pickFromBag();
        assertEquals(101, tileBag.size(), "Bag size should be 101 after one pick.");

        tileBag.returnTileToBag(pickedtile);
        assertEquals(102, tileBag.size(), "Bag size should be 102 after a pick and a return.");
    }

    @Test
    public void toString_bag() {
        String toString = tileBag.toString();

        assertTrue(toString.startsWith("TileBag: \n"), "Should start with TileBag");
        assertTrue(toString.contains("A"), "Should contain A if not empty");
        assertTrue(toString.contains("Z"), "Should contain Z if not empty");
        assertFalse(toString.contains("No tiles in this bag\n"), "Should not contain No tiles in this bag if not empty");
    }

    @Test
    public void ToString_emptyBag() {
        int initialSize = tileBag.size();
        for (int i=0;i<initialSize;i++) {
            assertNotNull(tileBag.pickFromBag(), "Picked tiles should not be null");
        }
        assertEquals(0,tileBag.size(), "Bag size should be 0 since now empty");

        String toString = tileBag.toString();
        assertEquals("TileBag: \nNo tiles in this bag\n", toString, "TileBag should be empty"+toString);
    }
}

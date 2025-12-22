import java.util.*;
import java.io.Serializable;

/**
 * TileBag Class -> Keeps note of the letters available to be picked by the players
 * Frequency of letter as set by the rules (wiki)
 *

 */


public class TileBag implements Serializable  {
    private Map<Tile, Integer> tiles;
    private Random random;

    private static final Tile BLANK_TILE = Tile.createBlank();

    /**
     * Populates tileBag as needed with the set frequencies of each tile
     */
    TileBag() {
        tiles = new HashMap<>();
        random = new Random();

        
        tiles.put(Tile.getTile('A'),10);
        tiles.put(Tile.getTile('B'),2);
        tiles.put(Tile.getTile('C'),2);
        tiles.put(Tile.getTile('D'),4);
        tiles.put(Tile.getTile('E'),12);
        tiles.put(Tile.getTile('F'),2);
        tiles.put(Tile.getTile('G'),3);
        tiles.put(Tile.getTile('H'),2);
        tiles.put(Tile.getTile('I'),9);
        tiles.put(Tile.getTile('J'),1);
        tiles.put(Tile.getTile('K'),1);
        tiles.put(Tile.getTile('L'),4);
        tiles.put(Tile.getTile('M'),2);
        tiles.put(Tile.getTile('N'),6);
        tiles.put(Tile.getTile('O'),8);
        tiles.put(Tile.getTile('P'),2);
        tiles.put(Tile.getTile('Q'),1);
        tiles.put(Tile.getTile('R'),7);
        tiles.put(Tile.getTile('S'),4);
        tiles.put(Tile.getTile('T'),6);
        tiles.put(Tile.getTile('U'),4);
        tiles.put(Tile.getTile('V'),2);
        tiles.put(Tile.getTile('W'),2);
        tiles.put(Tile.getTile('X'),1);
        tiles.put(Tile.getTile('Y'),2);
        tiles.put(Tile.getTile('Z'),1);
        tiles.put(BLANK_TILE, 2);
    }

    /**
     * Picks a random tile from the bag and decrements the count of that letter when picked
     * removes from bag if there is 0
     * @return random tile picked is null if bag is empty
     */
    public Tile pickFromBag() {
        if (tiles.isEmpty()) {
            System.out.println("No tiles in this bag");
            return null;
        }

        int randomNumber = random.nextInt(this.size());

        Tile pickedTile = null;

        for (Map.Entry<Tile, Integer> entry : tiles.entrySet()) {
            Tile currentTile = entry.getKey();
            int count =  entry.getValue();

            randomNumber -= count;
            if (randomNumber < 0) {
                pickedTile = currentTile;
                break;
            }
        }

        if (pickedTile != null) {
            int currentCount = tiles.get(pickedTile);
            if (currentCount > 1) {
                tiles.put(pickedTile, currentCount - 1);
            }
            else {
                tiles.remove(pickedTile);
            }
        }

        if (pickedTile != null && pickedTile.isBlank()) 
        {
            pickedTile = BLANK_TILE;
        }


        return pickedTile;
    }

    /**
     * Returns the tile given to the bag
     * @param tile that needs to be added back
     */
    public void returnTileToBag(Tile tile) {
        tiles.put(tile, tiles.getOrDefault(tile, 0) + 1);
    }

    /**
     * Returns count of the total letters in the bag
     * @return size of the bag
     */
    public int size() {
        int total = 0;
        for (Map.Entry<Tile, Integer> tile : tiles.entrySet()) {
            total += tile.getValue();
        }
        return total;
    }

    /**
     * String representation of the bag
     * "Tilebag: A, A, B, C, C"
     * if bag is empty
     * "TileBag: No tiles in this bag
     * @return current state of the bag as a string
     */

    @Override
    public String toString() {
        StringBuilder bagState = new StringBuilder("TileBag: \n");

        if (tiles.isEmpty()) {
            bagState.append("No tiles in this bag\n");
        }

        List<String> tileStrings = new ArrayList<>();

        for (Map.Entry<Tile, Integer> entry : tiles.entrySet()) {
            String letter = entry.getKey().toString();
            int count = entry.getValue();

            for (int i = 1; i <= count; i++) {
                tileStrings.add(letter);
            }
        }

        tileStrings.sort(String::compareTo);

        bagState.append(String.join(", ", tileStrings));

        return bagState.toString();
    }
}

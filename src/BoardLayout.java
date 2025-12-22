import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

/**
 * This class represents the board premium square layout. This is used as multipliers for the players score from
 * placing a word. XML format is used for the files. Takes XML files and TXT files with XML content.
 *
 */
public class BoardLayout {
    private static final int SIZE = 15; // Board size is constant
    private int[][] tileLetterMultiplier; // For letter multipliers
    private int[][] tileWordMultiplier;   // For word multipliers

    /**
     * Initialize the multipliers
     */
    public BoardLayout() {
        this.tileLetterMultiplier = letterMultiplierOriginal(); // Use default letter multipliers
        this.tileWordMultiplier = wordMultiplierOriginal();    // Use default word multipliers
    }

    /**
     * Initializes letter multipliers
     *
     * @return letter multipliers
     */
    private int[][] initTileLetterMultiplier() {
        tileLetterMultiplier = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                tileLetterMultiplier[i][j] = 1;
            }
        }
        return tileLetterMultiplier;
    }

    /**
     * Initializes word multipliers
     *
     * @return word multipliers
     */
    private int[][] initTileWordMultiplier() {
        int[][] tileWordMultiplier = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                tileWordMultiplier[i][j] = 1;
            }
        }
        return tileWordMultiplier;
    }


    /**
     * Initializes default letter multipliers
     *
     * @return letter multipliers
     */
    public int[][] letterMultiplierOriginal() {

        int[][] tileLetterMultiplier;
        tileLetterMultiplier = initTileLetterMultiplier();

        // Two Point Letter Multipliers - LIGHT BLUE ON THE BOARD GUI
        tileLetterMultiplier[0][3] = 2;
        tileLetterMultiplier[0][11] = 2;
        tileLetterMultiplier[2][6] = 2;
        tileLetterMultiplier[2][8] = 2;
        tileLetterMultiplier[3][0] = 2;
        tileLetterMultiplier[3][7] = 2;
        tileLetterMultiplier[3][14] = 2;
        tileLetterMultiplier[6][2] = 2;
        tileLetterMultiplier[6][6] = 2;
        tileLetterMultiplier[6][8] = 2;
        tileLetterMultiplier[6][12] = 2;
        tileLetterMultiplier[7][3] = 2;
        tileLetterMultiplier[7][11] = 2;
        tileLetterMultiplier[8][2] = 2;
        tileLetterMultiplier[8][6] = 2;
        tileLetterMultiplier[8][8] = 2;
        tileLetterMultiplier[8][12] = 2;
        tileLetterMultiplier[11][0] = 2;
        tileLetterMultiplier[11][7] = 2;
        tileLetterMultiplier[11][14] = 2;
        tileLetterMultiplier[12][6] = 2;
        tileLetterMultiplier[12][8] = 2;
        tileLetterMultiplier[14][3] = 2;
        tileLetterMultiplier[14][11] = 2;

        //Three Point Letter Multipliers - DARK BLUE ON THE GUI
        tileLetterMultiplier[1][5] = 3;
        tileLetterMultiplier[1][9] = 3;
        tileLetterMultiplier[5][1] = 3;
        tileLetterMultiplier[5][5] = 3;
        tileLetterMultiplier[5][9] = 3;
        tileLetterMultiplier[5][13] = 3;
        tileLetterMultiplier[9][1] = 3;
        tileLetterMultiplier[9][5] = 3;
        tileLetterMultiplier[9][9] = 3;
        tileLetterMultiplier[9][13] = 3;
        tileLetterMultiplier[13][5] = 3;
        tileLetterMultiplier[13][9] = 3;

        return tileLetterMultiplier;
    }

    /**
     * Initializes default word multipliers
     *
     * @return word multipliers
     */
    public int[][] wordMultiplierOriginal() {

        int[][] tileWordMultiplier;
        tileWordMultiplier = initTileWordMultiplier();

        //Two Point Word Multipliers - PINK ON THE BOARD GUI
        tileWordMultiplier[1][1] = 2;
        tileWordMultiplier[1][13] = 2;
        tileWordMultiplier[2][2] = 2;
        tileWordMultiplier[2][12] = 2;
        tileWordMultiplier[3][3] = 2;
        tileWordMultiplier[3][11] = 2;
        tileWordMultiplier[4][4] = 2;
        tileWordMultiplier[4][10] = 2;
        tileWordMultiplier[7][7] = 2;
        tileWordMultiplier[10][4] = 2;
        tileWordMultiplier[10][10] = 2;
        tileWordMultiplier[11][3] = 2;
        tileWordMultiplier[11][11] = 2;
        tileWordMultiplier[12][2] = 2;
        tileWordMultiplier[12][12] = 2;
        tileWordMultiplier[13][1] = 2;
        tileWordMultiplier[13][13] = 2;

        //Three Point Word Multipliers - RED ON THE BOARD GUI
        tileWordMultiplier[0][0] = 3;
        tileWordMultiplier[0][7] = 3;
        tileWordMultiplier[0][14] = 3;
        tileWordMultiplier[7][0] = 3;
        tileWordMultiplier[7][14] = 3;
        tileWordMultiplier[14][0] = 3;
        tileWordMultiplier[14][7] = 3;
        tileWordMultiplier[14][14] = 3;

        return tileWordMultiplier;
    }

    /**
     * Loads multipliers from xml or txt file and sets the layout.
     * Supports both .xml files and .txt files containing XML formatted data
     * Format of the file should be:
     *
     * <board>
     *      <multiplier type="letter" x="0" y="3" value="2"/>
     *      ...
     *      <multiplier type="word" x="0" y="0" value="3"/>
     * </board>
     *
     * @param filepath the file location to retrieve the custom layout
     */
    public void loadCustomLayout(String filepath){
        try{
            File file = findLayoutFile(filepath);

            if (file == null) {
                System.err.println("Error: Could not find layout file with path: " + filepath);
                System.err.println("Tried extensions: .xml, .txt");
                return;
            }

            int[][] letterMultipliers = initTileLetterMultiplier();
            int[][] wordMultipliers = initTileWordMultiplier();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList multipliers = document.getElementsByTagName("multiplier");

            for (int i = 0; i < multipliers.getLength(); i++) {
                Node node = multipliers.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String type = element.getAttribute("type");
                    try {
                        int x = Integer.parseInt(element.getAttribute("x"));
                        int y = Integer.parseInt(element.getAttribute("y"));
                        int value = Integer.parseInt(element.getAttribute("value"));

                        // Validate coordinates
                        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
                            if ("letter".equals(type)) {
                                letterMultipliers[x][y] = value;
                            } else if ("word".equals(type)) {
                                wordMultipliers[x][y] = value;
                            } else {
                                System.err.println("Warning: Unknown multiplier type '" + type +
                                        "' at (" + x + ", " + y + "). Skipping.");
                            }
                        }
                    } catch (NumberFormatException e){
                        System.err.println("Warning: Invalid number format in multiplier element. Skipping.");
                    }
                }
            }

            tileLetterMultiplier = letterMultipliers;
            tileWordMultiplier = wordMultipliers;

        } catch (Exception e){
            System.out.println("Error loading custom layout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Finds a layout file with the given filepath, trying common extensions.
     * Tries in order: exact path, .xml, .txt
     *
     * @param filepath the base filepath without extension
     * @return the File object if found, null otherwise
     */
    private File findLayoutFile(String filepath) {
        // Try exact path first (in case user included extension)
        File file = new File(filepath);
        if (file.exists() && file.isFile()) {
            return file;
        }

        // Try with .xml extension
        file = new File(filepath + ".xml");
        if (file.exists() && file.isFile()) {
            return file;
        }

        // Try with .txt extension
        file = new File(filepath + ".txt");
        if (file.exists() && file.isFile()) {
            return file;
        }

        // File not found with any extension
        return null;
    }

    /**
     * Returns the letter multipliers
     *
     * @return letter multipliers
     */
    public int[][] getLetterMultipliers() {
        return tileLetterMultiplier;
    }

    /**
     * Returns the word multipliers
     *
     * @return word multipliers
     */
    public int[][] getWordMultipliers() {
        return tileWordMultiplier;
    }

}

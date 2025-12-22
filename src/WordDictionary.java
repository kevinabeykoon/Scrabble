import java.io.*;
import java.util.*;
/**
 * WordDictionary.java is a helper class. It stores all the valid words that cna be played
 * in the game.
 *
 */
public class WordDictionary implements Serializable {
    private transient Set<String> words;  //set of all valid words in the game

    /**
     * Creates a dictioner of all valid words in the game. It takes an input text file (.txt)
     * and organizes all the words in the file into a set.
     *
     * @param filename
     * @throws IOException
     */
    public WordDictionary(String filename){
        //exception handling added for file reading
        words = new HashSet<>();
        try{
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String word;
            while ((word = br.readLine()) != null) {
                words.add(word.toUpperCase());
            }
            br.close();
        } catch (FileNotFoundException e){
            // catch exception for if the file is not found
            System.out.println("File not found: " + filename);
        } catch (IOException e){
            // catch exception if there is an I/O issue that affects the buffer from reading
            // the file
            System.out.println("An I/O error occurred while reading from the file: " + filename);
        } catch (Exception e){
            // catch exception for any unexpected issues
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if the inputted word is valid. A word is valid if it can be found within
     * the dictionary.
     *
     * @param word The word to be checked
     * @return  True if the word is valid, False otherwise
     */
    public boolean isValidWord(String word){
        return words.contains((word.toUpperCase()));
    }

    /**
     * Getter of words
     * @return list of valid words
     */
    public Set<String> getWords(){
        return words;
    }
}

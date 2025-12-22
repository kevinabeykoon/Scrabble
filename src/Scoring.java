import java.util.*;

/**
 * This class will keep track of letter points for scoring purposes.
 * Will also implement the method for calculating the point score of a valid word.
 *
 * @version 21/10/2025
 */
public class Scoring {
    private static final Map<Character, Integer> LETTER_VALUES = new HashMap<>();
    static{
        LETTER_VALUES.put('A', 1); LETTER_VALUES.put('B', 3); LETTER_VALUES.put('C', 3);
        LETTER_VALUES.put('D', 2); LETTER_VALUES.put('E', 1); LETTER_VALUES.put('F', 4);
        LETTER_VALUES.put('G', 2); LETTER_VALUES.put('H', 4); LETTER_VALUES.put('I', 1);
        LETTER_VALUES.put('J', 8); LETTER_VALUES.put('K', 5); LETTER_VALUES.put('L', 1);
        LETTER_VALUES.put('M', 3); LETTER_VALUES.put('N', 1); LETTER_VALUES.put('O', 1);
        LETTER_VALUES.put('P', 3); LETTER_VALUES.put('Q', 10); LETTER_VALUES.put('R', 1);
        LETTER_VALUES.put('S', 1); LETTER_VALUES.put('T', 1); LETTER_VALUES.put('U', 1);
        LETTER_VALUES.put('V', 4); LETTER_VALUES.put('W', 4); LETTER_VALUES.put('X', 8);
        LETTER_VALUES.put('Y', 4); LETTER_VALUES.put('Z', 10);
    }

    /**
     * Gets the value of the tile depending on what letter that tile represents
     *
     * @param letter
     * @return the value of the inputted letter. If letter is blank, a default value
     * of 0 is returned.
     */
    public static int getLetterValue(char letter){
        return LETTER_VALUES.getOrDefault(Character.toUpperCase(letter), 0);
    }

    /**
     * Calculates and returns a score based on the words provided
     * @param words The string of words which need their score calculated
     * @param letterMultiplier the letter multiplier applied any letters in the word
     * @return The total score of all words in the list
     */
    public static int calculateWordScore(List<String> words, List<Integer> letterMultiplier){
        int score = 0;
        int i = 0;

        for(String word : words){
            for (char letter : word.toCharArray()){
                // Use bounds checking to prevent crashes
                // If no multiplier provided for this letter, use 1 as default
                int multiplier = (i < letterMultiplier.size()) ? letterMultiplier.get(i) : 1;
                score += getLetterValue(letter) * multiplier;
                i++;
            }
        }
        return score;
    }
}

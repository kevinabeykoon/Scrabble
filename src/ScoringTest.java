import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Scoring class.
 *
 * Tests Scoring functionality, including:
 * - Retrieving the value of individual letters
 * - Calculating the total score of single and multiple words
 *
 */
class ScoringTest {

    @Test
    void testGetLetterValue() {
        assertEquals(1, Scoring.getLetterValue('A'));
        assertEquals(3, Scoring.getLetterValue('B'));
        assertEquals(10, Scoring.getLetterValue('Q'));
        assertEquals(0, Scoring.getLetterValue(' '));
    }

    @Test
    void testCalculateWordScore() {
        List<String> words = new ArrayList<>();
        words.add("TEST");
        words.add("HI");
        List<Integer> multipliers = new ArrayList<>(){};
        int score = Scoring.calculateWordScore(words, multipliers);
        int expectedScore = Scoring.getLetterValue('T') * 2+
                Scoring.getLetterValue('E') +
                Scoring.getLetterValue('S') +
                Scoring.getLetterValue('H') +
                Scoring.getLetterValue('I');
        assertEquals(expectedScore, score);
    }

    @Test
    void testCalculateWordScoreWithMultipliers() {
        List<String> words = new ArrayList<>();
        words.add("TEST");
        words.add("HI");
        List<Integer> multipliers = new ArrayList<>(Arrays.asList(2, 3, 4, 5, 2));
        int score = Scoring.calculateWordScore(words, multipliers);
        int expectedScore = Scoring.getLetterValue('T') * 2 +
                Scoring.getLetterValue('E') * 3 +
                Scoring.getLetterValue('S') * 4 +
                Scoring.getLetterValue('T') * 5 +
                Scoring.getLetterValue('H') * 2 +
                Scoring.getLetterValue('I');
        assertEquals(expectedScore, score);
    }

    @Test
    void testCalculateWordScoreWithSecondWordMultipliers() {
        List<String> words = new ArrayList<>();
        words.add("TEST");
        words.add("HI");
        List<Integer> multipliers = new ArrayList<>(Arrays.asList(1, 1, 1, 1, 2));
        int score = Scoring.calculateWordScore(words, multipliers);
        int expectedScore = Scoring.getLetterValue('T') +
                Scoring.getLetterValue('E') +
                Scoring.getLetterValue('S') +
                Scoring.getLetterValue('T') +
                Scoring.getLetterValue('H') * 2 +
                Scoring.getLetterValue('I');
        assertEquals(expectedScore, score);
    }
}

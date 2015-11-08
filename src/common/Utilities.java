package common;

import java.util.Arrays;
import java.util.Date;

/**
 * Class containing miscellaneous functions for any component to use. Should not
 * have dependency on non-java classes
 */
public final class Utilities {

    // @@author A0125546E
    /**
     * Evaluates if start and end dates are valid where end is after start
     * 
     * @param dateStart
     * @param dateEnd
     * @return true if either start or end is null. false if start is after end
     */
    public static final boolean verifyDate(Date dateStart, Date dateEnd) {
        if (dateStart != null && dateEnd != null && dateStart.after(dateEnd)) {
            return false;
        }
        return true;
    }

    public static final String formatString(String s, Object... args) {
        String formatted = String.format(s, args);
        return formatted;

    }

    /**
     * Provides the beginning of time in Unix term
     * 
     * @return the epoch
     */
    public static Date absBeginningTime() {
        return new Date(Long.MIN_VALUE);
    }

    /**
     * Provides the ending of time in Unix term 64-bit
     * 
     * @return end date
     */
    public static Date absEndingTime() {
        return new Date(Long.MAX_VALUE);
    }

    /**
     * Prepend and append empty spaces to the text
     * @param text to be modified
     * @param number of spacings to be added
     */
    public static String textSpacer(String text, int spacing) {
        String space = " ";
        StringBuilder spaces = new StringBuilder(); 
        StringBuilder returnText = new StringBuilder();
        for(int i = 0; i < spacing; i++){
            spaces.append(space);
        }
        
        returnText.append(spaces.toString());
        returnText.append(text);
        returnText.append(spaces.toString());
        return returnText.toString();
    }
    
    // @@author A0131891E
    /**
     * Simple check for whether (T[]) array contains (T) key
     */
    public static final <T> boolean arrayContains(T[] arr, T key) {
        assert (arr != null && key != null);
        return !Arrays.stream(arr).noneMatch((T x) -> key.equals(x));
    }

    // @@author A0131891E
    /**
     * Returns a deep copy of its argument
     * 
     * @param matrix
     * @return deep copy of matrix
     */
    public static final String[][] str2dArrayClone(String[][] matrix) {
        assert (matrix != null);
        final String[][] newMatrix = new String[matrix.length][];
        for (int i = 0; i < newMatrix.length; i++) {
            newMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return newMatrix;
    }
    
    // @@author A0133920N
    /**
     * Concatenates two warning string with space in between 
     * 
     * @param w1, w2
     * @return concatenated warning string
     */
    public static final String appendWarningStrings(String w1, String w2) {
    	assert(w2 != null);
        if (w1 == null || w1.trim().equals("")) {
        	return w2;
        } else {
        	return w1 + " " + w2;
        }
    }
}
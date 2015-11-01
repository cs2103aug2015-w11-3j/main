package common;

import java.util.Arrays;
import java.util.Date;
import java.util.Arrays;

/**
 * Class containing miscellaneous functions for any component to use. Should not have dependency on non-java
 * classes
 */
public final class Utilities {

    /**
     * Evaluates if start and end dates are valid where end is after start
     * @param dateStart
     * @param dateEnd
     * @return true if either start or end is null. false if start is after end
     */
    public static final boolean verifyDate(Date dateStart, Date dateEnd) {
        if (dateStart != null && dateEnd != null) {
            if (dateStart.after(dateEnd)) {
                return false;
            }
        }
        return true;
    }
    
    public static final String formatString(String s, Object... args){
        String formatted = String.format(s, args);
        return formatted;
        
    }
    //@@author A0131891E
    /**
     * Simple check for whether (T[]) array contains (T) key
     * uses T.equals for equality check
     * @param arr
     * @param key
     * @return true if key is in array
     */
    public static final <T> boolean arrayContains (T[] arr, T key) {
		assert(arr != null && key != null);
		for (T item : arr) {
			if (key.equals(item)) {
				return true;
			}
		}
		return false;
	}
    /**
     * Returns a deep copy of its argument
     * @param matrix
     * @return deep copy of matrix
     */
    public static final String[][] str2dArrayClone (String[][] matrix) {
    	assert(matrix != null);
    	final String[][] newMatrix = new String[matrix.length][];
		for (int i = 0; i < newMatrix.length; i++) {
			newMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
		}
		return newMatrix;
    }
	
}
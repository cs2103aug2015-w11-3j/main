package common;

import java.util.Date;

/**
 * Class containing miscellaneous function. Should not have dependency on non-java
 * classes
 */
public final class Utilities {

    /**
     * Evaluates if start and end dates are valid where end is after start
     * @param dateStart
     * @param dateEnd
     * @return true if either start or end is null. false if start is after end
     */
    public static boolean verifyDate(Date dateStart, Date dateEnd) {
        if (dateStart != null && dateEnd != null) {
            if (dateStart.after(dateEnd)) {
                return false;
            }
        }
        return true;
    }
    
    public static String formatString(String s, Object... args){
        String formatted = String.format(s, args);
        return formatted;
        
    }
    /**
     * Provides the beginning of time in Unix term
     * @return the epoch
     */
    public static Date absBeginningTime(){
        return new Date(0);
    }
    
    /**
     * Provides the ending of time in Unix term 64-bit
     * @return end date
     */
    public static Date absEndingTime(){
        return new Date(Long.MAX_VALUE);
    }
}
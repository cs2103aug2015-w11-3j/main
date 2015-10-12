package common;

import java.util.Date;

/**
 * Class containing miscelleous function.
 * Should not have dependency on non-java classes
 */
public final class Utilities {

	public static boolean verifyDate(Date dateStart, Date dateEnd) {
		if (dateStart != null && dateEnd != null) {
			if (dateStart.after(dateEnd)) {
				return false;
			}
		}
		return true;
	}
}
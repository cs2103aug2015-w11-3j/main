//@@author A0131891E
package parser;

import java.text.ParseException;
import java.util.Date;

public interface CelebiDateFormatter {
	public Date parseDate (String dateStr) throws ParseException;
	//public String formatDate (Date d);
}

//@@author A0131891E
package parser.temporal;

import java.text.ParseException;
import java.util.Date;

public interface CelebiDateParser {
	public Date parseDate (String dateStr, boolean isStart) throws ParseException;
	//public String formatDate (Date d);
}

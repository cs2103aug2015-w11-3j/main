//@@author A0131891E
package parser.temporal;

import java.time.LocalDate;

public interface DateParser {
	public LocalDate parseDate(String token);
}

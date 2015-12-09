//@@author A0131891E
package parser.temporal;

import java.time.LocalTime;

public interface TimeParser {
	public LocalTime parseStartTime(String token);
	public LocalTime parseEndTime(String token);
}

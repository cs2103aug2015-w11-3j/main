//@@author A0131891E
package parser.temporal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import parser.tokens.TokenController;

/**
 * Main date parser
 * @author Leow Yijin
 */
public class DefaultDateTimeParser {

	
	private DateParser ABS_DATE;
	private TimeParser ABS_TIME;
	private DateParser FUZZY_DATE;
	
	private static DefaultDateTimeParser instance;
	
	private DefaultDateTimeParser() {
		ABS_DATE = AbsoluteDateParser.getInstance();
		ABS_TIME = AbsoluteTimeParser.getInstance();
		FUZZY_DATE = FuzzyDateParser.getInstance();
	}

	public static DateTimeParser getInstance() {
		if (instance == null) {
			instance = new DefaultDateTimeParser();
		}
		return instance;
	}

	LocalDate date;
	LocalTime time;
	
	public static void main(String[] args) {
		
	}
	
	public Date parseStartDateTime(String token) {
		token = TokenController.cleanText(token);
		
		String[] tokens = token.split(",", 2);
		time = 
	}
	
	public Date parseEndDateTime(String token) {
		token = TokenController.cleanText(token);

		String[] tokens = token.split(",", 2);
		
	}

	private 
}

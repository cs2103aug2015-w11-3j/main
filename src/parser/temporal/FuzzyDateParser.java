//@@author A0131891E
package parser.temporal;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Calendar.*;

//import parser.tokens.TokenController;

/**
 * Parses date token string similarly to natural language
 * @author Leow Yijin
 */
public class FuzzyDateParser {
	
	// in N <periods> or N <periods> later
	private static final String REG_PREFIX_KEYS = 
			"(?:in|after)";
	private static final String REG_POSTFIX_KEYS = 
			"(?:later|in the future|from now)";
	private static final String REG_RELATIVE = 
			REG_PREFIX_KEYS + "\\s+(?<qty>\\d+)\\s+(?<prd>\\w+)\\s+" + REG_POSTFIX_KEYS;
	
	
	
	private final Pattern P_RELATIVE;

	private static FuzzyDateParser instance;
	private FuzzyDateParser() {
		P_RELATIVE = Pattern.compile(REG_RELATIVE);
	}
	public static FuzzyDateParser getInstance() {
		if (instance == null) {
			instance = new FuzzyDateParser();
		}
		return instance;
	}
	
//	@Override
	public LocalDate parseDate(String token) {
//		token = TokenController.cleanText(token);
		Matcher m;
		
		m = P_RELATIVE.matcher(token);
		if (m.matches()) {
			return parseRelativeDate(m);
		}
		
		return parseWeekLevel(token);
	}

	// parse date relative to now
	private LocalDate parseRelativeDate(Matcher m) {
		int qty = Integer.parseInt(m.group("qty"));
		String period = m.group("prd");
		
		switch (period) {
			
			case "day" : // Fallthrough
			case "days" :
				return LocalDate.now().plusDays(qty);
			
			case "week" : // Fallthrough
			case "weeks" :
				return LocalDate.now().plusWeeks(qty);
				
			case "month" : // Fallthrough
			case "months" :
				return LocalDate.now().plusMonths(qty);
				
			default:
				return null;				
		}
	}
	
	// parse by everyday language
	private LocalDate parseWeekLevel(String token) {
		String[] tokens = token.split("\\s+");
		boolean next = false;
		LocalDate rtnDate = LocalDate.now();
		if (tokens[0].equals("next")) {
			next = true;
		}
		if (tokens.length == 0 || tokens.length > 2) {
			return null;
		}
		String weekday = next ? tokens[1] : tokens[0];
		Calendar cal = new GregorianCalendar();
		switch (weekday) {
			
			case "tmr" :
			case "tomorrow" :
				return rtnDate.plusDays(1);
				
			case "week" :
				return next ? rtnDate.plusWeeks(1) : null;
				
			case "sun" :
			case "sunday" :
				cal.set(DAY_OF_WEEK, SUNDAY);
				break;
				
			case "mon" :
			case "monday" :
				cal.set(DAY_OF_WEEK, MONDAY);
				break;
			case "tue" :
			case "tues" :
			case "tuesday" :
				cal.set(DAY_OF_WEEK, TUESDAY);
				break;
			case "wed" :
			case "wednesday" :
				cal.set(DAY_OF_WEEK, WEDNESDAY);
				break;
			case "thu" :
			case "thur" :
			case "thurs" :
			case "thursday" :
				cal.set(DAY_OF_WEEK, THURSDAY);
				break;
			case "fri" :
			case "friday" :
				cal.set(DAY_OF_WEEK, FRIDAY);
				break;
			case "sat" :
			case "saturday" :
				cal.set(DAY_OF_WEEK, SATURDAY);
				break;
			
			default:
				return null;
		}
		if (next) {
			cal.add(WEEK_OF_YEAR, 1);
		}
		return AbsoluteDateParser.toLocalDate(cal.getTime());
	}
}

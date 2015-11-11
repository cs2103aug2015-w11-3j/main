//@@author A0131891E
package parser.temporal;

import java.time.LocalTime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import parser.tokens.TokenController;

/**
 * Parses absolute times, also supports hour- only
 * @author Leow Yijin
 */
public class AbsoluteTimeParser implements TimeParser {

	// Default start and end times
	private static final int DEF_START_H = 8;
	private static final int DEF_START_M = 0;
	private static final int DEF_END_H = 23;
	private static final int DEF_END_M = 59;
	
	private static final int DEF_MIN = 0;
	
	private static final String REG_TIME_SEP = "[\\Q.:\\E]?";
	
	private static final String REG_MERIDIEM = " *(?<mer>am|pm)";		// am|pm
	private static final String REG_24HR = "(?<hr>[01]?\\d|2[0123])"; 	// 0-23
	private static final String REG_12HR = "(?<hr>0?[1-9]|1[012])";		// 1-12
	private static final String REG_MIN = "(?<min>[0-5]\\d)"; 			// 0-59
	
	// Full time
	private static final String REG_FULL_24H = REG_24HR + REG_TIME_SEP + REG_MIN;
	private static final String REG_FULL_12H = REG_12HR + REG_TIME_SEP + REG_MIN + REG_MERIDIEM;
	// Hour only
	private static final String REG_12H_NOMIN = REG_12HR + REG_MERIDIEM;
	//private static final String REG_24H_NOMIN = REG_24HR; // deprec
	
	final Pattern P_FULL_12H, P_FULL_24H, P_12H_NOMIN; //, P_24H_NOMIN;
	
	private static AbsoluteTimeParser instance;
	
	private AbsoluteTimeParser() {
		P_FULL_12H = Pattern.compile(REG_FULL_12H); 
		P_FULL_24H = Pattern.compile(REG_FULL_24H); 
		P_12H_NOMIN = Pattern.compile(REG_12H_NOMIN);
		//P_24H_NOMIN = Pattern.compile(REG_24H_NOMIN);
	}
	public static TimeParser getInstance() {
		if (instance == null) {
			instance = new AbsoluteTimeParser();
		}
		return instance;
	}

	@Override
	public LocalTime parseStartTime(String token) {
//		token = TokenController.cleanText(token);
		if ("".equals(token)) { // no h/min
			return LocalTime.of(DEF_START_H, DEF_START_M);
		}
		return parseTime(token);
	}
	
	@Override
	public LocalTime parseEndTime(String token) {
//		token = TokenController.cleanText(token);
		if ("".equals(token)) { // no h/min
			return LocalTime.of(DEF_END_H, DEF_END_M);
		}
		return parseTime(token);
	}
	
	// null return if failed. Parse in decreasing order
	// of strictness of format.
	private LocalTime parseTime(String token) {
		LocalTime time;
		time = parse12H(P_FULL_12H, token);
		if (time != null) {
			return time;
		}
		time = parse24H(P_FULL_24H, token);
		if (time != null) {
			return time;
		}
		time = parse12H(P_12H_NOMIN, token);
		if (time != null) {
			return time;
		}
//		time = parse24H(P_24H_NOMIN, token);
//		if (time != null) {
//			return time;
//		}
		return null;
	}
	
	private LocalTime parse12H(Pattern p, String token) {
		int h, min;
		Matcher m;
		m = p.matcher(token);
		if (m.matches()) {
			h = parseHour12H(m);
			try {
				min = Integer.parseInt(m.group("min"));
			} catch (IllegalArgumentException e) {
				min = DEF_MIN;
			}
			return LocalTime.of(h, min);
		}
		return null;
	}
	private LocalTime parse24H(Pattern p, String token) {
		int h, min;
		Matcher m;
		m = p.matcher(token);
		if (m.matches()) {
			h = Integer.parseInt(m.group("hr"));
			try {
				min = Integer.parseInt(m.group("min"));
			} catch (IllegalArgumentException e) {
				min = DEF_MIN;
			}
			return LocalTime.of(h, min);
		}
		return null;
	}
	 
	private int parseHour12H(Matcher m) {
		int h;
		boolean isAM;
		// use meridiem to determine true hour
		switch (m.group("mer")) {		
			case "am" :
				isAM = true;
				break;
			case "pm" :
				isAM = false;
				break;
			default:
				throw new Error();
		}
		// perform necessary transformations
		h = Integer.parseInt(m.group("hr"));
		if (isAM) {
			return h == 12 ? 0 : h;
		}
		return h == 12 ? 12 : h + 12;
	}

	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (true) {
			try {
				System.out.println(AbsoluteTimeParser.getInstance().parseStartTime(in.nextLine()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

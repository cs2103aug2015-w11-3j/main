package parser;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.GregorianCalendar;
import static java.util.Calendar.*;

public class FullDateFormat implements DateParsingFormat {
	
	public static final String DELIM = DateFormatter.DATETIME_DELIM;
	public static final String SEP = DateFormatter.DATETIME_SEP;
	
	private static final String REGEX_NUM_SUFFIX = 
			"(\\d)(?:st|nd|rd|th)"; 	// $1 to capture preceding digit
	private final Pattern P_NUM_SUFFIX; // to match the "st" in 1st, "nd" in 2nd, etc..
	
	// parse for time section
	private static final String[] REGEX_TIMES = {
			String.format("hh%Smma", DELIM), 	// no delim b/w digits and meridian
			String.format("hh%Smm%<Sa", DELIM),	// delim b/w digits and meridian
			String.format("HH%Smm", DELIM)		// no meridian, 24h
	};
	private final DateFormat[] TIME_DFS;
	
	// parse for date section (cal day)
	private static final String[] REGEX_DATES = {
			String.format("dd%SMM%<Syy", DELIM), 	// for handling numbered monthS
			String.format("dd%SMMM%<Syy", DELIM), 	// for handling text monthS
			String.format("yy%SMM%<Sdd", DELIM), 	// yy/mm/dd iS lower in prio than dd/mm/yy
			String.format("yy%SMMM%<Sdd", DELIM)	// ditto, for text monthS
	};
	private final DateFormat[] DATE_DFS;

	private GregorianCalendar cal;
	
	public FullDateFormat () {
		
		P_NUM_SUFFIX = Pattern.compile(REGEX_NUM_SUFFIX);
		
		// Time parsing setup
		TIME_DFS = new DateFormat[REGEX_TIMES.length];
		for (int i = 0; i < REGEX_TIMES.length; i++) {
			TIME_DFS[i] = new SimpleDateFormat(REGEX_TIMES[i]);
			TIME_DFS[i].setLenient(false);
		}
		
		// Date (day) parsing setup
		DATE_DFS = new DateFormat[REGEX_DATES.length];
		final Date twoDigitYearStart = new Date(0); // Sets 2 digit year parsing to begin from 1970
		for (int i = 0; i < REGEX_DATES.length; i++) {
			DATE_DFS[i] = new SimpleDateFormat(REGEX_DATES[i]);
			DATE_DFS[i].setLenient(false);
			((SimpleDateFormat)DATE_DFS[i]).set2DigitYearStart(twoDigitYearStart);
		}
		
	}
	
	// Takes in datestring preprocessed by DateParser to handle all seperators and delimiters
	@Override
	public Date parse (String token) throws ParseException {

		cal = new GregorianCalendar();
		
		// removes all num suffixes
		token = removeNumSuffixes(token);
		
		// split datestring into date and time parts
		String[] split = token.split("\\Q"+SEP+"\\E");
		if (split.length != 2) {
			//System.out.println(split.length);
			throw new ParseException("datestring cannot be split into 2 parts (time and date)", -1);
		}
		
		boolean firstPartIsTime;
		Date timeD, dateD;
		timeD = parseBy(split[0], TIME_DFS); // try parse first half as time segment
		
		if (timeD == null) { 	// first half cannot be parsed as time
			dateD = parseBy(split[0], DATE_DFS);
			timeD = parseBy(split[1], TIME_DFS);
		} else { 				// first half successfully parsed as time
			dateD = parseBy(split[1], DATE_DFS);
		}
		
		if (timeD == null || dateD == null) {
			//System.out.println(split);
			throw new ParseException("datestring cannot be parsed as full absolute date", -1);
		}
		
		cal.setTime(dateD);
		GregorianCalendar timeCal = new GregorianCalendar();
		timeCal.setTime(timeD);
		cal.set(HOUR_OF_DAY, timeCal.get(HOUR_OF_DAY)); // load hour
		cal.set(MINUTE, timeCal.get(MINUTE)); 			// load minute
		
		return cal.getTime();
	}
	
	private Date parseBy (String token, DateFormat[] dfs) {
		for (DateFormat df : dfs) {
			try {
				return df.parse(token);
			} catch (ParseException pe) {
				;
			}
		}
		return null;
	}
	
	private String removeNumSuffixes (String s) {
		return P_NUM_SUFFIX.matcher(s).replaceAll("$1");
	}

	// Expl testing
	public static void main (String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		DateParsingFormat fdf = new FullDateFormat();
		System.out.println(SEP);
		while (true) {
			try {
			System.out.println(fdf.parse(in.nextLine()));
			} catch (ParseException e) {
				System.out.println(e);
			}
		}
//		System.out.println((new SimpleDateFormat()).getCalendar() instanceof GregorianCalendar);
	}

}

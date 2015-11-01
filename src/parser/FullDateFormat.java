//@@author A0131891E
package parser;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import common.Utilities;

import java.util.regex.Matcher;
import java.util.GregorianCalendar;
import static java.util.Calendar.*;

public class FullDateFormat implements CelebiDateFormatter {
	
	public static final String DELIM = DateFormatter.DATETIME_DELIM;
	public static final String SEP = DateFormatter.DATETIME_SEP;
	
	private static final String REGEX_NUM_SUFFIX = 
			"(\\d)(?:st|nd|rd|th)"; 	// $1 to capture preceding digit
	private final Pattern P_NUM_SUFFIX; // to match the "st" in 1st, "nd" in 2nd, etc..
	
	// parse for time section
	private static final String[] REGEX_TIMES = {
			String.format("hh%smm%<sa", DELIM),	// delim b/w digits and meridian
			String.format("hh%smma", DELIM), 	// no delim b/w digits and meridian
			String.format("HH%smm", DELIM)		// no meridian, 24h
	};
	private final DateFormat[] TIME_DFS;
	
	// parse for date section (cal day)
	private static final String[] REGEX_DATES = {
			String.format("dd%sM%<syy", DELIM), 	// for handling numbered months
			String.format("dd%sMMM%<syy", DELIM), 	// for handling text months
			String.format("yy%sM%<sdd", DELIM), 	// yy/mm/dd is lower in prio than dd/mm/yy
			String.format("yy%sMMM%<sdd", DELIM)	// ditto, for text months
	};
	private final DateFormat[] DATE_DFS;

	private GregorianCalendar cal;
	
	FullDateFormat () {
		
		P_NUM_SUFFIX = Pattern.compile(REGEX_NUM_SUFFIX);
		
		TIME_DFS = regexesToDFs(REGEX_TIMES); // Time parsing setup
		DATE_DFS = regexesToDFs(REGEX_DATES); // Date (day) parsing setup
		
	}
	private DateFormat[] regexesToDFs (String[] regexes) {
		final Date twoDigitYearStart = Utilities.absBeginningTime();//new Date(0); // Sets 2 digit year parsing to begin from 1970
		final DateFormat[] dfs = new DateFormat[regexes.length];
		for (int i = 0; i < dfs.length; i++) {
			dfs[i] = new SimpleDateFormat(regexes[i]);
			dfs[i].setLenient(false);
			((SimpleDateFormat)dfs[i]).set2DigitYearStart(twoDigitYearStart);
		}
		return dfs;
	}
	
	// Takes in datestring preprocessed by DateParser to handle all seperators and delimiters
	@Override
	public Date parseDate (String token) throws ParseException {

		cal = new GregorianCalendar();
		
		// removes all num suffixes
		token = removeNumSuffixes(token);
		
		// split datestring into date and time parts
		String[] split = token.split("\\Q"+SEP+"\\E");
		if (split.length != 2) {
			//System.out.println(split.length);
			throw new ParseException("datestring cannot be split into 2 parts (time and date)", -1);
		}
		
		Date timeD, dateD;
		dateD = dfParse(split[0], DATE_DFS); // try parse first half as time segment
		if (dateD == null) { 	// first half cannot be parsed as time
			dateD = dfParse(split[1], DATE_DFS);
			timeD = dfParse(split[0], TIME_DFS);
		} else { 				// first half successfully parsed as time
			timeD = dfParse(split[1], TIME_DFS);
		}
		
		if (timeD == null || dateD == null) {
			//System.out.println(split);
			//System.out.println("time: " + timeD + "\ndate: " + dateD);
			throw new ParseException("datestring cannot be parsed as full absolute date", -1);
		}
		
		cal.setTime(dateD);
		GregorianCalendar timeCal = new GregorianCalendar();
		timeCal.setTime(timeD);
		cal.set(HOUR_OF_DAY, timeCal.get(HOUR_OF_DAY)); // load hour
		cal.set(MINUTE, timeCal.get(MINUTE)); 			// load minute
		
		return cal.getTime();
	}
	
	private Date dfParse (String token, DateFormat[] dfs) {
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
		CelebiDateFormatter fdf = new FullDateFormat();
		DateFormat df;
		while (true) {
			try {
				//df = new SimpleDateFormat(in.nextLine());
				//System.out.println(df.parse(in.nextLine()));
			System.out.println(fdf.parseDate(in.nextLine()));
			} catch (ParseException e) {
				System.out.println(e);
			}
		}
//		System.out.println((new SimpleDateFormat()).getCalendar() instanceof GregorianCalendar);
	}

}

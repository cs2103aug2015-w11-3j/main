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

public class FullDateFormat implements DateParsingFormat {
	
	public static final String DELIM = DateFormatter.DATETIME_DELIM;
	public static final String SEP = DateFormatter.DATETIME_SEP;
	
	private static final String REGEX_NUM_SUFFIX = 
			"(\\d)(?:st|nd|rd|th)"; 	// $1 to capture preceding digit
	private final Pattern P_NUM_SUFFIX; // to match the "st" in 1st, "nd" in 2nd, etc..
	
	// parse for time section
	private static final String[] REGEX_TIMES = {
			String.format("hh%smma", DELIM), 	// no delim b/w digits and meridian
			String.format("hh%smm%<sa", DELIM),	// delim b/w digits and meridian
			String.format("HH%smm", DELIM)		// no meridian, 24h
	};
	private final DateFormat[] TIME_DF_ARRAY;
	
	// parse for date section (cal day)
	private static final String[] REGEX_DATES = {
			String.format("dd%sMM%<syy", DELIM), 	// for handling numbered months
			String.format("dd%sMMM%<syy", DELIM), 	// for handling text months
			String.format("yy%sMM%<sdd", DELIM), 	// yy/mm/dd is lower in prio than dd/mm/yy
			String.format("yy%sMMM%<sdd", DELIM)	// ditto, for text months
	};
	private final DateFormat[] DATE_DF_ARRAY;

	private GregorianCalendar cal;
	
	public FullDateFormat () {
		
		P_NUM_SUFFIX = Pattern.compile(REGEX_NUM_SUFFIX);
		
		// Time parsing setup
		TIME_DF_ARRAY = new DateFormat[REGEX_TIMES.length];
		for (int i = 0; i < REGEX_TIMES.length; i++) {
			TIME_DF_ARRAY[i] = new SimpleDateFormat(REGEX_TIMES[i]);
			TIME_DF_ARRAY[i].setLenient(false);
		}
		
		// Date (day) parsing setup
		DATE_DF_ARRAY = new DateFormat[REGEX_DATES.length];
		final Date twoDigitYearStart = new Date(0); // Sets 2 digit year parsing to begin from 1970
		for (int i = 0; i < REGEX_DATES.length; i++) {
			DATE_DF_ARRAY[i] = new SimpleDateFormat(REGEX_DATES[i]);
			DATE_DF_ARRAY[i].setLenient(false);
			((SimpleDateFormat)DATE_DF_ARRAY[i]).set2DigitYearStart(twoDigitYearStart);
		}
		
	}
	
	// Takes in datestring preprocessed by DateParser to handle all seperators and delimiters
	@Override
	public Date parse (String token) throws ParseException {
		
		// removes all num suffixes
		token = removeNumSuffixes(token);
		
		// split datestring into date and time parts
		String[] split = token.split(SEP);
		if (split.length != 2) {
			throw new ParseException("datestring cannot be split into time and date parts", -1);
		}
		
		boolean firstPartIsTime;
		
		
	}
	
	@Override
	public String format (Date d) {
		// TODO
		return null;
	}
	
	private String removeNumSuffixes (String s) {
		return P_NUM_SUFFIX.matcher(s).replaceAll("$1");
	}

	// Expl testing
	public static void main (String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		Pattern p = Pattern.compile("(\\d)(?:st|nd|rd|th)");
		while (true) {
//			try {
//			System.out.println(
//					dp.parseAbsDate(in.nextLine())
//					);
//			} catch (ParseException e) {
//				System.out.println(e);
//			}
			Matcher m = p.matcher(in.nextLine());
			System.out.println(m.replaceAll("$1"));
		}
//		System.out.println((new SimpleDateFormat()).getCalendar() instanceof GregorianCalendar);
	}

}

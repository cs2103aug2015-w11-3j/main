package parser;

import java.text.ParseException;
import java.util.Calendar;
import static java.util.Calendar.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DateFormatter implements CelebiDateFormatter {
	
	/////////////////////////////////////////////////////////////////
	// Preprocess datestring
	/////////////////////////////////////////////////////////////////
	
	// For separating intra-date tokens, matching characters {'-'|'_'|'/'|'\'|'.'|':'}
	private static final String REGEX_DATETIME_DELIM = 
	"[\\Q-_/\\.:\\E]+";
	private final Pattern P_DATETIME_DELIM;
	
	// For separating date and time portions, matching chars {','|' '|';'}
	private static final String REGEX_DATETIME_SEP =
	"[; ,]+";
	private final Pattern P_DATETIME_SEP;
	
	// placeholder strings for pattern matching tokenisation
	public static final String DATETIME_DELIM = "*";
	public static final String DATETIME_SEP = "|";
	
	private final DateParsingFormat CONV_DF;
	private final DateParsingFormat FULL_DF;
	private final DateParsingFormat PART_DF;

	DateFormatter() {
		P_DATETIME_DELIM = Pattern.compile(REGEX_DATETIME_DELIM);
		P_DATETIME_SEP = Pattern.compile(REGEX_DATETIME_SEP);
		FULL_DF = new FullDateFormat();
		PART_DF = new PartialDateFormat();
		CONV_DF = new ConvenienceDateFormat();
	}

	public Date parseDate (String token) throws ParseException {
		assert(token != null);
		token = token.trim().toLowerCase();
		Calendar cal = new GregorianCalendar();
		switch (token) {
		
			// current time
			case "now" :
				return new Date();
				
			// +24h
			case "tmr" :		// Fallthrough
			case "tomorrow" :
				return new Date(1000*60*60*24 + (new Date()).getTime());
				
			// +24*7h
			case "next week" :
				return new Date(1000*60*60*24*7 + (new Date()).getTime());
				
			case "next mon" :
			case "next monday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, MONDAY);
				return cal.getTime();
				
			case "next tue" :
			case "next tues" :
			case "next tuesday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, TUESDAY);
				return cal.getTime();
				
			case "next wed" :
			case "next wednesday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, WEDNESDAY);
				return cal.getTime();
				
			case "next thu" :
			case "next thur" :
			case "next thursday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, THURSDAY);
				return cal.getTime();
				
			case "next fri" :
			case "next friday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, FRIDAY);
				return cal.getTime();

			case "next sat" :
			case "next saturday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, SATURDAY);
				return cal.getTime();

			case "next sun" :
			case "next sunday" :
				cal.add(WEEK_OF_YEAR, 1);
				cal.set(DAY_OF_WEEK, SUNDAY);
				return cal.getTime();
				
			default :
				return parseAbsDate(token);
		}
	}
	Date parseAbsDate (String token) throws ParseException {
		assert(token != null);
		
		
		
		// replace date delimiters with common token
		token = token.trim();
		token = P_DATETIME_DELIM.matcher(token).replaceAll(DATETIME_DELIM); // process token delims
		token = P_DATETIME_SEP.matcher(token).replaceAll(DATETIME_SEP); // process date-time seperator
		
		// try parsing partial dates (without all calendar fields filled).
//		try { 
//			return PART_DF.parse(token);
//		} catch (ParseException pePart) {
//			;
//		}
		
		// final try: parse with full info, down to minute resolution.
		try { 
			return FULL_DF.parse(token);
		} catch (ParseException peFull) {
			throw peFull;
		}
	}
	
	public String formatDate (Date d) {
		// TODO
		return null;
	}
	
	public static void main (String[] args) {
		DateFormatter df = new DateFormatter();
		Scanner in = new Scanner(System.in);
		while (true) {
			try {
				System.out.println(df.parseDate(in.nextLine()));
			} catch (ParseException pe) {
				System.out.println(pe);
			}
		}
	}
}

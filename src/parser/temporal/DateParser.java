//@@author A0131891E
package parser.temporal;

import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DateParser implements CelebiDateParser {
	
	/////////////////////////////////////////////////////////////////
	// Preprocess datestring
	/////////////////////////////////////////////////////////////////
	
	// For separating intra-date tokens, matching characters {'-'|'_'|' '|'/'|'\'|'.'|':'}
	private static final String REGEX_DATETIME_DELIM = 
		"[\\Q-_/\\.: \\E]+";
	private final Pattern P_DATETIME_DELIM;
	
	// For separating date and time portions, matching chars {','|';'}
	private static final String REGEX_DATETIME_SEP =
		"\\s*[;,]+\\s*";
	private final Pattern P_DATETIME_SEP;
	
	// placeholder strings for pattern matching tokenisation
	public static final String DATETIME_DELIM = "*";
	public static final String DATETIME_SEP = "|";
	

	/////////////////////////////////////////////////////////////////
	// Worker classes for parsing different date formats
	/////////////////////////////////////////////////////////////////
	
	private final CelebiDateParser CONV_DF;
	private final CelebiDateParser FULL_DF;
	private final CelebiDateParser PART_DF;

	public DateParser() {
		P_DATETIME_DELIM = Pattern.compile(REGEX_DATETIME_DELIM);
		P_DATETIME_SEP = Pattern.compile(REGEX_DATETIME_SEP);
		FULL_DF = new FullDateFormat();
		PART_DF = new PartialDateFormat();
		CONV_DF = new ConvenienceDateFormat();
	}

	public Date parseDate (String token, boolean isStart) throws ParseException {
		assert(token != null);
		token = token.trim().toLowerCase();
		try {
			return CONV_DF.parseDate(token, isStart);
		} catch (ParseException pe) {
			return parseAbsDate(token, isStart);
		}
	}
	Date parseAbsDate (String token, boolean isStart) throws ParseException {
		assert(token != null);
		
		// replace date delimiters with common token
		token = token.trim();
		token = P_DATETIME_SEP.matcher(token).replaceAll(DATETIME_SEP); // process date-time seperator
		token = P_DATETIME_DELIM.matcher(token).replaceAll(DATETIME_DELIM); // process token delims
		
		// try parse with full info, down to minute resolution.
		try { 
			return FULL_DF.parseDate(token, isStart);
		} catch (ParseException pePart) {
			;
		}
		
		// final try: parsing partial dates (without all calendar fields filled).
		try { 
			return PART_DF.parseDate(token, isStart);
		} catch (ParseException peFull) {
			;
		}
		
		throw new ParseException("Unparseable as Date: \"" + token + '"', 0);
	}
	
	public String formatDate (Date d) {
		// TODO
		return null;
	}
	
	public static void main (String[] args) {
		DateParser df = new DateParser();
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		while (true) {
			try {
				System.out.println(df.parseDate(in.nextLine(), true));
			} catch (ParseException pe) {
				System.out.println(pe);
			}
		}
	}
}

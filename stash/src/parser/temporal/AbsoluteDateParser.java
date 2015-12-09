//@@author A0131891E
package parser.temporal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.tokens.TokenController;

/**
 * Parses absolute dates, also supports partial dates.
 * 
 * @author Leow Yijin
 */
public class AbsoluteDateParser implements DateParser {

	private static final String DATE_SEP_SUBS = "-";
	private static final String REG_DATE_SEP = "[\\Q./\\-_\\E]+";
	private static final String REG_DATE_SEP_WITH_SPACE = "(?:" + REG_DATE_SEP + "|\\s+)";
	private final Pattern P_DATE_SEPS;
	
	// @see parseAsDigitsOnly: Parse dates that are solid blocks of numbers
	private static final String REG_8_DIGITS = "\\d{8}";
	private static final String DFSTR_8_DIGITS = "yyyyMMdd";
	private static final String REG_6_DIGITS = "\\d{6}"; 
	private static final String DFSTR_6_DIGITS = "yyMMdd";
	private final Pattern P_8D, P_6D;
	private final DateFormat DF_8D, DF_6D;
	
	private static final String REG_DAY = "(?<day>\\d+)(?:st|nd|rd|th)?";
	private final Pattern P_DAY;
	private static final String REG_SUFFIX = "(\\d)(?:st|nd|rd|th)";
	private final Pattern P_SUFFIX;

	private static final DateFormat[] DF_NO_YEAR = {
			new SimpleDateFormat("MMM-d"),
			new SimpleDateFormat("d-MMM"),
			new SimpleDateFormat("MM-d"),
			new SimpleDateFormat("d-MM")
	};
	
	private static final DateFormat[] DF_FULL = {
			new SimpleDateFormat("yy-MMM-dd"),
			new SimpleDateFormat("dd-MMM-yy"),
			new SimpleDateFormat("MMM-dd-yy"),

			new SimpleDateFormat("yy-MM-dd"),
			new SimpleDateFormat("dd-MM-yy"),
			new SimpleDateFormat("MM-dd-yy")
	};
	
	private static AbsoluteDateParser instance;
	private AbsoluteDateParser() {
		P_DATE_SEPS = Pattern.compile(REG_DATE_SEP_WITH_SPACE);
		
		P_8D = Pattern.compile(REG_8_DIGITS);
		P_6D = Pattern.compile(REG_6_DIGITS);
		DF_8D = new SimpleDateFormat(DFSTR_8_DIGITS);
		DF_6D = new SimpleDateFormat(DFSTR_6_DIGITS);
		
		P_DAY = Pattern.compile(REG_DAY);
		P_SUFFIX = Pattern.compile(REG_SUFFIX);
	}
	public static DateParser getInstance() {
		if (instance == null) {
			instance = new AbsoluteDateParser();
		}
		return instance;
	}
	
	@Override
	public LocalDate parseDate(String token) {
		LocalDate date;
		String[] tokens;
		token = TokenController.cleanText(token);
		// substitute common token for separator
		token = P_DATE_SEPS.matcher(token).replaceAll(DATE_SEP_SUBS);
		
		// @see parseStrictDigits
		date = parseStrictDigits(token);
		if (date != null) { 
			return date;
		}
		
		// tokenise date string based on seperator pattern.
		// early termination if size is wrong.
		tokens = token.split(REG_DATE_SEP_WITH_SPACE);
		if (tokens.length > 3 || "".equals(token)) {
			return LocalDate.now(); // incorrect number of tokens
		}
		
		// Check from year to month to day.
		// Few people leave out the day and include the month
		// They like day level precision.
		switch (tokens.length) {
			case 1 : // day
				return parseDay(tokens[0]);
				//break;
			case 2 : // month day
				return parseMonthDay(token);
				//break;
			case 3 : // full
				return parseFull(token);
				//break;
			default :
				System.out.println(tokens.length);
				for (String s : tokens) {
					System.out.print(s + " ");
				}
				System.out.println();
				assert(false);
		}
		return null;
	}

	// try parse as yyyyMMdd then yyMMdd
	private LocalDate parseStrictDigits(String token) {
		if (P_8D.matcher(token).matches()) {
			try {
				return toLocalDate(DF_8D.parse(token));
			} catch (ParseException e) {
			}
		}
		if (P_6D.matcher(token).matches()) {
			try {
				return toLocalDate(DF_6D.parse(token));				
			} catch (ParseException e) {
			}
		}
		return null;
	}
	
	
	private LocalDate parseDay(String token) {
		int day;
		Matcher m = P_DAY.matcher(token);
		if (m.matches()) {
			try {
				day = Integer.parseInt(m.group("day"));
				return LocalDate.now().withDayOfMonth(day);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	// try parsing tokens as month and day
	private LocalDate parseMonthDay(String token) {
		Date d;
		int year = LocalDate.now().getYear();
		token = cleanNumSuffix(token);
		for (DateFormat df : DF_NO_YEAR) {
			try {
				df.setLenient(false);
				d = df.parse(token);
				return toLocalDate(d).withYear(year);
			} catch (ParseException e) {
				;
			}
		}
		return null;
	}
	
	// try parsing as full date
	private LocalDate parseFull(String token) {
		token = cleanNumSuffix(token);
		for (DateFormat df : DF_FULL) {
			try {
				df.setLenient(false);
				return toLocalDate(df.parse(token));
			} catch (ParseException e) {
				;
			}
		}
		return null;
	}
	
	private String cleanNumSuffix(String token) {
		return P_SUFFIX.matcher(token).replaceFirst("$1");
	}
	
	public static LocalDate toLocalDate(Date d) {
		return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (true) {
			try {
				System.out.println(AbsoluteDateParser.getInstance().parseDate(in.nextLine()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

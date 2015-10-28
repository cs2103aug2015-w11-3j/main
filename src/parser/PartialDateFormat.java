package parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import static java.util.Calendar.*;

public class PartialDateFormat implements DateParsingFormat {

	public static final String DELIM = DateFormatter.DATETIME_DELIM;
	public static final String SEP = DateFormatter.DATETIME_SEP;
	
	private static final String REGEX_NUM_SUFFIX = 
			"(\\d)(?:st|nd|rd|th)"; 	// $1 to capture preceding digit
	private final Pattern P_NUM_SUFFIX; // to match the "st" in 1st, "nd" in 2nd, etc..

	// parse for date section (cal day)
	private final DateFormat[] DATE_YMD_DFS;
	private static final String[] REGEX_YMD_DATES = {
			String.format("dd%sM%<syy", DELIM), 	// for handling numbered months
			String.format("dd%sMMM%<syy", DELIM), 	// for handling text months
			String.format("yy%sM%<sdd", DELIM), 	// yy/mm/dd is lower in prio than dd/mm/yy
			String.format("yy%sMMM%<sdd", DELIM)	// ditto, for text months
	};

	private final DateFormat[] DATE_MD_DFS;
	private static final String[] REGEX_MD_DATES = {
			String.format("dd%sMMM", DELIM),		// no year, text month
			String.format("MMM%sdd", DELIM),
			String.format("dd%sM", DELIM), 			// no year, digit month
			String.format("M%sdd", DELIM),
	};
	
	private final DateFormat[] DATE_D_DFS;
	private static final String[] REGEX_D_DATES = {
			String.format("dd")					// only day (num)
	};
	private final DateFormat[] DATE_WD_DFS;
	private static final String[] REGEX_WD_DATES = {
			String.format("EEE")					// only day (weekday name)
	};

	
	private GregorianCalendar cal;
	
	PartialDateFormat () {

		P_NUM_SUFFIX = Pattern.compile(REGEX_NUM_SUFFIX);
	
		DATE_YMD_DFS = regexesToDFs(REGEX_YMD_DATES);
		DATE_MD_DFS = regexesToDFs(REGEX_MD_DATES);
		DATE_D_DFS = regexesToDFs(REGEX_D_DATES);
		DATE_WD_DFS = regexesToDFs(REGEX_WD_DATES);
		
	}
	
	private DateFormat[] regexesToDFs (String[] regexes) {
		final Date twoDigitYearStart = new Date(0); // Sets 2 digit year parsing to begin from 1970
		final DateFormat[] dfs = new DateFormat[regexes.length];
		for (int i = 0; i < dfs.length; i++) {
			dfs[i] = new SimpleDateFormat(regexes[i]);
			dfs[i].setLenient(false);
			((SimpleDateFormat)dfs[i]).set2DigitYearStart(twoDigitYearStart);
		}
		return dfs;
	}
	
	@Override
	public Date parse (String token) throws ParseException {
		
		cal = new GregorianCalendar();		
		GregorianCalendar tempCal = new GregorianCalendar(); // to extract day/month/year info from cal
		token = removeNumSuffixes(token);
		
		Date parsedDate;
		
		// try to parse as full date (day-level)
		parsedDate = dfParse(token, DATE_YMD_DFS);
		if (parsedDate != null) {
			tempCal.setTime(parsedDate);
			cal.set(YEAR, tempCal.get(YEAR));
			cal.set(MONTH, tempCal.get(MONTH));
			cal.set(DAY_OF_MONTH, tempCal.get(DAY_OF_MONTH));
			return cal.getTime();
		}
		
		// try to parse without year
		parsedDate = dfParse(token, DATE_MD_DFS);
		if (parsedDate != null) {
			tempCal.setTime(parsedDate);
			cal.set(MONTH, tempCal.get(MONTH));
			cal.set(DAY_OF_MONTH, tempCal.get(DAY_OF_MONTH));
			return cal.getTime();
		}
		
		// try to parse with only day info
		parsedDate = dfParse(token, DATE_D_DFS);
		if (parsedDate != null) {
			tempCal.setTime(parsedDate);
			cal.set(DAY_OF_MONTH, tempCal.get(DAY_OF_MONTH));
			return cal.getTime();
		}
		// try to parse with only weekday name info
		parsedDate = dfParse(token, DATE_WD_DFS);
		if (parsedDate != null) {
			tempCal.setTime(parsedDate);
			cal.set(DAY_OF_WEEK, tempCal.get(DAY_OF_WEEK));
			return cal.getTime();
		}
		
		throw new ParseException("cannot be parsed only as partial date",-1);
	}
	
	private String removeNumSuffixes (String s) {
		return P_NUM_SUFFIX.matcher(s).replaceAll("$1");
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
}

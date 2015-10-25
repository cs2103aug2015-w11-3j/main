package parser;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CelebiFullDateFormat extends SimpleDateFormat {
	
	public static final String DELIM = DateParser.DATETIME_DELIM;
	public static final String SEP = DateParser.DATETIME_SEP;
	
	// parse for time section
	private static final String REGEX_TIME_A = 
			String.format("hh%smma", DELIM); // no chars between 
	private static final String REGEX_TIME_B = 
			String.format("hh%smm%sa", DELIM);
	private static final String REGEX_TIME_C = 
			String.format("HH%smm", DELIM);
	private final DateFormat DF_TIME_A;
	private final DateFormat DF_TIME_B;
	private final DateFormat DF_TIME_C;
	
	// parse for date section (cal day)
	private static final String REGEX_DATE_A = 
			String.format("dd%sMM%syy", DELIM); // for handling numbered months
	private static final String REGEX_DATE_B = 
			String.format("dd%sMMM%syy", DELIM); // for handling text months
	private final DateFormat DF_DATE_A;
	private final DateFormat DF_DATE_B;

	private final DateFormat[] TIME_DF_ARRAY;
	private final DateFormat[] DATE_DF_ARRAY;

	CelebiFullDateFormat () {
		
		DF_TIME_A = new SimpleDateFormat(REGEX_TIME_A);
		DF_TIME_B = new SimpleDateFormat(REGEX_TIME_B);
		DF_TIME_C = new SimpleDateFormat(REGEX_TIME_C);
		TIME_DF_ARRAY = new DateFormat[] {DF_TIME_A, DF_TIME_B, DF_TIME_C};
		
		DF_DATE_A = new SimpleDateFormat(REGEX_DATE_A);
		DF_DATE_B = new SimpleDateFormat(REGEX_DATE_B);
		DATE_DF_ARRAY = new DateFormat[] {DF_DATE_A, DF_DATE_B};
		
		DF_TIME_A.setLenient(false);
		DF_TIME_B.setLenient(false);
		DF_TIME_C.setLenient(false);
		DF_DATE_A.setLenient(false);
		DF_DATE_B.setLenient(false);
		
	}
	
	@Override
	public Date parse (String token) throws ParseException {
//		for (DateFormat df : FULL_DF_ARRAY) {
//			try {
//				Date d = df.parse(token);
//				if (d != null) {
//					return d;
//				}
//			} catch (ParseException e) {
//				;
//			}
//		}
//		throw new ParseException("Does not fit any full date format", -1);
	}

	// Expl testing
	public static void main (String[] args) throws Exception {
		DateParser dp = new DateParser();
		Scanner in = new Scanner(System.in);
		while (true) {
			try {
			System.out.println(
					dp.parseAbsDate(in.nextLine())
					);
			} catch (ParseException e) {
				System.out.println(e);
			}
		}
	}

}

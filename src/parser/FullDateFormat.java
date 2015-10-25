package parser;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FullDateFormat extends SimpleDateFormat {
	
	
	
	// time before date
	private static final String REGEX_TD_A = 
			"hh*mma*dd*MM*yyyy";
	private static final String REGEX_TD_B = 
			"hh*mm*a*dd*MM*yyyy";
	private static final String REGEX_TD_C = 
			"HH*mm*dd*MM*yyyy";
	private final DateFormat DF_TD_A;
	private final DateFormat DF_TD_B;
	private final DateFormat DF_TD_C;
	
	// date before time
	private static final String REGEX_DT_A = 
			"dd*MM*yyyy*hh*mma";
	private static final String REGEX_DT_B = 
			"dd*MM*yyyy*hh*mm*a";
	private static final String REGEX_DT_C = 
			"dd*MM*yyyy*HH*mm";
	private final DateFormat DF_DT_A;
	private final DateFormat DF_DT_B;
	private final DateFormat DF_DT_C;
	
	// All dateformats to loop through
	private final DateFormat[] FULL_DF_ARRAY;

	FullDateFormat () {
		
		DF_TD_A = new SimpleDateFormat(REGEX_TD_A);
		DF_TD_B = new SimpleDateFormat(REGEX_TD_B);
		DF_TD_C = new SimpleDateFormat(REGEX_TD_C);

		DF_DT_A = new SimpleDateFormat(REGEX_DT_A);
		DF_DT_B = new SimpleDateFormat(REGEX_DT_B);
		DF_DT_C = new SimpleDateFormat(REGEX_DT_C);
		
		FULL_DF_ARRAY = new DateFormat[] {
				DF_TD_A, DF_TD_B, DF_TD_C,
				DF_DT_A, DF_DT_B, DF_DT_C
			};
	}
	
	@Override
	public Date parse (String token) throws ParseException {
		for (DateFormat df : FULL_DF_ARRAY) {
			try {
				Date d = df.parse(token);
				if (d != null) {
					return d;
				}
			} catch (ParseException e) {
				;
			}
		}
		throw new ParseException("Does not fit any full date format", -1);
	}

	

}

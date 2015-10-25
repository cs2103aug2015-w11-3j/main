package parser;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class CelebiPartialDateFormat extends SimpleDateFormat {

	//private static final String REGEX_
	
	CelebiPartialDateFormat () {
		
	}
	
	@Override
	public Date parse (String token) throws ParseException {
		throw new ParseException("",-1);
	}
}

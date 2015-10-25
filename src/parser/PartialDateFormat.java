package parser;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class PartialDateFormat implements DateParsingFormat {

	//private static final String REGEX_
	
	PartialDateFormat () {
		
	}
	
	@Override
	public Date parse (String token) throws ParseException {
		throw new ParseException("",-1);
	}
	
}

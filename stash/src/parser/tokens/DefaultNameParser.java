package parser.tokens;

import java.text.ParseException;
import java.util.regex.Pattern;

import parser.Parser;

public class DefaultNameParser implements Parser<String>{
		
	private static final String REGEX_VALID_NAME = 
			"[^;]+";
	private Pattern P_VALID_NAME;
	private static Parser<String> instance;

	private DefaultNameParser() {
		P_VALID_NAME = Pattern.compile(REGEX_VALID_NAME);
	}
	
	public static Parser<String> getInstance() {
		if (instance == null) {
			instance = new DefaultNameParser();
		}
		return instance;
	}
	
	@Override
	public String parse(String token) throws ParseException {
		if (P_VALID_NAME.matcher(token).matches()) {
			return token;
		}
		throw new ParseException("", 0);
	}

	@Override
	public boolean isPossiblyValid(String token) {
		return token != null && !"".equals(token);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

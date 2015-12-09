//@@author A0131891E
package parser.tokens;

import java.text.ParseException;

import parser.Parser;

public class DefaultIdParser implements Parser<Integer> {

	private static DefaultIdParser instance;
	
	private DefaultIdParser() {
	}
	
	public static Parser<Integer> getInstance() {
		if (instance == null) {
			instance = new DefaultIdParser();
		}
		return instance;
	}

	@Override
	public Integer parse(String token) throws ParseException {
		try {
			return Integer.parseInt(token.trim());
		} catch (NumberFormatException e) {
			throw new ParseException("", 0);
		}
	}

	@Override
	public boolean isPossiblyValid(String token) {
		return token != null && !"".equals(token); 
	}
	
	public static void main(String[] args) {
	}
}

//@@author A0131891E
package parser.commands.abstracted;

import java.text.ParseException;

import parser.ParserFacade;

public abstract class UnaryTextCommand extends UnaryCommand {

	protected String text;
	private static final char ILLEGAL_CHAR = ';';
	
	public UnaryTextCommand(String input) {
		super(input);
	}

	protected void parseArgs(ParseException throwee) throws ParseException {
		super.parseArgs(throwee);
		String token = tokens[0];
		token = ParserFacade.cleanText(token);
		if (token.indexOf(ILLEGAL_CHAR) != -1) {
			throw throwee;
		}
		text = token;
	}
}

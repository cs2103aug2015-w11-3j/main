//@@author A0131891E
package parser.commands.abstracted;

import java.text.ParseException;

import parser.ParserFacade;

/**
 * Abstract superclass for 1-ary comands where the sole
 * arg is parsed as task UID
 * @author Leow Yijin
 */
public abstract class UnaryIdCommand extends UnaryCommand {
	
	protected int taskUID;
	
	protected UnaryIdCommand(String input) {
		super(input);
	}

	// Helps parse the only argument as a task uid (negative numbers caught by Logic)
	protected void parseArgs(ParseException throwee) throws ParseException {
		super.parseArgs(throwee);
		String token = tokens[0];
		token = ParserFacade.cleanText(token);
		try {
			taskUID = Integer.parseInt(token);
		} catch (NumberFormatException e) {
			throw throwee;
		}
	}
}

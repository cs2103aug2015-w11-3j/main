//@@author A0131891E
package parser.commands.abstracted;

import java.text.ParseException;

import parser.commands.Command;

/**
 * Abstract superclass for user commands with variable number of arguments.
 * 
 * Utility: provides a simple input tokeniser based off the superclass's
 * default token delimiter pattern.
 * 
 * @author Leow Yijin
 */
public abstract class VariadicCommand extends Command {
	
	protected VariadicCommand(String input) {
		super(input);
	}

	protected String[] tokenise() throws ParseException {
		return P_DEFAULT_TOKEN_DELIMITER.split(INPUT);
	}
}

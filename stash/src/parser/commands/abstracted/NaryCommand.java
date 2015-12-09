//@@author A0131891E
package parser.commands.abstracted;

import java.text.ParseException;

import parser.commands.Command;

/**
 * Abstract superclass for user commands with fixed number of arguments.
 * 
 * Utility: provides a simple n-limited tokeniser method based on
 * the superclass's default token delimiter pattern.
 * 
 * @author Leow Yijin
 */
public abstract class NaryCommand extends Command {
	
	protected NaryCommand(String input) {
		super(input);
	}
	
	// Quick check for non empty args.
	// Do not call for 0-ary.
	protected void parseArgs(ParseException throwee) throws ParseException {
		if ("".equals(ARG_STRING)) {
			throw throwee;
		}
	}
	
	protected String[] tokenise(int n) {
		return P_DEFAULT_TOKEN_DELIMITER.split(INPUT, n);
	}
}

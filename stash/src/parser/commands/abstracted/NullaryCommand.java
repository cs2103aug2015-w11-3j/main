//@@author A0131891E
package parser.commands.abstracted;

import java.text.ParseException;

/**
 * Abstract superclass for 0-ary commands
 * @author Leow Yijin
 */
public abstract class NullaryCommand extends NaryCommand {

	public static final int NUM_ARGS = 0;
	
	public NullaryCommand(String input) {
		super(input);
	}

	@Override
	protected void parseArgs(ParseException throwee) throws ParseException {
		return;
	}
	
	@Override
	protected String[] tokenise() {
		return new String[0]; // no args
	}
	@Override
	protected void parseAndValidateArgs() throws ParseException {
		return; // no args
	}
	
}

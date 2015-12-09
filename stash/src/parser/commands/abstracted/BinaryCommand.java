//@@author A0131891E
package parser.commands.abstracted;

import java.text.ParseException;

/**
 * Abstract superclass for 2-ary commands
 * @author Leow Yijin
 */
public abstract class BinaryCommand extends NaryCommand {

	public static final int NUM_ARGS = 2;
	
	public BinaryCommand(String input) {
		super(input);
	}


	@Override
	protected String[] tokenise() throws ParseException {
		return super.tokenise(NUM_ARGS);
	}
}

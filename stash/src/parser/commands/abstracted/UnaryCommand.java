//@@author A0131891E
package parser.commands.abstracted;

/**
 * Abstract superclass for 1-ary commands
 * @author Leow Yijin
 */
public abstract class UnaryCommand extends NaryCommand {

	public static final int NUM_ARGS = 1;
	
	public UnaryCommand(String input) {
		super(input);
	}
	
	@Override
	protected String[] tokenise() {
		return new String[]{ ARG_STRING };
	}
}

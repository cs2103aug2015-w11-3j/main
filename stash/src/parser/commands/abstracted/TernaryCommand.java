//@@author A0131891E
package parser.commands.abstracted;

/**
 * Abstract superclass for 3-ary commands
 * @author Leow Yijin
 */
public abstract class TernaryCommand extends UnaryCommand {

	public static final int NUM_ARGS = 3;
	
	public TernaryCommand(String input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String[] tokenise() {
		return super.tokenise(NUM_ARGS);
	}

}

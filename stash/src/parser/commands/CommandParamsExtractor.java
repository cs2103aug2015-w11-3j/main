//@@author A0131891E
package parser.commands;

import java.text.ParseException;

/** Design Pattern: Command
 * Invoker for parser.commands.Command
 * 
 * Primitive specification, feel free to extend with 
 * utility logic and other stuff.
 * @author Leow Yijin
 */
public interface CommandParamsExtractor {
	CommandParams extractParams(Command userCommand) throws ParseException;
	public int getSuccessfulParses(); // count
}

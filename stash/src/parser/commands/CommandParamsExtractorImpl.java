//@@author A0131891E
package parser.commands;

import java.text.ParseException;

/** Design Pattern: Comand
 * 
 * Simple implementation of Command's invoker.
 * @author Leow Yijin
 */
public class CommandParamsExtractorImpl implements CommandParamsExtractor {

	private int numParsed;
	
	public CommandParamsExtractorImpl() {
		numParsed = 0;
	}

	@Override
	public CommandParams extractParams(Command userCommand) throws ParseException {
		numParsed++;
		return userCommand.getParams();
	}
	
	@Override
	public int getSuccessfulParses() {
		return numParsed;
	}
}

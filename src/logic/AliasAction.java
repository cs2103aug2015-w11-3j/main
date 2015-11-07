//@@author A0131891E
package logic;

import java.io.IOException;

import common.Configuration;
import common.Log;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.IllegalAliasException;
import logic.exceptions.LogicException;
import parser.Command;

public class AliasAction implements Action {

	private static final String RESERVED_ALIAS = "alias";
	private static final String USR_MSG_ALIAS_CLEAR = "All custom alias mappings cleared";
	private static final String USR_MSG_ALIAS_RESERVED = "You cannot use the reserved keyword \"alias\" as an alias";
	private static final String USR_MSG_ALIAS_SUCCESS = "Alias mapping created: %s --> %s";
	
    private Command cCommand;
    private TasksBag cBag;
    
    private String newAlias;
    private Command.Type aliasTarget;
    
	public AliasAction(Command cmd, TasksBag internalBag) {
        cCommand = cmd;
        cBag = internalBag;
        newAlias = cmd.getText();
        aliasTarget = cmd.getSecondaryCmdType();
	}

	@Override
	public Feedback execute() throws LogicException {
		
		if (aliasTarget == null) { // user wants to clear alias mappings
			try {
				Configuration.getInstance().clearUserAliases();
			} catch (IOException ioe) {
				Log.log(ioe.toString());
				ioe.printStackTrace();
				throw new LogicException("Config saving error: your changes may not persist.");
			}
			return new CommandFeedback(cCommand, cBag, USR_MSG_ALIAS_CLEAR);
		}
		
		assert newAlias != null && !"".equals(newAlias) && aliasTarget != Command.Type.INVALID;
		
		if (RESERVED_ALIAS.equals(newAlias)) {
			return new CommandFeedback(cCommand, cBag, USR_MSG_ALIAS_RESERVED);
		}
		
		try {
			Configuration.getInstance().setUserAlias(newAlias, aliasTarget);
		} catch (IOException ioe) {
			Log.log(ioe.toString());
			ioe.printStackTrace();
			throw new LogicException("Config saving error: your changes may not persist.");
		}
		return new CommandFeedback(cCommand, cBag, Utilities.formatString(USR_MSG_ALIAS_SUCCESS, newAlias, aliasTarget));
	}

}

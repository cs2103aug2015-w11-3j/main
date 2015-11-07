//@@author A0131891E
package logic;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import common.Configuration;
import common.Log;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.Aliases;
import parser.Command;

public class AliasAction implements Action {

	private static final String USR_MSG_ALIAS_CLEAR = "All custom alias mappings cleared";
	private static final String USR_MSG_ALIAS_RESERVED = "You cannot use the reserved keyword \"%s\" as an alias. Reserved keywords are shown in \"help\"";
	private static final String USR_MSG_ALIAS_SUCCESS = "Alias mapping created: %s --> %s";
	
	private static final Set<String> RESERVED_ALIASES = Aliases.getInstance().getReservedCmdTokens();
	private static final Pattern P_ILLEGAL_ALIAS = Pattern.compile("\\s|\\p{Lu}");
	
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
		
		// user wants to clear alias mappings
		if (aliasTarget == null) { 
			try {
				Configuration.getInstance().clearUserAliases();
			} catch (IOException ioe) {
				Log.log(ioe.toString());
				ioe.printStackTrace();
				throw new LogicException("Config saving error: your changes may not persist.");
			}
			return new CommandFeedback(cCommand, cBag, USR_MSG_ALIAS_CLEAR);
		}
		
		assert newAlias != null // if aliasTarget != null, must have newAlias.
				&& !"".equals(newAlias) // parser won't parse empty string as alias
				&& aliasTarget != Command.Type.INVALID // parser shouldnt give INVALID
				&& !P_ILLEGAL_ALIAS.matcher(newAlias).matches(); // parser will remove whitespace and tolowercase
		
		// Don't allow user to user to re-map reserved keywords
		if (RESERVED_ALIASES.contains(newAlias)) {
			return new CommandFeedback(cCommand, cBag, Utilities.formatString(USR_MSG_ALIAS_RESERVED, newAlias));
		}
		
		// Add new alias mapping and save to config file
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

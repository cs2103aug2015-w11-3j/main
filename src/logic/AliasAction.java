//@@author A0131891E
package logic;

import java.io.IOException;
import java.util.regex.Pattern;

import common.Log;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.Aliases;
import parser.AliasesImpl;
import parser.commands.CommandData;

public class AliasAction implements Action {

	private static final String USR_MSG_CONFIG_ERROR = "Config saving error: your changes may not persist.";
    private static final String USR_MSG_ALIAS_CLEAR = "All custom alias mappings cleared";
	private static final String USR_MSG_ALIAS_RESERVED = "You cannot use the reserved keyword \"%s\" as an alias. Reserved keywords are shown in \"help\"";
	private static final String USR_MSG_ALIAS_SUCCESS = "Alias mapping created: %s --> %s";
	
	private static final Pattern P_VALID_ALIAS = Pattern.compile("^[\\S&&[^\\p{javaUpperCase}]]++$");
	
	private static final Aliases ALIASES = AliasesImpl.getInstance();
	
    private CommandData cCommand;
    private TasksBag cBag;
    
    private String newAlias;
    private CommandData.Type aliasTarget;
    
	public AliasAction(CommandData cmd, TasksBag internalBag) {
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
				ALIASES.clearCustomAliases();
			} catch (IOException ioe) {
				Log.log(ioe.toString());
				ioe.printStackTrace();
				throw new LogicException(USR_MSG_CONFIG_ERROR);
			}
			return new CommandFeedback(cCommand, cBag, USR_MSG_ALIAS_CLEAR);
		}
		
		assert newAlias != null // if aliasTarget != null, must have newAlias.
				&& !"".equals(newAlias) // parser won't parse empty string as alias
				&& aliasTarget != CommandData.Type.INVALID // parser shouldnt give INVALID
				&& P_VALID_ALIAS.matcher(newAlias).matches(); // parser should have removed whitespace and tolowercase
		
		// Don't allow user to user to re-map reserved keywords
		if (ALIASES.isReservedCmdAlias(newAlias)) {
			return new CommandFeedback(cCommand, cBag, Utilities.formatString(USR_MSG_ALIAS_RESERVED, newAlias));
		}
		
		// Add new alias mapping and save to config file
		try {
			ALIASES.setCustomAlias(newAlias, aliasTarget);
		} catch (IOException ioe) {
			Log.log(ioe.toString());
			ioe.printStackTrace();
			throw new LogicException(USR_MSG_CONFIG_ERROR);
		}
		return new CommandFeedback(cCommand, cBag, Utilities.formatString(USR_MSG_ALIAS_SUCCESS, newAlias, aliasTarget));
    }

}

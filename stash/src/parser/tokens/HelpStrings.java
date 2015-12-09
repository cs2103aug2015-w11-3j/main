//@@author A0131891E
package parser.tokens;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import common.Utilities;
import parser.ParserFacade;
import parser.commands.CommandParams;

public final class HelpStrings {
	
    /////////////////////////////////////////////////////////////////
    // Human-readable strings for command formats (used by logic.HelpAction and UI popup)
    /////////////////////////////////////////////////////////////////
	
    public static final String HELP_LIST_ALL_CMDS = "ALL CMDS: add, delete, edit, mark, unmark, show, undo, redo, search, filter, clear, alias, move, skin, help, exit ";
    
    public static final String TOOLTIP_FORMAT_UPDATE = "Editing Task: [ %1$s \"task ID number\" name|start|end \"new value\" ]";
    
    
    
    
    public static final String TOOLTIP_FORMAT_FILTER = "Filter by Date: [ %1$s before|after \"reference date\" ] OR [ %1$s within \"earliest\" and \"latest\" ]";
    
    
    
    
    private static final Pattern P_NO_WHITESPACE = Pattern.compile("\\S*+");
    
    private static final TokenController ALIASES = DefaultTokenController.getInstance();
    
    private static final Map<CommandParams.CmdType, String> CMD_FORMATS = mapCmdToFormatStrings();

    public static String getHelpToolTip(String token){
    	assert(token != null && P_NO_WHITESPACE.matcher(token).matches());

    	CommandParams.CmdType cmdType = ALIASES.getCmdType(ParserFacade.cleanText(token));
    	assert(cmdType != CommandParams.CmdType.INVALID);
    	
    	if (cmdType == null) {
    		return null; // cannot parse as command token
    	}
    	
    	final String formatStr = CMD_FORMATS.get(cmdType);
    	return Utilities.formatString(formatStr, token);    	
    }
    
    private static Map<CommandParams.CmdType, String> mapCmdToFormatStrings () {
    	final Map<CommandParams.CmdType, String> map = new HashMap<>();
    	
    	return map;
    }
 
}

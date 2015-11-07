//@@author A0131891E
package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import common.Utilities;
import parser.Command.Type;

public final class HelpStrings {
	
    /////////////////////////////////////////////////////////////////
    // Human-readable strings for command formats (used by logic.HelpAction and UI popup)
    /////////////////////////////////////////////////////////////////
	
    public static final String HELP_LIST_ALL_CMDS = "ALL CMDS: add, delete, edit, mark, unmark, show, undo, redo, search, filter, clear, alias, move, skin, help, exit ";

    public static final String FORMAT_ADD = "Add New: [ %1$s \"name\" ]   [ %1$s \"name\"; due|start \"date\" ]   [ %1$s \"name\"; from \"start date\" to \"end date\" ]";
    public static final String FORMAT_DELETE = "Deleting Task: [ delete \"task ID number\" ]";
    public static final String FORMAT_UPDATE = "Editing Task: [ %1$s \"task ID number\" name|start|end \"new value\" ]";
    
    public static final String FORMAT_MARK = "Marking as Completed: [ %1$s \"task ID number\" ]";
    public static final String FORMAT_UNMARK = "Marking as Incomplete: [ %1$s \"task ID number\" ]";
    
    public static final String FORMAT_SHOW = "Changing View Tab: [ %1$ default ]   [ %1$s done ]   [ %1$s unfinished ]";
    
    public static final String FORMAT_UNDO = "Undo Previous Changes To Tasks: [ %1$s ]";
    public static final String FORMAT_REDO = "Redo Previously Undone Action: [ %1$s ]";
    
    public static final String FORMAT_SEARCH = "Searching in Names: [ %1$s \"words to find in task names\" ]";
    public static final String FORMAT_FILTER = "Filter by Date: [ %1$s before|after \"reference date\" ] OR [ %1$s within \"earliest\" and \"latest\" ]";
    public static final String FORMAT_MOVE = "Change Save File Location: [ %1$s \"new save file path\" ]";
    public static final String FORMAT_CLEAR = "Clear Current Search/Filters: [ %1$s ]";
    
    public static final String FORMAT_ALIAS = "Set Alias for Command: [ %1$s \"target command\" \"new alias\" ]   [ %1$s reset ]";
    public static final String FORMAT_THEME = "Change Colour Theme: [ %1$s day|night ]";
    
    public static final String FORMAT_QUIT = "Quit Celebi :( [ %1$s ]";
    public static final String FORMAT_HELP = "Get Help! [ %1$s ] ";
    
    
    private static final Pattern P_NO_WHITESPACE = Pattern.compile("\\S*+");
    
    private static final Aliases ALIASES = AliasesImpl.getInstance();
    
    private static final Map<Command.Type, String> CMD_FORMATS = mapCmdToFormatStrings();

    public static String getHelpToolTip(String token){
    	assert(token != null && P_NO_WHITESPACE.matcher(token).matches());

    	Type cmdType = ALIASES.getCmdFromAlias(Parser.cleanText(token));
    	assert(cmdType != Type.INVALID);
    	
    	if (cmdType == null) {
    		return null; // cannot parse as command token
    	}
    	
    	final String formatStr = CMD_FORMATS.get(cmdType);
    	return Utilities.formatString(formatStr, token);    	
    }
    
    private static Map<Command.Type, String> mapCmdToFormatStrings () {
    	final Map<Command.Type, String> map = new HashMap<>();
    	
    	map.put( Type.ADD, FORMAT_ADD );
    	map.put( Type.DELETE, FORMAT_DELETE );
    	map.put( Type.UPDATE, FORMAT_UPDATE );
    	
    	map.put( Type.MARK, FORMAT_MARK );
    	map.put( Type.UNMARK, FORMAT_UNMARK );
    	
    	map.put( Type.SHOW, FORMAT_SHOW );
    	
    	map.put( Type.UNDO, FORMAT_UNDO );
    	map.put( Type.REDO, FORMAT_REDO );
    	
    	map.put( Type.SEARCH, FORMAT_SEARCH );
    	map.put( Type.FILTER_DATE, FORMAT_FILTER );
    	map.put( Type.CLEAR_FILTERS, FORMAT_CLEAR );
    	
    	map.put( Type.ALIAS, FORMAT_ALIAS );
    	map.put( Type.THEME, FORMAT_THEME );
    	map.put( Type.QUIT, FORMAT_QUIT );
    	map.put( Type.HELP, FORMAT_HELP );
    	map.put( Type.MOVE, FORMAT_MOVE );
    	
    	return map;
    }
 
}

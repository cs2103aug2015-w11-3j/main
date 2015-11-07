//@@author A0131891E
package parser;

public class HelpStrings {
    /////////////////////////////////////////////////////////////////
    // Human-readable strings for command formats (used by logic.HelpAction)
    /////////////////////////////////////////////////////////////////
    public static final String HELP_LIST_ALL_CMDS = "< skin | help/quit | add/delete/edit | mark/unmark | show | undo/redo | search/filter/clear | move>";

    public static final String HELP_FORMAT_ADD = "\"add <name>\" OR \"add <name>; by <due date>\" OR \"add <name>; from <start date> to <end date>\"";
    public static final String HELP_FORMAT_DEL = "\"delete <task ID number>\"";
    public static final String HELP_FORMAT_UPD = "\"edit <task ID number> name/start/end <new value>\"";
    public static final String HELP_FORMAT_MARK = "\"mark <task ID number>\"";
    public static final String HELP_FORMAT_UNMARK = "\"unmark <task ID number>\"";
    public static final String HELP_FORMAT_SHOW = "\"\"";
    public static final String HELP_FORMAT_UNDO = "\"undo\"";
    public static final String HELP_FORMAT_REDO = "\"redo\"";
    public static final String HELP_FORMAT_SEARCH = "\"search <words to find in names>\"";
    public static final String HELP_FORMAT_FILTER = "\"filter before/after <reference date>\" OR \"filter between <earliest> and <latest>\"";
    public static final String HELP_FORMAT_MOVE = "\"move <new save file path>\"";
    public static final String HELP_FORMAT_QUIT = "\"quit\" OR \"q\"";
    public static final String HELP_FORMAT_HELP = "\"help\" OR \"help <command name (from list of cmds from \"help\")>\"";
    
    public static final String HELP_FORMAT_THEME = "\"theme/skin <day/night>\"";
    public static final String HELP_FORMAT_CLEAR = "\"clr OR clear OR reset\"";
    public static final String HELP_FORMAT_ALIAS = "\"alias <default command token> <your new alias>\"";
}

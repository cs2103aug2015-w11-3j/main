//@@author A0131891E
package parser;

import common.Utilities;

public class HelpStrings {
    /////////////////////////////////////////////////////////////////
    // Human-readable strings for command formats (used by logic.HelpAction)
    /////////////////////////////////////////////////////////////////
    public static final String HELP_LIST_ALL_CMDS = "ALL CMDS: skin | exit/quit | add/delete/edit | mark/unmark | show | undo/redo | search/filter/clear | move";

    public static final String HELP_FORMAT_ADD = "Add New: \"%1$s <name>\"   \"%1$s <name>; due/starting <date>\"   \"%1$s <name>; from <start date> to <end date>\"";
    public static final String HELP_FORMAT_DEL = "Deleting Task: \"delete <task ID number>\"";
    public static final String HELP_FORMAT_UPD = "Editing Task: \"edit <task ID number> name/start/end <new value>\"";
    public static final String HELP_FORMAT_MARK = "Marking as Completed: \"%1$s <task ID number>\"";
    public static final String HELP_FORMAT_UNMARK = "Marking as Incomplete: \"%1$s <task ID number>\"";
    public static final String HELP_FORMAT_SHOW = "Changing View Tab: \"%1$ default\"   \"%1$s done\"   \"%1$s unfinished\"";
    public static final String HELP_FORMAT_UNDO = "Undo Previous Changes To Tasks: \"%1$s\"";
    public static final String HELP_FORMAT_REDO = "Redo Previously Undone Action: \"%1$s\"";
    public static final String HELP_FORMAT_SEARCH = "Searching in Names: \"%1$s <words to find in task names>\"";
    public static final String HELP_FORMAT_FILTER = "Filter by Date: \"%1$s before/after <reference date>\" OR \"%1$s within <earliest> and <latest>\"";
    public static final String HELP_FORMAT_MOVE = "Change Save File Location: \"%1$s <new save file path>\"";
    public static final String HELP_FORMAT_QUIT = "Quit Celebi :( \"%1$s\"";
    public static final String HELP_FORMAT_HELP = "Get Help! \"%1$s\"  \"%1$s <any command>)>\"";
    
    public static final String HELP_FORMAT_THEME = "Change Colour Theme: \"%1$s day/night\"";
    public static final String HELP_FORMAT_CLEAR = "Clear Current Search/Filters: \"%1$s\"";
    public static final String HELP_FORMAT_ALIAS = "Set Alias for Command: \"%1$s <target command> <new alias>\"   \"%1$s reset\"";

    public static String getHelpToolTip(String hint){
        switch(hint){
            case "add":
                return HELP_FORMAT_ADD;
        }
        return null;
    }
    
}

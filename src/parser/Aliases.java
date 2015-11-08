//@@author A0131891E
package parser;

import java.io.IOException;

import parser.commands.CommandData;

/**
 * Handles all alias mapping and storage
 * 
 */
public abstract class Aliases {

	/////////////////////////////////////////////////////
	// Methods for command tokens
	/////////////////////////////////////////////////////
	
	public boolean isCmdAlias (String alias) {
		return isCustomAlias(alias) || isDefaultAlias(alias);
	};
	public abstract boolean isCustomAlias (String alias);
	public abstract boolean isDefaultAlias (String alias);
	public abstract boolean isReservedCmdAlias (String alias);
	
	public abstract CommandData.Type getCmdFromAlias (String alias);
	
	public abstract void setCustomAlias (String alias, CommandData.Type target) throws IOException;
	public abstract void clearCustomAliases () throws IOException;
	
	
	/////////////////////////////////////////////////////
	// Aliases for command tokens
	/////////////////////////////////////////////////////
	
	// cannot be used as alias (reserved)
	// make sure to update for new commands
	static final String[] CMD_RESERVED = {
		"add",
		"edit",
		"delete",
		
		"show",
		
		"mark",
		"unmark",
		
		"undo",
		"redo",
		
		"search",
		"filter",
		"clear",
		
		"skin",
		"help",
		"exit",
		"move"
	};
	
	static final String[] CMD_ADD = {
		"a",
		"add",
		"new",
		"create"
	};
	static final String[] CMD_UPD = {
		"upd",
		"update",
		"set",
		"edit"
	};
	static final String[] CMD_DEL = {
		"d",
		"del",
		"delete",
		"rm",
		"remove"
	};
	static final String[] CMD_MARK = {
		"done",
		"finish",
		"mark",
		"complete"
	};
	static final String[] CMD_UNMARK = {
		"unmark",
		"reopen",
		"uncomplete"
	};
	static final String[] CMD_UNDO = {
		"u",
		"un",
		"undo"
	};
	static final String[] CMD_REDO = {
		"redo",
		"re" 
	};
	static final String[] CMD_SHOW = {
		"show",
		"view",
		"display"
	};
	static final String[] CMD_SEARCH = {
		"s",
		"find",
		"search"
	};
	static final String[] CMD_FILTER = {
		"f",
		"fil",
		"filter"
	};
	static final String[] CMD_CLEAR = {
		"clr",
		"cls",
		"clear",
		"reset"
	};
	static final String[] CMD_MOVE = {
		"mv",
		"move"
	};
	static final String[] CMD_ALIAS = {
		"map",
		"alias",
		"shortcut",
		"point",
	};
	static final String[] CMD_HELP = {
		"help",
		"?",
		"-h"
	};
	static final String[] CMD_THEME = {
		"skin",
		"theme",
		"color",
		"colour"
	};
	static final String[] CMD_QUIT = {
		"q",	
		"quit",
		"exit"
	};
	
	/////////////////////////////////////////////////////
	// Aliases for Task field tokens
	/////////////////////////////////////////////////////
	static final String[] FIELD_NAME = {
		"name",
		"title",
		"text",
		"descr",
		"description"
	};
	static final String[] FIELD_START_DATE = {
		"at",
		"start",
		"starting",
		"from",
		"begin",
		"on"
	};
	static final String[] FIELD_END_DATE = {
		"by",
		"finish",
		"to",
		"end",
		"due",
		"till",
		"until"
	};

	/////////////////////////////////////////////////////
	// Aliases for show command arguments
	/////////////////////////////////////////////////////
	static final String[] VIEW_DEFAULT = {
		"",
		"urgent",
		"default",
		"upcoming"
	};
	static final String[] VIEW_INCOMPLETE = {
		"undone",
		"open",
		"incomplete",
		"unfinished",
		"pending"
	};
	static final String[] VIEW_COMPLETED = {
		"done",
		"closed",
		"complete",
		"completed",
		"finished"
	};
	
	/////////////////////////////////////////////////////
	// Aliases for filter command delimiters
	/////////////////////////////////////////////////////
	static final String[] FILTER_ARG_BEF = {
		"by",
		"bef",
		"before"
	};
	static final String[] FILTER_ARG_AFT = {
		"aft",
		"after"
	};
	static final String[] FILTER_ARG_BTW_START = {
		"from",
		"start",
		"b/w",
		"btw",
		"between",
		"range",
		"within"
	};
	static final String[] FILTER_ARG_BTW_END = {
		"by",
		"to",
		"end",
		"and",
		"till",
		"until"
	};

    /////////////////////////////////////////////////////
    // Aliases for themes
    /////////////////////////////////////////////////////
	static final String[] THEME_DAY = {
        "day",
        "light",
        "bright",
        "white"
	};
	static final String[] THEME_NIGHT = {
        "night",
        "shadow",
        "dark",
        "black"
	};
	
    /////////////////////////////////////////////////////////
    // Aliases for special update date values to remove date
    /////////////////////////////////////////////////////////
	// for UPDATE cmd removing of dates (task conversion event->deadline->float}
	static final String[] CLEAR_VAL = {
		"nil",
		"null",
		"none",
		"remove",
		"clear",
		"empty",
		"reset"
	};
	
}

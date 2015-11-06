//@@author A0131891E
package parser;

import static common.Utilities.*;

/**
 * This class holds all user input token aliases.
 * Token parsing logic will reference the data here, so
 * you can easily add/remove/change/view aliases here and
 * have any changes propogate to the relevant classes. 
 */
public final class Aliases {
	// Disable instantiation
	private Aliases () {
	}

	
	/////////////////////////////////////////////////////
	// Aliases for command tokens
	/////////////////////////////////////////////////////
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
	static final String[] CMD_QUIT = {
		"q",	
		"quit",
		"exit"
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
		"search",
	};
	static final String[] CMD_FILTER = {
		"f",
		"fil",
		"filter"
	};
	static final String[] CMD_MOVE = {
		"mv",
		"move"
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
	static final String[][] TOKENS_CMD = {
		CMD_ADD,
		CMD_DEL,
		CMD_UPD,
		CMD_QUIT,
		CMD_MARK,
		CMD_UNMARK,
		CMD_UNDO,
		CMD_REDO,
		CMD_SHOW,
		CMD_SEARCH,
		CMD_FILTER,
		CMD_MOVE,
		CMD_HELP,
		CMD_THEME
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
		"upcoming",
	};
	static final String[] VIEW_INCOMPLETE = {
		"undone",
		"open",
		"incomplete",
		"unfinished",
		"pending"
	};
	static final String[] VIEW_COMPLETE = {
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
        "bright"
	};
	static final String[] THEME_NIGHT = {
        "night",
        "shadow",
        "dark"
	};
	
	/*
	 * returns deep clone of TOKENS (preserves TOKENS immutability)
	 * does not clone Strings (java Strings already immutable)
	 * 
	 * Currently used by ui.view.CelebiViewController
	 * for real-time user input command syntax highlighting.
	 */
	public static final String[][] getValidCmdTokens () {
		return str2dArrayClone(TOKENS_CMD);
	}

}

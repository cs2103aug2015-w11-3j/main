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
			"u",	
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
			"reopen" 
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
			"view"
	};
	static final String[] CMD_SEARCH = {
			"search",
			"find"
	};
	static final String[] CMD_FILTER = {
			"fil",
			"filter"
	};
	static final String[] CMD_CHANGE_SAVE_LOC = {
			"mv",
			"move"
	};
	static final String[] CMD_HELP = {
			"help",
			"?"
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
			CMD_CHANGE_SAVE_LOC,
			CMD_HELP
	};
	
	/////////////////////////////////////////////////////
	// Aliases for Task field tokens
	/////////////////////////////////////////////////////
	static final String[] FIELD_NAME = {
			"name",
			"title",
			"text"
	};
	static final String[] FIELD_START_DATE = {
			"start",
			"from",
			"begin"
	};
	static final String[] FIELD_END_DATE = {
			"end",
			"by",
			"due",
			"till",
			"until"
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

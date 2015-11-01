//@@author A0131891E
package parser;

import static common.Utilities.*;

/**
 * This class holds all static immutable parser-related data used by any class.
 * In effect, it acts as a canonical data storage. 
 * 
 * Only the parser package classes can change it during runtime (unadvisable). 
 * Classes outside this package cannot mutate the data inside.
 * 
 * This purpose of this class is to make it easy for developers to implement
 * any changes related to parsing in general; 
 * 
 * for example, if you want to add a new alias for the "add" command, 
 * you only need to add the new token to the TOKENS_ADD array, and the 
 * change will be propogated to any classes that reference TOKENS_ADD.
 * 
 * Note: Runtime changed will not be propogated.
 */
public final class ParserReferenceData {
	// Disable instantiation
	private ParserReferenceData() {
	}

	/////////////////////////////////////////////////////
	// Add
	/////////////////////////////////////////////////////
	static final String[] TOKENS_ADD = {
			"a",
			"add",
			"new",
			"create"
	};
	static final String[] TOKENS_UPD = {
			"u",	
			"upd",
			"update",
			"set",
			"edit"
	};
	static final String[] TOKENS_DEL = {
			"d",
			"del",
			"delete",
			"rm",
			"remove"
	};
	static final String[] TOKENS_QUIT = {
			"q",	
			"quit",
			"exit"
	};
	static final String[] TOKENS_MARK = {
			"done",
			"finish",
			"mark",
			"complete"
	};
	static final String[] TOKENS_UNMARK = {
			"unmark",
			"reopen" 
	};
	static final String[] TOKENS_UNDO = {
			"undo",
			"un" 
	};
	static final String[] TOKENS_REDO = {
			"redo",
			"re" 
	};
	static final String[] TOKENS_SHOW = {
			"show",
			"view"
	};
	static final String[] TOKENS_SEARCH = {
			"search",
			"find"
	};
	static final String[] TOKENS_FILTER = {
			"fil",
			"filter"
	};
	static final String[] TOKENS_CHANGE_SAVE_LOC = {
			"mv",
			"move"
	};
	static final String[] TOKENS_HELP = {
			"help",
			"?"
	};
	static final String[][] TOKENS = {
			TOKENS_ADD,
			TOKENS_DEL,
			TOKENS_UPD,
			TOKENS_QUIT,
			TOKENS_MARK,
			TOKENS_UNMARK,
			TOKENS_UNDO,
			TOKENS_REDO,
			TOKENS_SHOW,
			TOKENS_SEARCH,
			TOKENS_FILTER,
			TOKENS_CHANGE_SAVE_LOC,
			TOKENS_HELP
	};

	/*
	 * returns deep clone of TOKENS (preserves TOKENS immutability)
	 * does not clone Strings (java Strings already immutable)
	 * Currently used by ui.view.CelebiViewController
	 * for real-time user input command syntax highlighting.
	 */
	public static final String[][] getValidCmdTokens () {
		return str2dArrayClone(TOKENS);
	}

}

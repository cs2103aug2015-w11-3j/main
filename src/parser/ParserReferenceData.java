//@@author A0131891E
package parser;

import static common.Utilities.*;

/**
 * This class holds all static immutable parser-related data used by any class.
 * In effect, it acts as a canonical data storage. Only the parser package classes
 * can change it during runtime (unadvisable). For all intents and purposes though,
 * the data here is immutable.
 * 
 * Classes outside parser package cannot mutate the data in this class.
 * 
 */
public final class ParserReferenceData {
	// Disable instantiation
	private ParserReferenceData() {
	}

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

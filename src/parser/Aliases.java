//@@author A0131891E
package parser;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Arrays;

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
	static final Set<String> CMD_ADD = createSet(
		"a",
		"add",
		"new",
		"create"
	);
	static final Set<String> CMD_UPD = createSet(
		"upd",
		"update",
		"set",
		"edit"
	);
	static final Set<String> CMD_DEL = createSet(
		"d",
		"del",
		"delete",
		"rm",
		"remove"
	);
	static final Set<String> CMD_QUIT = createSet(
		"q",	
		"quit",
		"exit"
	);
	static final Set<String> CMD_MARK = createSet(
		"done",
		"finish",
		"mark",
		"complete"
	);
	static final Set<String> CMD_UNMARK = createSet(
		"unmark",
		"reopen",
		"uncomplete"
	);
	static final Set<String> CMD_UNDO = createSet(
		"u",
		"un",
		"undo"
	);
	static final Set<String> CMD_REDO = createSet(
		"redo",
		"re" 
	);
	static final Set<String> CMD_SHOW = createSet(
		"show",
		"view",
		"display"
	);
	static final Set<String> CMD_SEARCH = createSet(
		"s",
		"find",
		"search"
	);
	static final Set<String> CMD_FILTER = createSet(
		"f",
		"fil",
		"filter"
	);
	static final Set<String> CMD_MOVE = createSet(
		"mv",
		"move"
	);
	static final Set<String> CMD_HELP = createSet(
		"help",
		"?",
		"-h"
	);
	static final Set<String> CMD_THEME = createSet(
		"skin",
		"theme",
		"color",
		"colour"
	);
	
	/////////////////////////////////////////////////////
	// Aliases for Task field tokens
	/////////////////////////////////////////////////////
	static final Set<String> FIELD_NAME = createSet(
		"name",
		"title",
		"text",
		"descr",
		"description"
	);
	static final Set<String> FIELD_START_DATE = createSet(
		"at",
		"start",
		"from",
		"begin",
		"on"
	);
	static final Set<String> FIELD_END_DATE = createSet(
		"by",
		"finish",
		"to",
		"end",
		"due",
		"till",
		"until"
	);

	/////////////////////////////////////////////////////
	// Aliases for show command arguments
	/////////////////////////////////////////////////////
	static final Set<String> VIEW_DEFAULT = createSet(
		"",
		"urgent",
		"default",
		"upcoming"
	);
	static final Set<String> VIEW_INCOMPLETE = createSet(
		"undone",
		"open",
		"incomplete",
		"unfinished",
		"pending"
	);
	static final Set<String> VIEW_COMPLETE = createSet(
		"done",
		"closed",
		"complete",
		"completed",
		"finished"
	);
	
	/////////////////////////////////////////////////////
	// Aliases for filter command delimiters
	/////////////////////////////////////////////////////
	static final Set<String> FILTER_ARG_BEF = createSet(
		"bef",
		"before"
	);
	static final Set<String> FILTER_ARG_AFT = createSet(
		"aft",
		"after"
	);
	static final Set<String> FILTER_ARG_BTW_START = createSet(
		"from",
		"start",
		"b/w",
		"btw",
		"between",
		"range",
		"within"
	);
	static final Set<String> FILTER_ARG_BTW_END = createSet(
		"to",
		"end",
		"and",
		"till",
		"until"
	);

    /////////////////////////////////////////////////////
    // Aliases for themes
    /////////////////////////////////////////////////////
	static final Set<String> THEME_DAY = createSet(
        "day",
        "light",
        "bright",
        "white"
	);
	static final Set<String> THEME_NIGHT = createSet(
        "night",
        "shadow",
        "dark",
        "black"
	);
	
	/*
	 * returns a Set containing all valid command tokens
	 * Currently used by ui.view.CelebiViewController
	 * for real-time user input command syntax highlighting.
	 */
	public static final Set<String> getValidCmdTokens () {
		final Set<String> allTokens = new LinkedHashSet<String>();
		allTokens.addAll(CMD_ADD);
		allTokens.addAll(CMD_UPD);
		allTokens.addAll(CMD_QUIT);
		allTokens.addAll(CMD_MARK);
		allTokens.addAll(CMD_UNMARK);
		allTokens.addAll(CMD_UNDO);
		allTokens.addAll(CMD_REDO);
		allTokens.addAll(CMD_SHOW);
		allTokens.addAll(CMD_SEARCH);
		allTokens.addAll(CMD_FILTER);
		allTokens.addAll(CMD_MOVE);
		allTokens.addAll(CMD_HELP);
		allTokens.addAll(CMD_THEME);
		return allTokens;
	}
	
	@SafeVarargs
	private static final <E> Set<E> createSet (E... elements) {
		return new LinkedHashSet<E>(Arrays.asList(elements));
	}
}

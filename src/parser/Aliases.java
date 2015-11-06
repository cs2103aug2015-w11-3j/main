//@@author A0131891E
package parser;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Collection;


/**
 * This class holds all user input token aliases.
 * Token parsing logic will reference the data here, so
 * you can easily add/remove/change/view aliases here and
 * have any changes propogate to the relevant classes. 
 * 
 * NOTE: ALIAS STRINGS CANNOT CONTAIN WHITESPACE
 */
public final class Aliases {

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
		"->"
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
	
	private static Aliases instance = null;
	public static Aliases getInstance () {
		if (instance == null) {
			instance = new Aliases();
		}
		return instance;
	}
	
	// Stores the default alias->command.type mapping
	private final Map<String, Command.Type> ALIAS_MAP_CMD_TYPE;
	
	private Aliases () {
		
		ALIAS_MAP_CMD_TYPE = new LinkedHashMap<>();
		mapCmdTypeAliases();
		
	}
	
	private final void mapCmdTypeAliases () {

		mapAliases(CMD_ADD, Command.Type.ADD);
		mapAliases(CMD_UPD, Command.Type.UPDATE);
		mapAliases(CMD_DEL, Command.Type.DELETE);
		
		mapAliases(CMD_MARK, Command.Type.MARK);
		mapAliases(CMD_UNMARK, Command.Type.UNMARK);
		
		mapAliases(CMD_UNDO, Command.Type.UNDO);
		mapAliases(CMD_REDO, Command.Type.REDO);
		
		mapAliases(CMD_SHOW, Command.Type.SHOW);
		
		mapAliases(CMD_SEARCH, Command.Type.SEARCH);
		mapAliases(CMD_FILTER, Command.Type.FILTER_DATE);
		mapAliases(CMD_CLEAR, Command.Type.CLEAR_FILTERS);
		
		mapAliases(CMD_MOVE, Command.Type.MOVE);
		mapAliases(CMD_ALIAS, Command.Type.ALIAS);
		
		mapAliases(CMD_HELP, Command.Type.HELP);
		mapAliases(CMD_THEME, Command.Type.THEME);
		
		mapAliases(CMD_QUIT, Command.Type.QUIT);
	}
	
	
	private final void mapAliases (String[] aliases, Command.Type cmdType) {
		mapAliasesToValue(ALIAS_MAP_CMD_TYPE, cmdType, Arrays.asList(aliases));
	}
	
	// getter returns clone to preserve immutability
	public final Map<String, Command.Type> getAliasMap () {
		return new LinkedHashMap<>(ALIAS_MAP_CMD_TYPE);
	}
	
	/**
	 * Maps every key in keys to value, inside map.
	 * If any duplicate is detected in the map, assertion kills program.
	 * Since this is used only for alias mapping, any detected duplicates
	 * imply an error on the dev's part in specifying the aliases above.
	 * @param map : mappings are stored here.
	 * @param value : to be mapped by all aliases
	 * @param aliases : to be mapped to value
	 */
	private static final <V> void mapAliasesToValue (Map<String, V> map, V value, Collection<String> aliases) {
		assert(map != null && aliases != null && value != null);
		for (String alias : aliases) {
			assert map.put(alias, value) == null : "duplicate key binding " + alias ; // no duplicates allowed
		}
	}
}

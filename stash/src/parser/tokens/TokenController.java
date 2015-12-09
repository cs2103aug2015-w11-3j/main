//@@author A0131891E
package parser.tokens;

import java.io.IOException;

import common.Task.DataType;
import common.TasksBag.ViewType;
import parser.commands.ClearCommand;
import parser.commands.CommandParams;
import parser.commands.HelpCommand;
import parser.commands.QuitCommand;
import parser.commands.RedoCommand;
import parser.commands.ThemeCommand;
import parser.commands.UndoCommand;
import parser.commands.CommandParams.CmdType;
import ui.view.CelebiViewController.Skin;

/** 
 * Handles all requests related to validation of individual
 * tokens when parsing user input.
 * 
 * tokens are either keywords or values.
 * values are either names, words, filepaths, or dates.
 * 
 * @author Leow Yijin
 */
public interface TokenController {
	
	public void init(); 
	
	// ========== CmdType KEYWORDS ===============
	
	// check for match with defined keywords
	boolean isCmdKeyword(String keyword); 		// full search
	boolean isCustomCmdAlias(String keyword); 	// aliases can overwrite defaults
	boolean isDefaultCmdKeyword(String keyword); // keywords we pre-defined
	boolean isReservedCmdKeyword(String keyword); // cannot be used as alias
	
	CmdType getCmdType(String key); // Keyword -> CommandParams.CmdType  // null if no match
	
	// ========== altering User Custom CmdType Alias ===============
	void setCustomAlias(String alias, CommandParams.CmdType target) throws IOException;
	void clearCustomAliases() throws IOException; 
	
	// Returns display string for popup help based on first token of input
	String getHelpTooltip(String firstToken);  // null if no match

	
	// Keyword -> CelebiViewController.Skin
	Skin getSkin(String key);  // null if no match
	// Keyword -> TasksBag.ViewType
	ViewType getViewType(String key);  // null if no match
	
	// ========== Task.FieldType KEYWORDS ===============
	
	public DataType getFieldType(String token); // null if no match
	
	String getRegexNameField();
	String getRegexStartField();
	String getRegexEndField();
	
	boolean isStartFieldId(String token);
	boolean isNameFieldId(String token);
	boolean isEndFieldId(String token);
	
	// for parsing special null/reset value
	boolean isResetValue(String token);
	
	// for parsing filter command special keywords
	String getRegexFilterBefore();
	String getRegexFilterAfter();
	String getRegexFilterBtwFront();
	String getRegexFilterBtwDelim();
	
	// =========== misc ============
	
	static String cleanText(String text) {
		return text.trim().toLowerCase();
	}
	
	
	/////////////////////////////////////////////////////
	// Keywords for command type identifier
	/////////////////////////////////////////////////////
	
	static final String[] CMD_FILTER = {
		"f",
		"fil",
		"filter"
	};
	

	/////////////////////////////////////////////////////
	// Keywords for Task data field identifiers
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
	// Keywords for views
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
	static final String[] FILTER_ARG_BTW_FRONT = {
		"b/w",
		"btw",
		"between",
		"range",
		"within"
	};
	static final String[] FILTER_ARG_BTW_DELIM = {
		"and",
		"to"
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
	static final String[] RESET_VAL = {
		"nil",
		"null",
		"none",
		"remove",
		"clear",
		"empty",
		"reset"
	};

	public static final Class<?>[] ALL_COMMANDS = {
			// 0-ary
			QuitCommand.class,
			HelpCommand.class,
			UndoCommand.class,
			RedoCommand.class,
			ClearCommand.class,
			// 1-ary
			ThemeCommand.class
//			DeleteCommand.class,
//			MarkCommand.class,
//			UnmarkCommand.class,
//			ShowCommand.class,
//			SearchCommand.class,
//			MoveCommand.class,
//			// 2-ary
//			AliasCommand.class,
//			// 3-ary
//			UpdateCommand.class,
//			// variadic
//			AddCommand.class,
//			FilterCommand.class
	};
}

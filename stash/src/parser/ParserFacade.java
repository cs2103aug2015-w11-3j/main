//@@author A0131891E
package parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import parser.tokens.TokenController;

import static ui.view.CelebiViewController.Skin;
import common.TasksBag;
import parser.commands.CommandParams;

/** Design Pattern: Facade
 * 
 * Facade for all external facing functionality in the Parser component.
 * External components will only interact with this interface
 * and the parser.commands.CommandData data-type interface.
 * 
 * Subsystems covered by this facade class:
 * 
 * ========= Logic related parsing/updating ============
 * 
 * @see parser.commands.CommandParamsParser
 * PARSE_CMD: (parsing) 
 * 		{ String -> CommandParams } 
 *  	Parses relevant command data from full user input.
 * 
 * @see parser.tokens.TokenController
 * UPDATE_CUSTOM_ALIASES: (update custom keywords)
 *  	Processes and redirects requests for changes in user's alias mappings.	
 * 
 * ========= UI related parsing/formatting ============
 * 
 * @see parser.temporal.DateFormatter
 * FORMAT_DATE: (formatting) 
 * 		{ Date -> String } 
 * 		Formats Date into conveniently readable string.
 * 
 * @see parser.tokens.TokenController
 * FORMAT_HELP: (formatting) 
 * 		{ String -> String } 
 * 		Gets correct text for GUI help popup based on user input's first token.
 * 
 * @see parser.tokens.TokenController
 * CMD_KEYWORD_CHECKING: (checking)
 * 		{ String -> boolean }
 * 		Checks if a String matches any command keywords.
 * 
 * ======================= Misc ===========================
 * 
 * @see parser.tokens.TokenController
 * TOKEN_PARSING: (parsing) 
 * 		{ String -> boolean/desired Type } 
 * 		User input string is tokenised during parsing.
 * 		Each token is either a KEYWORD or a VALUE
 * 		Values 
 * 
 * @see parser.commands.Command
 * CREATE_CMD: (factory) 
 * 		{ var args -> CommandData }
 * 		Bypasses PARSE_CMD subsystem to create CommandData objects.
 *  	Might be useful for testing.
 * 
 * @author Leow Yijin
 */
public interface ParserFacade extends Parser<CommandParams>, Formatter<Date> {
	
	void init(); // called during app startup
	
	
	// PARSE_CMD subsystem
	// (superinterface) CommandParams parse(String);
	
	// FORMAT_DATE subsystem
	// (superinterface) String formatDate(Date)
	
	// FORMAT_HELP subsystem
	String getHelpTooltip(String firstToken);
	
	// CMD_KEYWORD_CHECKING subsystem
	boolean isCmdKeyword(String keyword); 			// Checks in custom and default keywords
	boolean isCustomCmdAlias(String keyword); 		// User defined custom aliases
	boolean isDefaultCmdKeyword(String keyword); 	// Command keywords we pre-defined
	boolean isReservedCmdKeyword(String keyword); 	// Cannot be overwritten as alias
	
	// UPDATE_CUSTOM_ALIASES subsystem
	
	// CREATE_CMD subsystem
	CommandParams makeAdd(String userRawInput, String name, Date start, Date end);
	CommandParams makeUpdateName(String userRawInput, int taskUID, String newName);
	CommandParams makeUpdateStart(String userRawInput, int taskUID, Date newDate);
	CommandParams makeUpdateEnd(String userRawInput, int taskUID, Date newDate);
	CommandParams makeDelete(String userRawInput, int taskUID);
	CommandParams makeQuit(String userRawInput);
	CommandParams makeInvalid(String userRawInput);
	CommandParams makeShow(String userRawInput, TasksBag.ViewType viewType);
	CommandParams makeRedo(String userRawInput);
	CommandParams makeUndo(String userRawInput);
	CommandParams makeMark(String userRawInput, int taskUID);
	CommandParams makeUnmark(String userRawInput, int taskUID);
	CommandParams makeSearch(String userRawInput, String searchKey);
	CommandParams makeFilterDate(String userRawInput, Date rangeStart, Date rangeEnd);
	CommandParams makeClear(String userRawInput);
	CommandParams makeMove(String userRawInput, Path newPath);
	CommandParams makeHelp(String userRawInput);
	CommandParams makeTheme(String userRawInput, Skin theme);
	CommandParams makeAlias(String userRawInput, String alias, CommandParams.CmdType target);
	
	// to debug command
	static void printCmd (CommandParams c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("text: " + c.getText());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
		System.out.println("path: "+ c.getPath());
		System.out.println("theme: "+ c.getTheme());
		System.out.println("2nd cmd type: "+ c.getSecondaryCmdType());
		System.out.println("View: "+ c.getViewType());
	}

	static String cleanText (String token) {
		return TokenController.cleanText(token);
	}
	static Path parsePath (String token) throws ParseException {
		assert(token != null);
		return Paths.get(token.trim());
	}
}

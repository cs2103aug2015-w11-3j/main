//@@author A0131891E
package parser.commands;

import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import common.Task;
import common.TasksBag;
import parser.ParserFacade;
import parser.DefaultParserFacade;
import ui.view.CelebiViewController.Skin;

/** Design Pattern: Command
 * 
 * Root abstract superclass for all Command types.
 * Will be invoked by CommandParserInvoker.
 * 
 * Provides a static Pattern for default tokenising
 * where the default delimiter is variable length whitespace.
 * 
 * @author Leow Yijin
 */
public abstract class Command {

	public static final String WARNING_MSG = "Wrong format for \"%s\"";
	public static final String REGEX_WHITESPACE =
			"\\s+";
	protected static final Pattern P_DEFAULT_TOKEN_DELIMITER = 
			Pattern.compile(REGEX_WHITESPACE);
	
	protected final String INPUT, ARG_STRING, CMD_WORD;
	protected CommandParams cmdParams;
	protected String[] tokens;
	
	protected Command(String input) {
		INPUT = input;
		String[] cmdAndArgs = P_DEFAULT_TOKEN_DELIMITER.split(INPUT, 2);
		CMD_WORD = cmdAndArgs[0];
		if (cmdAndArgs.length < 2) {
			ARG_STRING = "";
		} else {
			ARG_STRING = cmdAndArgs[1];
		}
	}

	// Command pattern execute() analog
	public CommandParams getParams() throws ParseException {
		tokens = tokenise();
		parseAndValidateArgs();
		cmdParams = prepareCmdParams();
		return cmdParams;
	}
	
	// Extract tokens to facilitate argument parsing.
	protected abstract String[] tokenise() throws ParseException;
	
	// Parse the tokens into their actual arg values.
	protected abstract void parseAndValidateArgs() throws ParseException;
	
	// Fill the cmdParams object with arg values.
	protected abstract CommandParams prepareCmdParams();	
	
	
	///////////////////////////////////////////////////////////////
	// Utility methods for generating CommandData objects directly
	// Currently uses CommandDataImpl as the concrete class
	// Used by ParserController facade in CREATE_CMD subsystem
	//////////////////////////////////////////////////////////////
	
	public static CommandParams makeAdd(String userRawInput, String name, Date start, Date end) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.ADD, userRawInput);
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setText(name);
		return cmd;
	}
	public static CommandParams makeUpdateName(String userRawInput, int taskUID, String newName) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.NAME);
		cmd.setTaskUID(taskUID);
		cmd.setText(newName);
		return cmd;	
	}
	public static CommandParams makeUpdateStart(String userRawInput, int taskUID, Date newDate) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.DATE_START);
		cmd.setTaskUID(taskUID);
		cmd.setStart(newDate);
		return cmd;	
	}
	public static CommandParams makeUpdateEnd(String userRawInput, int taskUID, Date newDate) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.DATE_END);
		cmd.setTaskUID(taskUID);
		cmd.setEnd(newDate);
		return cmd;	
	}
	public static CommandParams makeDelete(String userRawInput, int taskUID) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.DELETE, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public static CommandParams makeShow(String userRawInput, TasksBag.ViewType view) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.SHOW, userRawInput);
		cmd.setViewType(view);
		return cmd;		
	}
	public static CommandParams makeRedo(String userRawInput) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.REDO, userRawInput);
		return cmd;
	}
	public static CommandParams makeUndo(String userRawInput) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.UNDO, userRawInput);
		return cmd;
		
	}
	public static CommandParams makeMark(String userRawInput, int taskUID) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.MARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public static CommandParams makeUnmark(String userRawInput, int taskUID) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.UNMARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public static CommandParams makeSearch(String userRawInput, String keywords) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.SEARCH, userRawInput);
		cmd.setText(keywords);
		return cmd;
	}
	public static CommandParams makeFilterDate(String userRawInput, Date rangeStart, Date rangeEnd) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.FILTER_DATE, userRawInput);
		cmd.setStart(rangeStart);
		cmd.setEnd(rangeEnd);
		return cmd;
	}
	public static CommandParams makeClear(String userRawInput) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.CLEAR_FILTERS, userRawInput);
		return cmd;
	}
	public static CommandParams makeMove(String userRawInput, Path newPath) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.MOVE, userRawInput);
		cmd.setPath(newPath);
		return cmd;
	}
	public static CommandParams makeHelp(String userRawInput) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.HELP, userRawInput);
		return cmd;
	}
	public static CommandParams makeQuit(String userRawInput) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.QUIT, userRawInput);
		return cmd;
	}
	public static CommandParams makeTheme(String userRawInput, Skin theme) {
        final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.THEME, userRawInput);
	    cmd.setTheme(theme);
	    return cmd;
	}
	public static CommandParams makeAlias(String userRawInput, String alias, CommandParams.CmdType target) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.ALIAS, userRawInput);
		cmd.setText(alias);
		cmd.setSecondaryCmdType(target);
		return cmd;
	}
	public static CommandParams makeInvalid(String userRawInput) {
		final CommandParams cmd = new CommandParamsImpl(CommandParams.CmdType.INVALID, userRawInput);
		return cmd;
	}
	
}

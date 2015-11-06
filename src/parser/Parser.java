//@@author A0131891E
package parser;

import static common.Utilities.arrayContains;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.sun.javafx.css.Combinator;
import common.Task;
import common.TasksBag;
import common.Utilities;
import ui.view.CelebiViewController;
import common.TasksBag;

public class Parser implements ParserInterface {
	
	/////////////////////////////////////////////////////////////////
	// Patterns for user command arguments matching (trim results)
	/////////////////////////////////////////////////////////////////

	// for whitespace work
	private final Pattern P_WHITESPACE;
	private static final String REGEX_WHITESPACE = 
			"\\s+";
	
	//TODO regexes for all cmds for consistency
	private static final String REGEX_MATCHING_NAME_FIELD_ALIAS = regexContaining(Aliases.FIELD_NAME);
	private static final String REGEX_MATCHING_START_FIELD_ALIAS = regexContaining(Aliases.FIELD_START_DATE);
	private static final String REGEX_MATCHING_END_FIELD_ALIAS = regexContaining(Aliases.FIELD_END_DATE);
	
	private static final String GRPNAME_NAME = "name";
	private static final String GRPNAME_START = "start";
	private static final String GRPNAME_END = "end";
	private static final String GRPNAME_UID = "uid";
	
	private static final String REGEX_VALID_NAME = "[^;]+";
	private static final String REGEX_UNVALIDATED_DATE = ".+"; // still needs to get parsed by date formatter
	private static final String REGEX_UID = "-?\\d+";
	
	private static final String REGEX_GRP_NAME = regexNamedGrp(REGEX_VALID_NAME, GRPNAME_NAME);
	private static final String REGEX_GRP_START = regexNamedGrp(REGEX_UNVALIDATED_DATE, GRPNAME_START);
	private static final String REGEX_GRP_END = regexNamedGrp(REGEX_UNVALIDATED_DATE, GRPNAME_END);
	private static final String REGEX_GRP_UID = regexNamedGrp(REGEX_UID, GRPNAME_UID);
	
	////////////////////////////////////////////////////////////////
	// ADD command parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String REGEX_NAME_DATE_DELIM = "\\s*;\\s*";
	
	// <name>
	private final Pattern P_ADD_FLT;
	private static final String REGEX_ADD_FLT = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_NAME)
		.append('$')
		.toString();
	
	// <name>; <start field identifier> <start>
	private final Pattern P_ADD_START;
	private static final String REGEX_ADD_START = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_NAME)
		.append(REGEX_NAME_DATE_DELIM)
		.append(REGEX_MATCHING_START_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_START)
		.append('$')
		.toString();

	// <name>; <end field identifier> <end>
	private final Pattern P_ADD_END;
	private static final String REGEX_ADD_END = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_NAME)
		.append(REGEX_NAME_DATE_DELIM)
		.append(REGEX_MATCHING_END_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_END)
		.append('$')
		.toString();

	// <name>; <start field identifier> <start> <end field identifier> <end>
	private final Pattern P_ADD_EVT;
	private static final String REGEX_ADD_EVT = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_NAME)
		.append(REGEX_NAME_DATE_DELIM)
		.append(REGEX_MATCHING_START_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_START)
		.append(REGEX_WHITESPACE)
		.append(REGEX_MATCHING_END_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_END)
		.append('$')
		.toString();

	////////////////////////////////////////////////////////////////
	// UPD command parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String GRPNAME_NEWVAL = "newval";
	private static final String REGEX_GRP_NEW_NAME = regexNamedGrp(REGEX_VALID_NAME, GRPNAME_NEWVAL);
	private static final String REGEX_GRP_NEW_DATE = regexNamedGrp(REGEX_UNVALIDATED_DATE, GRPNAME_NEWVAL);
	
	// <field> <uid> <newval>
	private final Pattern P_UPD_NAME;
	private static final String REGEX_UPD_NAME = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_UID)
		.append(REGEX_WHITESPACE)
		.append(REGEX_MATCHING_NAME_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_NEW_NAME)
		.append('$')
		.toString();
	private final Pattern P_UPD_START;
	private static final String REGEX_UPD_START = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_UID)
		.append(REGEX_WHITESPACE)
		.append(REGEX_MATCHING_START_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_NEW_DATE)
		.append('$')
		.toString();
	private final Pattern P_UPD_END;
	private static final String REGEX_UPD_END = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_UID)
		.append(REGEX_WHITESPACE)
		.append(REGEX_MATCHING_END_FIELD_ALIAS)
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_NEW_DATE)
		.append('$')
		.toString();

	////////////////////////////////////////////////////////////////
	// UID arg only command (DEL, MARK, UNMARK) parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String REGEX_UID_ONLY = 
		(new StringBuilder())
		.append('^')
		.append(REGEX_GRP_UID)
		.append('$')
		.toString();

	private final Pattern P_DEL;
	private static final String REGEX_DEL = REGEX_UID_ONLY;
	private final Pattern P_MARK;
	private static final String REGEX_MARK = REGEX_UID_ONLY;
	private final Pattern P_UNMARK;
	private static final String REGEX_UNMARK = REGEX_UID_ONLY;

	////////////////////////////////////////////////////////////////
	// FILTER command parsing parameters
	////////////////////////////////////////////////////////////////

	private static final String GRPNAME_FIL_LO_BOUND = "start";
	private static final String GRPNAME_FIL_HI_BOUND = "end";
	private static final String REGEX_GRP_FIL_LO_BOUND = 
			regexNamedGrp(REGEX_UNVALIDATED_DATE, GRPNAME_FIL_LO_BOUND);
	private static final String REGEX_GRP_FIL_HI_BOUND = 
			regexNamedGrp(REGEX_UNVALIDATED_DATE, GRPNAME_FIL_HI_BOUND);
	
	// before|bef <key(date)>
	private final Pattern P_FILTER_BEF;
	private static final String REGEX_FILTER_BEF = 
		(new StringBuilder())
		.append('^')
		.append(regexContaining(Aliases.FILTER_ARG_BEF))
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_FIL_HI_BOUND)
		.append('$')
		.toString();
			//"^(?:before|bef)\\s+(?<key>.+)$";

	// after|aft <key(date)>
	private final Pattern P_FILTER_AFT;
	private static final String REGEX_FILTER_AFT = 
		(new StringBuilder())
		.append('^')
		.append(regexContaining(Aliases.FILTER_ARG_AFT))
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_FIL_LO_BOUND)
		.append('$')
		.toString();
			//"^(?:after|aft)\\s+(?<key>.+)$";
	
	// between|b/w|btw|from|start <key1(date)> and|to|till|until|end <key2(date)>
	private final Pattern P_FILTER_BTW;
	private static final String REGEX_FILTER_BTW = 
		(new StringBuilder())
		.append('^')
		.append(regexContaining(Aliases.FILTER_ARG_BTW_START))
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_FIL_LO_BOUND)
		.append(REGEX_WHITESPACE)
		.append(regexContaining(Aliases.FILTER_ARG_BTW_END))
		.append(REGEX_WHITESPACE)
		.append(REGEX_GRP_FIL_HI_BOUND)
		.append('$')
		.toString();
			//"^(?:between|b/w|btw|from|start)\\s+(?<key1>.+)\\s+(?:and|to|till|until|end)\\s+(?<key2>.+)$";
	
	
	/////////////////////////////////////////////////////////////////
	// instance fields
	/////////////////////////////////////////////////////////////////
	private String userRawInput;
	private static Parser parserInstance;
	private final CelebiDateParser DATE_FORMATTER;
	/////////////////////////////////////////////////////////////////
	
	private Parser () {
		userRawInput = "no user input received";
		
		DATE_FORMATTER = new DateParser();
		
		P_WHITESPACE = Pattern.compile(REGEX_WHITESPACE);
		
		P_ADD_FLT = Pattern.compile(REGEX_ADD_FLT, CASE_INSENSITIVE);
		P_ADD_START = Pattern.compile(REGEX_ADD_START, CASE_INSENSITIVE);
		P_ADD_END = Pattern.compile(REGEX_ADD_END, CASE_INSENSITIVE);
		P_ADD_EVT = Pattern.compile(REGEX_ADD_EVT, CASE_INSENSITIVE);
		
		P_UPD_NAME = Pattern.compile(REGEX_UPD_NAME, CASE_INSENSITIVE);
		P_UPD_START = Pattern.compile(REGEX_UPD_START, CASE_INSENSITIVE);
		P_UPD_END = Pattern.compile(REGEX_UPD_END, CASE_INSENSITIVE);
		
		P_DEL = Pattern.compile(REGEX_DEL, CASE_INSENSITIVE);
		P_MARK = Pattern.compile(REGEX_MARK, CASE_INSENSITIVE);
		P_UNMARK = Pattern.compile(REGEX_UNMARK, CASE_INSENSITIVE);
		
		P_FILTER_BEF = Pattern.compile(REGEX_FILTER_BEF, CASE_INSENSITIVE);
		P_FILTER_AFT = Pattern.compile(REGEX_FILTER_AFT, CASE_INSENSITIVE);
		P_FILTER_BTW = Pattern.compile(REGEX_FILTER_BTW, CASE_INSENSITIVE);
	}
	// singleton access
	public static Parser getParser () {
		if (parserInstance == null) {
			parserInstance = new Parser();
		}
		return parserInstance;
	}
	
	@Override
	public void init () {
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	private static final String regexContaining (String[] tokens) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(?:");
		for (String tok : tokens) {
			sb.append("\\Q").append(tok).append("\\E"); // necesary escaping
			sb.append('|');
		}
		if (tokens.length > 0) { 
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(')');
		return sb.toString();
	}
	private static final String regexNamedGrp (String regex, String grpName) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(?");
		sb.append('<').append(grpName).append('>');
		sb.append(regex);
		sb.append(')');
		return sb.toString();
}
	
	@Override
	public Command parseCommand (String rawInput) {
		assert(rawInput != null);
		// TODO rework to not use String.split
		userRawInput = rawInput;
		// Splits input string at first whitespace substring, trimming trailing whitespace
		String[] cmdAndArgs = P_WHITESPACE.split(rawInput.trim(), 2);
		// if no command args, set the args half of the array to empty string
		if (cmdAndArgs.length != 2) { 
			cmdAndArgs = new String[]{cmdAndArgs[0], ""};
		}
		Command.Type cmdType = getCmdType(cmdAndArgs[0]);
		return passArgs(cmdType, cmdAndArgs[1]);
	}
	
	private Command.Type getCmdType (String token) {
		assert(token != null);
		token = token.toLowerCase();
		
		if (arrayContains(Aliases.CMD_ADD, token)) {
			return Command.Type.ADD;
		}
		if (arrayContains(Aliases.CMD_DEL, token)) {
			return Command.Type.DELETE;
		}
		if (arrayContains(Aliases.CMD_UPD, token)) {
			return Command.Type.UPDATE;
		}
		if (arrayContains(Aliases.CMD_QUIT, token)) {
			return Command.Type.QUIT;	
		}
		if (arrayContains(Aliases.CMD_MARK, token)) {
			return Command.Type.MARK;
		}
		if (arrayContains(Aliases.CMD_UNMARK, token)) {
			return Command.Type.UNMARK;
		}
		if (arrayContains(Aliases.CMD_UNDO, token)) {
			return Command.Type.UNDO;
		}
		if (arrayContains(Aliases.CMD_REDO, token)) {
			return Command.Type.REDO;			
		}
		if (arrayContains(Aliases.CMD_SHOW, token)) {
			return Command.Type.SHOW;			
		}
		if (arrayContains(Aliases.CMD_SEARCH, token)) {
			return Command.Type.SEARCH;			
		}
		if (arrayContains(Aliases.CMD_FILTER, token)) {
			return Command.Type.FILTER_DATE;
		}
		if (arrayContains(Aliases.CMD_MOVE, token)) {
			return Command.Type.MOVE;
		}
		if (arrayContains(Aliases.CMD_HELP, token)) {
			return Command.Type.HELP;
		}
		if (arrayContains(Aliases.CMD_THEME, token)) {
			return Command.Type.THEME;
		}
		switch (token) {
		case "clr" :
		case "cls" :
		case "clear" :
		case "reset" :
			return Command.Type.CLEAR_FILTERS;
			
		}
		return Command.Type.INVALID;
	}

	private Command passArgs (Command.Type type, String args) {
		assert(type != null && args != null);
		args = args.trim();
		switch (type) {
			case ADD :
				return parseAdd(args);
				//break;
			case DELETE : 
				return parseDel(args);
				//break;
			case UPDATE : 
				return parseUpd(args);
				//break;
			case QUIT :
				return parseQuit(args);
				//break;
			case INVALID :
				return makeInvalid();
				//break;
			case SHOW :
				return parseShow(args);
				//break;
			case REDO :
				return parseRedo(args);
				//break;
			case UNDO :
				return parseUndo(args);
				//break;
			case MARK :
				return parseMark(args);
				//break;
			case UNMARK :
				return parseUnmark(args);
				//break;
			case SEARCH :
				return parseSearch(args);
				//break;
			case FILTER_DATE :
				return parseFilterDate(args);
				//break;
			case MOVE:
				return parseMove(args);
				//break;
			case HELP:
				return parseHelp(args);
				//break;
			case CLEAR_FILTERS:
				return parseClear(args);
			case THEME :
				return parseTheme(args);
				//break;
			default :
				break;
			}
		assert(false); // should never happen
		return null;
	}

	private Command parseClear (String args) {
		assert(args != null);
		return new Command(Command.Type.CLEAR_FILTERS, userRawInput);
	}
	private Command parseAdd (String args) {
		assert(args != null);
		args = args.trim();
		
		Matcher m;
		Date start, end;
		String name;
		
		m = P_ADD_FLT.matcher(args);
		if (m.matches()) {
			name = m.group(GRPNAME_NAME).trim();
			start = null;
			end = null;
			return makeAdd(name, start, end);
		}
		try {
			// must put more restrictive regexes in front
			// to prevent looser regexes accidentally capturing
			// string that should have matched the former!!!!
			m = P_ADD_EVT.matcher(args);
			if (m.matches()) {
				name = m.group(GRPNAME_NAME).trim();
				start = parseDate(m.group(GRPNAME_START).trim(), true);
				end = parseDate(m.group(GRPNAME_END).trim(), false);
				return makeAdd(name, start, end);
			}
			
			m = P_ADD_START.matcher(args);
			if (m.matches()) {
				name = m.group(GRPNAME_NAME).trim();
				start = parseDate(m.group(GRPNAME_START).trim(), true);
				end = null;
				return makeAdd(name, start, end);
			}
			
			m = P_ADD_END.matcher(args);
			if (m.matches()) {
				name = m.group(GRPNAME_NAME).trim();
				start = null;
				end = parseDate(m.group(GRPNAME_END).trim(), false);
				return makeAdd(name, start, end);
			}			
		} catch (ParseException pe) {
			//System.out.println(pe);
		}
		return makeInvalid();
	}
	private Command parseDel (String args) {
		assert(args != null);
		args = args.trim();
		Matcher m = P_DEL.matcher(args);
		if (m.matches()) {
			try {
				int uid = Integer.parseInt(m.group(GRPNAME_UID));
				return makeDelete(uid);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return makeInvalid();
	}
	private Command parseUpd (String args) {
		assert(args != null);
		
		int uid;
		Matcher m;
		
		m = P_UPD_NAME.matcher(args);
		if (m.matches()) {
			final String newName = m.group(GRPNAME_NEWVAL).trim();
			uid = Integer.parseInt(m.group(GRPNAME_UID));
			return makeUpdateName(uid, newName);
		}

		try {
			m = P_UPD_START.matcher(args);
			if (m.matches()) {
				final Date newStart = parseDate(m.group(GRPNAME_NEWVAL), true);
				uid = Integer.parseInt(m.group(GRPNAME_UID));
				return makeUpdateStart(uid, newStart);
			}
	
			m = P_UPD_END.matcher(args);
			if (m.matches()) {
				final Date newEnd = parseDate(m.group(GRPNAME_NEWVAL), false);
				uid = Integer.parseInt(m.group(GRPNAME_UID));
				return makeUpdateEnd(uid, newEnd);
			}
		} catch (ParseException pe) {
			//System.out.println(pe);
		}
		return makeInvalid();
	}
	private Command parseQuit (String args) {
		assert(args != null);
		return makeQuit();
	}
	private Command parseShow (String args) {
		assert(args != null);
		args = args.trim().toLowerCase();
		if (arrayContains(Aliases.VIEW_DEFAULT, args)) {
			return makeShow(TasksBag.ViewType.TODAY);
		}
		if (arrayContains(Aliases.VIEW_INCOMPLETE, args)) {
			return makeShow(TasksBag.ViewType.INCOMPLETE_TASKS);
		}
		if (arrayContains(Aliases.VIEW_COMPLETE, args)) {
			return makeShow(TasksBag.ViewType.COMPLETE_TASKS);
		}
		return makeInvalid();
	}
	private Command parseRedo (String args) {
		assert(args != null);
		return makeRedo();
	}
	private Command parseUndo (String args) {
		assert(args != null);
		return makeUndo();
	}
	private Command parseMark (String args) {
		assert(args != null);
		args = args.trim();
		final Matcher m = P_MARK.matcher(args);
		if (m.matches()) {
			final int uid = Integer.parseInt(m.group(GRPNAME_UID));
			return makeMark(uid);
		}
		return makeInvalid();
	}
	private Command parseUnmark (String args) {
		assert(args != null);
		args = args.trim();
		final Matcher m = P_UNMARK.matcher(args);
		if (m.matches()) {
			final int uid = Integer.parseInt(m.group(GRPNAME_UID));
			return makeUnmark(uid);
		}
		return makeInvalid();
	}
	private Command parseSearch (String args) {
		assert(args != null);
		args = args.trim();
		if (args.length() != 0) {
			return makeSearch(args);
		}
		return makeInvalid();
	}
	private Command parseFilterDate (String args) {
		assert(args != null);
		args = args.trim();
		
		Matcher m;
		Date min, max;

		m = P_FILTER_BTW.matcher(args);
		if (m.matches()) {
			try {
				min = parseDate(m.group(GRPNAME_FIL_LO_BOUND), true);
				max = parseDate(m.group(GRPNAME_FIL_HI_BOUND), false);
				return makeFilterDate(min, max);
			} catch (ParseException pe) {
				;
			}
		}
		
		m = P_FILTER_BEF.matcher(args);
		if (m.matches()) {
			try {
				max = parseDate(m.group(GRPNAME_FIL_HI_BOUND), false);
				min = Utilities.absBeginningTime();//new Date(0);
				return makeFilterDate(min, max);
			} catch (ParseException pe) {
				;
			}
		}
		
		m = P_FILTER_AFT.matcher(args);
		if (m.matches()) {
			try {
				min = parseDate(m.group(GRPNAME_FIL_LO_BOUND), true);
				max = Utilities.absEndingTime();//new Date(Long.MAX_VALUE);
				return makeFilterDate(min, max);
			} catch (ParseException pe) {
				;
			}
		}
		
		return makeInvalid();
	}
	private Command parseMove (String args) {
		assert(args != null);
		args = args.trim();
		if (args.length() != 0) {
			try {
				Path p = parsePath(parseText(args));
				return makeMove(p);
			} catch (ParseException pe) {
				;
			}
		}
		return makeInvalid();
	}
	private Command parseHelp (String args) {
		assert(args != null);
		args = args.trim();
		if (args.length() == 0) { // no args for help cmd
			return makeHelp(null);
		}
		Command.Type helpTarget = getCmdType(args);
		// help args can be parsed into a cmd type
		if (helpTarget != Command.Type.INVALID) {
			return makeHelp(helpTarget);
		}
		return makeInvalid();
	}
	private Command parseTheme (String args) {
		Command cmd = new Command(Command.Type.THEME, userRawInput);
		if (args.equals("day")) {

			cmd.setTheme(CelebiViewController.Skin.DAY);
		} else if (args.equals("night")) {

			cmd.setTheme(CelebiViewController.Skin.NIGHT);
		}
		return cmd;
	}
	
/*	Task.DataType parseFieldKey (String token) throws ParseException {
		assert(token != null);
		token = token.toLowerCase();
		if (arrayContains(Aliases.FIELD_NAME, token)) {
			return Task.DataType.NAME;
		}
		if (arrayContains(Aliases.FIELD_START_DATE, token)) {
			return Task.DataType.DATE_START;
		}
		if (arrayContains(Aliases.FIELD_END_DATE, token)) {
			return Task.DataType.DATE_END;
		}
		throw new ParseException("", -1);
	}*/
	/*Object parseFieldValue (Task.DataType key, String valStr) throws ParseException, IllegalArgumentException {
		assert(key != null && valStr != null);
		switch (key) {
			case NAME : 
				return parseText(valStr);
			case DATE_START :	// Fallthrough
			case DATE_END : 
				return parseDate(valStr);
			default :
				throw new IllegalArgumentException("key must be amongst : NAME, DATE_START, DATE_END");
		}
	}*/
	String parseText (String token) {
		assert(token != null);
		return token.trim();
	}
	Date parseDate (String token, boolean isStart) throws ParseException {
		assert(token != null);
		return DATE_FORMATTER.parseDate(token, isStart);
	}
	Path parsePath (String token) throws ParseException {
		assert(token != null);
		return Paths.get(token.trim());
	}
	
	@Override
	public Command makeAdd (String name, Date start, Date end) {
		Command cmd = new Command(Command.Type.ADD, userRawInput);
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setText(name);
		return cmd;
	}
	@Override
	public Command makeUpdateName (int taskUID, String newName) {
		Command cmd = new Command(Command.Type.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.NAME);
		cmd.setTaskUID(taskUID);
		cmd.setText(newName);
		return cmd;	
	}
	@Override
	public Command makeUpdateStart (int taskUID, Date newDate) {
		Command cmd = new Command(Command.Type.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.DATE_START);
		cmd.setTaskUID(taskUID);
		cmd.setStart(newDate);
		return cmd;	
	}
	@Override
	public Command makeUpdateEnd (int taskUID, Date newDate) {
		Command cmd = new Command(Command.Type.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.DATE_END);
		cmd.setTaskUID(taskUID);
		cmd.setEnd(newDate);
		return cmd;	
	}
	@Override
	public Command makeDelete (int taskUID) {
		Command cmd = new Command(Command.Type.DELETE, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	@Override
	public Command makeQuit () {
		Command cmd = new Command(Command.Type.QUIT, userRawInput);
		return cmd;
	}
	@Override
	public Command makeInvalid () {
		Command cmd = new Command(Command.Type.INVALID, userRawInput);
		return cmd;
	}
	@Override
	public Command makeShow (TasksBag.ViewType view) {
		Command cmd = new Command(Command.Type.SHOW, userRawInput);
		cmd.setViewType(view);
		return cmd;		
	}
	@Override
	public Command makeRedo () {
		Command cmd = new Command(Command.Type.REDO, userRawInput);
		return cmd;
	}
	@Override
	public Command makeUndo () {
		Command cmd = new Command(Command.Type.UNDO, userRawInput);
		return cmd;
		
	}
	@Override
	public Command makeMark (int taskUID) {
		Command cmd = new Command(Command.Type.MARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	@Override
	public Command makeUnmark (int taskUID) {
		Command cmd = new Command(Command.Type.UNMARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	@Override
	public Command makeSearch (String keyword) {
		Command cmd = new Command(Command.Type.SEARCH, userRawInput);
		cmd.setText(keyword);
		return cmd;
	}
	@Override
	public Command makeFilterDate (Date rangeStart, Date rangeEnd) {
		Command cmd = new Command(Command.Type.FILTER_DATE, userRawInput);
		cmd.setStart(rangeStart);
		cmd.setEnd(rangeEnd);
		return cmd;
	}
	@Override
	public Command makeMove (Path newPath) {
		Command cmd = new Command(Command.Type.MOVE, userRawInput);
		cmd.setPath(newPath);
		return cmd;
	}
	@Override
	public Command makeHelp (Command.Type helpTarget) {
		Command cmd = new Command(Command.Type.HELP, userRawInput);
		cmd.setHelpCmdType(helpTarget);
		return cmd;
	}
	
	public static void printCmd (Command c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getText());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
	}
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		Parser p = new Parser();
		while (true) {
//			System.out.println(p.P_ADD_FLT.pattern());
//			System.out.println(p.P_ADD_FLT.matcher(sc.nextLine()).matches());
			printCmd(p.parseCommand(sc.nextLine()));
		}
	}

}

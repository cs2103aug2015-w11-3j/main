//@@author A0131891E
package parser;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;

//import com.sun.javafx.css.Combinator;
import common.Task;
import common.TasksBag;
import common.Utilities;
import static ui.view.CelebiViewController.Skin;

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
	private static final String REGEX_UID = "-?\\d+"; // supports negative numbers for logic to throw exception
	
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
	private static final String REGEX_ADD_FLT = concatArgs(
		'^',
		REGEX_GRP_NAME,
		'$'
	);
		
	
	// <name>; <start field identifier> <start>
	private final Pattern P_ADD_START;
	private static final String REGEX_ADD_START = concatArgs(
		'^',
		REGEX_GRP_NAME,
		REGEX_NAME_DATE_DELIM,
		REGEX_MATCHING_START_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_START,
		'$'
	);

	// <name>; <end field identifier> <end>
	private final Pattern P_ADD_END;
	private static final String REGEX_ADD_END = concatArgs(
		'^',
		REGEX_GRP_NAME,
		REGEX_NAME_DATE_DELIM,
		REGEX_MATCHING_END_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_END,
		'$'
	);

	// <name>; <start field identifier> <start> <end field identifier> <end>
	private final Pattern P_ADD_EVT;
	private static final String REGEX_ADD_EVT = concatArgs(
		'^',
		REGEX_GRP_NAME,
		REGEX_NAME_DATE_DELIM,
		REGEX_MATCHING_START_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_START,
		REGEX_WHITESPACE,
		REGEX_MATCHING_END_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_END,
		'$'
	);

	////////////////////////////////////////////////////////////////
	// UPD command parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String GRPNAME_NEWVAL = "newval";
	private static final String REGEX_GRP_NEW_NAME = regexNamedGrp(REGEX_VALID_NAME, GRPNAME_NEWVAL);
	private static final String REGEX_GRP_NEW_DATE = regexNamedGrp(REGEX_UNVALIDATED_DATE, GRPNAME_NEWVAL);
	
	// <field> <uid> <newval>
	private final Pattern P_UPD_NAME;
	private static final String REGEX_UPD_NAME = concatArgs(
		'^',
		REGEX_GRP_UID,
		REGEX_WHITESPACE,
		REGEX_MATCHING_NAME_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_NEW_NAME,
		'$'
	);
	private final Pattern P_UPD_START;
	private static final String REGEX_UPD_START = concatArgs(
		'^',
		REGEX_GRP_UID,
		REGEX_WHITESPACE,
		REGEX_MATCHING_START_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_NEW_DATE,
		'$'
	);
	private final Pattern P_UPD_END;
	private static final String REGEX_UPD_END = concatArgs(
		'^',
		REGEX_GRP_UID,
		REGEX_WHITESPACE,
		REGEX_MATCHING_END_FIELD_ALIAS,
		REGEX_WHITESPACE,
		REGEX_GRP_NEW_DATE,
		'$'
	);

	////////////////////////////////////////////////////////////////
	// UID arg only command (DEL, MARK, UNMARK, parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String REGEX_UID_ONLY = concatArgs(
		'^',
		REGEX_GRP_UID,
		'$'
	);

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
	
	// before|bef <key(date,>
	private final Pattern P_FILTER_BEF;
	private static final String REGEX_FILTER_BEF = concatArgs(
		'^',
		regexContaining(Aliases.FILTER_ARG_BEF),
		REGEX_WHITESPACE,
		REGEX_GRP_FIL_HI_BOUND,
		'$'
	);

	// after|aft <key(date,>
	private final Pattern P_FILTER_AFT;
	private static final String REGEX_FILTER_AFT = concatArgs(
		'^',
		regexContaining(Aliases.FILTER_ARG_AFT),
		REGEX_WHITESPACE,
		REGEX_GRP_FIL_LO_BOUND,
		'$'
	);
	
	// between|b/w|btw|from|start <key1(date,> and|to|till|until|end <key2(date,>
	private final Pattern P_FILTER_BTW;
	private static final String REGEX_FILTER_BTW = concatArgs(
		'^',
		regexContaining(Aliases.FILTER_ARG_BTW_START),
		REGEX_WHITESPACE,
		REGEX_GRP_FIL_LO_BOUND,
		REGEX_WHITESPACE,
		regexContaining(Aliases.FILTER_ARG_BTW_END),
		REGEX_WHITESPACE,
		REGEX_GRP_FIL_HI_BOUND,
		'$'
	);
	
	
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

	private static final String regexContaining (Collection<String> tokens) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(?:");
		for (String tok : tokens) {
			sb.append("\\Q").append(tok).append("\\E"); // necesary escaping
			sb.append('|');
		}
		if (tokens.size() > 0) { 
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
	private static final String concatArgs (Object... args) {
		final StringBuilder sb = new StringBuilder();
		for (Object item : args) {
			sb.append(item);
		}
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
		token = cleanText(token);
		// CUD
		if (Aliases.CMD_ADD.contains(token)) {
			return Command.Type.ADD;
		}
		if (Aliases.CMD_DEL.contains(token)) {
			return Command.Type.DELETE;
		}
		if (Aliases.CMD_UPD.contains(token)) {
			return Command.Type.UPDATE;
		}
		// completing tasks
		if (Aliases.CMD_MARK.contains(token)) {
			return Command.Type.MARK;
		}
		if (Aliases.CMD_UNMARK.contains(token)) {
			return Command.Type.UNMARK;
		}
		// undo/redo
		if (Aliases.CMD_UNDO.contains(token)) {
			return Command.Type.UNDO;
		}
		if (Aliases.CMD_REDO.contains(token)) {
			return Command.Type.REDO;			
		}
		// display
		if (Aliases.CMD_SHOW.contains(token)) {
			return Command.Type.SHOW;			
		}
		// filter text/date
		if (Aliases.CMD_SEARCH.contains(token)) {
			return Command.Type.SEARCH;			
		}
		if (Aliases.CMD_FILTER.contains(token)) {
			return Command.Type.FILTER_DATE;
		}
		if (Aliases.CMD_CLEAR.contains(token)) {
			return Command.Type.CLEAR_FILTERS;
		}
		// misc
		if (Aliases.CMD_MOVE.contains(token)) {
			return Command.Type.MOVE;
		}
		if (Aliases.CMD_HELP.contains(token)) {
			return Command.Type.HELP;
		}
		if (Aliases.CMD_QUIT.contains(token)) {
			return Command.Type.QUIT;	
		}
		if (Aliases.CMD_THEME.contains(token)) {
			return Command.Type.THEME;
		}
		// user is an idiot?
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
		final String newName;
		final Date newStart;
		final Date newEnd;
		String newValue;
		
		m = P_UPD_NAME.matcher(args);
		if (m.matches()) {
			uid = Integer.parseInt(m.group(GRPNAME_UID));
			newValue = m.group(GRPNAME_NEWVAL);
			newName = newValue.trim();
			return makeUpdateName(uid, newName);
		}

		try {
			m = P_UPD_START.matcher(args);
			if (m.matches()) {
				uid = Integer.parseInt(m.group(GRPNAME_UID));
				newValue = m.group(GRPNAME_NEWVAL);
				newStart = parseUpdDate(newValue);
				return makeUpdateStart(uid, newStart);
			}
	
			m = P_UPD_END.matcher(args);
			if (m.matches()) {
				uid = Integer.parseInt(m.group(GRPNAME_UID));
				newValue = m.group(GRPNAME_NEWVAL);
				newEnd = parseUpdDate(newValue);
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
		args = cleanText(args);
		if (Aliases.VIEW_DEFAULT.contains(args)) {
			return makeShow(TasksBag.ViewType.TODAY);
		}
		if (Aliases.VIEW_INCOMPLETE.contains(args)) {
			return makeShow(TasksBag.ViewType.INCOMPLETE_TASKS);
		}
		if (Aliases.VIEW_COMPLETE.contains(args)) {
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
	private Command parseClear (String args) {
		assert(args != null);
		return makeClear();
	}
	private Command parseMove (String args) {
		assert(args != null);
		args = args.trim();
		if (args.length() != 0) {
			try {
				Path p = parsePath(args.trim());
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
		assert(args != null);
		args = cleanText(args);
		if (Aliases.THEME_DAY.contains(args)) {
			return makeTheme(Skin.DAY);
		}
		if (Aliases.THEME_NIGHT.contains(args)) {
			return makeTheme(Skin.NIGHT);
		}
		return makeInvalid();
	}
	
	// cleans a string by trimming trailing whitespace and shifting alpha to lowercase
	String cleanText (String token) {
		assert(token != null);
		return token.trim().toLowerCase();
	}
	Date parseDate (String token, boolean isStart) throws ParseException {
		assert(token != null);
		return DATE_FORMATTER.parseDate(token, isStart);
	}
	Path parsePath (String token) throws ParseException {
		assert(token != null);
		return Paths.get(token.trim());
	}
	// Used only in parseUpdate: allows special datestrings
	// to signify removal of a date field from the task.
	// Allows conversion from event->deadline/startonly->float
	private Date parseUpdDate (String dateStr) throws ParseException {
		if (Aliases.DATE_CLEAR.contains(cleanText(dateStr))) {
			return null;
		}
		return parseDate(dateStr, true);
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
	public Command makeClear () {
		Command cmd = new Command(Command.Type.CLEAR_FILTERS, userRawInput);
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
	@Override
	public Command makeQuit () {
		Command cmd = new Command(Command.Type.QUIT, userRawInput);
		return cmd;
	}
	@Override
	public Command makeTheme (Skin theme) {
            Command cmd = new Command(Command.Type.THEME, userRawInput);
	    cmd.setTheme(theme);
	    return cmd;
	}
	@Override
	public Command makeInvalid () {
		Command cmd = new Command(Command.Type.INVALID, userRawInput);
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
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		Parser p = new Parser();
		while (true) {
//			System.out.println(p.P_ADD_FLT.pattern());
//			System.out.println(p.P_ADD_FLT.matcher(sc.nextLine()).matches());
			printCmd(p.parseCommand(sc.nextLine()));
		}
	}
}

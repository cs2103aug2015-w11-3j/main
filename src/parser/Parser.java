//@@author A0131891E
package parser;

//import com.sun.javafx.css.Combinator;
import common.Task;
import static java.util.regex.Pattern.*;
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import static common.Utilities.*;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.*;
import java.util.Scanner;

public class Parser implements ParserInterface {
	
	/////////////////////////////////////////////////////////////////
	// Human-readable strings for command formats (used by logic.HelpAction)
	/////////////////////////////////////////////////////////////////
	public static final String HELP_LIST_ALL_CMDS = 
			"| help, quit | add, del, edit | mark, unmark | show | undo, redo | search, filter | move |";
	
	public static final String HELP_FORMAT_ADD = 
			"\"add <name>\" OR \"add <name>; by <due date>\" OR \"add <name>; from <start date> to <end date>\"";
	public static final String HELP_FORMAT_DEL = 
			"\"del <task ID number>\"";
	public static final String HELP_FORMAT_UPD = 
			"\"edit <task ID number> name/start/end <new value>\"";
	public static final String HELP_FORMAT_MARK = 
			"\"mark <task ID number>\"";
	public static final String HELP_FORMAT_UNMARK = 
			"\"unmark <task ID number>\"";
	public static final String HELP_FORMAT_SHOW = 
			"\"\"";
	public static final String HELP_FORMAT_UNDO = 
			"\"undo\" OR \"un\" OR \"u\"";
	public static final String HELP_FORMAT_REDO = 
			"\"redo\" OR \"re\"";
	public static final String HELP_FORMAT_SEARCH = 
			"\"search <words to find in names>\" OR \"s <words to find in names\"";
	public static final String HELP_FORMAT_FILTER = 
			"\"filter before/after <reference date>\" OR \"filter from \"";
	public static final String HELP_FORMAT_MOVE = 
			"\"move <new save file path>\"";
	public static final String HELP_FORMAT_QUIT = 
			"\"quit\" OR \"q\"";
	public static final String HELP_FORMAT_HELP = 
			"\"help\" OR \"help <command name (from list of cmds from \"help\")>\"";
	
	/////////////////////////////////////////////////////////////////
	// Patterns for user command arguments matching (trim results)
	/////////////////////////////////////////////////////////////////

	// for whitespace work
	private final Pattern P_WHITESPACE;
	private static final String REGEX_WHITESPACE = 
			"\\s+";
	
	// <name>
	private final Pattern P_ADD_FLT;
	private static final String REGEX_ADD_FLT = 
			"^(?<name>[^;]+)$";
	// <name>; by|due <end>
	private final Pattern P_ADD_DL;
	private static final String REGEX_ADD_DL = 
			"^(?<name>[^;]+);\\s+(?:by|due|at)\\s(?<end>.+)$";
	// <name>; from|start <start> end|to|till|until|due <end>
	private final Pattern P_ADD_EVT;
	private static final String REGEX_ADD_EVT = 
			"^(?<name>[^;]+);\\s+(?:from|start)\\s+(?<start>.+)\\s+(?:till|to|until|end|due)\\s+(?<end>.+)$";
	
	// <uid>
	private final Pattern P_DEL;
	private static final String REGEX_DEL = 
			"^(?<uid>\\d++)$";
	
	// <field> <uid> <newval>
	private final Pattern P_UPD;
	private static final String REGEX_UPD = 
			"^(?<uid>\\d+)\\s(?<field>\\w+)\\s(?<newval>.+)$";

	// before|bef <key(date)>
	private final Pattern P_FILTER_BEF;
	private static final String REGEX_FILTER_BEF = 
			"^(?:before|bef)\\s+(?<key>.+)$";

	// after|aft <key(date)>
	private final Pattern P_FILTER_AFT;
	private static final String REGEX_FILTER_AFT = 
			"^(?:after|aft)\\s+(?<key>.+)$";
	
	// between|b/w|btw|from|start <key1(date)> and|to|till|until|end <key2(date)>
	private final Pattern P_FILTER_BTW;
	private static final String REGEX_FILTER_BTW = 
			"^(?:between|b/w|btw|from|start)\\s+(?<key1>.+)\\s+(?:and|to|till|until|end)\\s+(?<key2>.+)$";
	
	/////////////////////////////////////////////////////////////////
	// instance fields
	/////////////////////////////////////////////////////////////////
	private String userRawInput;
	private static Parser parserInstance;
	private final CelebiDateFormatter DATE_FORMATTER;
	/////////////////////////////////////////////////////////////////
	
	private Parser () {
		userRawInput = "no user input received";
		
		DATE_FORMATTER = new DateFormatter();
		
		P_WHITESPACE = Pattern.compile(REGEX_WHITESPACE);
		P_ADD_FLT = Pattern.compile(REGEX_ADD_FLT, CASE_INSENSITIVE);
		P_ADD_DL = Pattern.compile(REGEX_ADD_DL, CASE_INSENSITIVE);
		P_ADD_EVT = Pattern.compile(REGEX_ADD_EVT, CASE_INSENSITIVE);
		P_DEL = Pattern.compile(REGEX_DEL);
		P_UPD = Pattern.compile(REGEX_UPD);
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

	@Override
	public Command parseCommand (String rawInput) {
		assert(rawInput != null);
		
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
			return Command.Type.show_temp;			
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
		return Command.Type.INVALID;
	}

	private Command passArgs (Command.Type type, String args) {
		assert(type != null && args != null);
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
			case show_temp :
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
			default :
				break;
			}
		assert(false); // should never happen
		return null;
	}

	private Command parseAdd (String args) {
		assert(args != null);
		Matcher m;
		Date start, end;
		String name;
		Pattern[] addPs = { 
					P_ADD_EVT,
					P_ADD_DL,
					P_ADD_FLT
				};
		
		for (Pattern p : addPs) {
			m = p.matcher(args);
			name = null;
			start = end = null;
			if (m.matches()) {
				try {
					name = parseText(m.group("name"));
					end = parseDate(m.group("end")); // throws IAE if FLT
					start = parseDate(m.group("start")); // throws IAE if FLT/DL
				} catch (IllegalArgumentException ie) {
					//System.out.println(ie);
					; // nonexistent named capturing group for FLT,DL
				} catch (Exception pe) {
					System.out.println(pe);
					break; // unparsable tokens, invalid command.
				}
				return makeAdd(name, start, end);
			}
		}
		return makeInvalid();
	}
	private Command parseDel (String args) {
		assert(args != null);
		Matcher m = P_DEL.matcher(args);
		if (m.matches()) {
			try {
				int uid = Integer.parseInt(m.group("uid"));
				return makeDelete(uid);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return makeInvalid();
	}
	private Command parseUpd (String args) {
		assert(args != null);
		Matcher m = P_UPD.matcher(args);
		if (m.matches()) {
			try {
				int uid = Integer.parseInt(m.group("uid"));
				Task.DataType field = parseFieldKey(m.group("field"));
				Object newval = parseFieldValue(field, m.group("newval"));
				return makeUpdate(uid, field, newval);
			} catch (Exception e) {
				System.out.println(e); // unparsable tokens
			}
		}
		return makeInvalid();
	}
	private Command parseQuit (String args) {
		assert(args != null);
		return makeQuit();
	}
	private Command parseShow (String args) {
		assert(args != null);
		args = args.toLowerCase();
		if (arrayContains(Aliases.VIEW_DEFAULT, args)) {
			return makeShow(Command.Type.SHOW_DEFAULT);
		}
		if (arrayContains(Aliases.VIEW_INCOMPLETE, args)) {
			return makeShow(Command.Type.SHOW_INCOMPLETE);
		}
		if (arrayContains(Aliases.VIEW_COMPLETE, args)) {
			return makeShow(Command.Type.SHOW_COMPLETE);
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
		try {
			return makeMark(Integer.parseInt(args));
		} catch (NumberFormatException e) {
			return makeInvalid();
		}
	}
	private Command parseUnmark (String args) {
		assert(args != null);
		try {
			return makeUnmark(Integer.parseInt(args));
		} catch (NumberFormatException e) {
			return makeInvalid();
		}
	}
	private Command parseSearch (String args) {
		assert(args != null);
		if (args.length() != 0) {
			return makeSearch(args);
		}
		return makeInvalid();
	}
	private Command parseFilterDate (String args) {
		assert(args != null);
		Matcher m;
		Date start, end;

		if ((m = P_FILTER_BEF.matcher(args)).matches()) {
			try {
				end = parseDate(m.group("key"));
				start = new Date(0);
				return makeFilterDate(start, end);
			} catch (ParseException pe) {
				;
			}
		} else if ((m = P_FILTER_AFT.matcher(args)).matches()) {
			try {
				start = parseDate(m.group("key"));
				end = new Date(Long.MAX_VALUE);
				return makeFilterDate(start, end);
			} catch (ParseException pe) {
				;
			}
		} else if ((m = P_FILTER_BTW.matcher(args)).matches()) {
			try {
				start = parseDate(m.group("key1"));
				end = parseDate(m.group("key2"));
				if (start.after(end)) {
					Date temp = end;
					end = start;
					start = temp;
				}
				return makeFilterDate(start, end);
			} catch (ParseException pe) {
				;
			}
		}
		
		return makeInvalid();
	}
	private Command parseMove (String args) {
		assert(args != null);
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
	
	Task.DataType parseFieldKey (String token) throws ParseException {
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
	}
	Object parseFieldValue (Task.DataType key, String valStr) throws ParseException, IllegalArgumentException {
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
	}
	String parseText (String token) {
		assert(token != null);
		return token.trim();
	}
	Date parseDate (String token) throws ParseException {
		assert(token != null);
		return DATE_FORMATTER.parseDate(token);
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
	public Command makeUpdate (int taskUID, Task.DataType fieldType, Object newValue) throws IllegalArgumentException {
		Command cmd = new Command(Command.Type.UPDATE, userRawInput);
		cmd.setTaskField(fieldType);
		cmd.setTaskUID(taskUID);
		switch (fieldType) {
		case NAME : 
			cmd.setText((String) newValue);
			break;
		case DATE_START :
			cmd.setStart((Date) newValue);
			break;
		case DATE_END :
			cmd.setEnd((Date) newValue);
			break;
		default :
			throw new IllegalArgumentException("Allowed fields are name, start, and end dates.");
			// break;
		}
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
	public Command makeShow (Command.Type showtype) {
		Command cmd = new Command(showtype, userRawInput);
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
			printCmd(p.parseCommand(sc.nextLine()));
		}
	}

}

package parser;

//import com.sun.javafx.css.Combinator;
import common.Task;
import static java.util.regex.Pattern.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.*;
import java.util.Scanner;

public class Parser implements ParserInterface {

	public static final String[] TOKENS_ADD = {
			"a",
			"add",
			"new",
			"create"
	};
	public static final String[] TOKENS_DEL = {
			"d",
			"del",
			"delete",
			"rm",
			"remove"
	};
	public static final String[] TOKENS_UPD = {
			"u",	
			"upd",
			"update",
			"set",
			"edit"
	};
	public static final String[] TOKENS_QUIT = {
			"q",	
			"quit",
			"exit"
	};
	public static final String[] TOKENS_MARK = {
			"mark",
			"complete"
	};
	public static final String[] TOKENS_UNMARK = {
			"unmark",
			"reopen" 
	};
	public static final String[] TOKENS_UNDO = {
			"undo",
			"un" 
	};
	public static final String[] TOKENS_REDO = {
			"redo",
			"re" 
	};
	public static final String[] TOKENS_SHOW = {
			"show",
			"view"
	};
	public static final String[] TOKENS_SEARCH = {
			"search",
			"find"
	};
	public static final String[] TOKENS_FILTER = {
			"fil",
			"filter"
	};
	public static final String[] TOKENS_CHANGE_SAVE_LOC = {
			"mv",
			"move"
	};
	public static final String[][] TOKENS = {
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
			TOKENS_CHANGE_SAVE_LOC
	};
	
	private static Parser parserInstance;
	private final CelebiDateFormatter DATE_FORMATTER;
	
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
			"^(?<name>[^;]+);\\s+(?:by|due)\\s(?<end>.+)$";
	
	// <name>; from|start <start> end|to|till|until|due <end>
	private final Pattern P_ADD_EVT;
	private static final String REGEX_ADD_EVT = 
			"^(?<name>[^;]+);\\s+(?:from|start)\\s(?<start>.+)\\s(?:till|to|until|end|due)\\s(?<end>.+)$";
	
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
		String[] cmdAndArgs = P_WHITESPACE.split(rawInput.trim(), 2);
		if (cmdAndArgs.length != 2) {
			String[] temp = {cmdAndArgs[0], ""};
			cmdAndArgs = temp;
		}
		Command.Type cmdType = getCmdType(cmdAndArgs[0]);
		return passArgs(cmdType, cmdAndArgs[1]);
	}
	
	private Command.Type getCmdType (String token) {
		assert(token != null);
		token = token.toLowerCase();
		
		if (arrayContains(TOKENS_ADD, token)) {
			return Command.Type.ADD;
		}
		if (arrayContains(TOKENS_DEL, token)) {
			return Command.Type.DELETE;
		}
		if (arrayContains(TOKENS_UPD, token)) {
			return Command.Type.UPDATE;
		}
		if (arrayContains(TOKENS_QUIT, token)) {
			return Command.Type.QUIT;	
		}
		if (arrayContains(TOKENS_MARK, token)) {
			return Command.Type.MARK;
		}
		if (arrayContains(TOKENS_UNMARK, token)) {
			return Command.Type.UNMARK;
		}
		if (arrayContains(TOKENS_UNDO, token)) {
			return Command.Type.UNDO;
		}
		if (arrayContains(TOKENS_REDO, token)) {
			return Command.Type.REDO;			
		}
		if (arrayContains(TOKENS_SHOW, token)) {
			return Command.Type.show_temp;			
		}
		if (arrayContains(TOKENS_SEARCH, token)) {
			return Command.Type.SEARCH;			
		}
		if (arrayContains(TOKENS_FILTER, token)) {
			return Command.Type.FILTER_DATE;
		}
		if (arrayContains(TOKENS_CHANGE_SAVE_LOC, token)) {
			return Command.Type.CHANGE_SAVE_LOC;
		}
		
		return Command.Type.INVALID;
	}

	private Command passArgs (Command.Type type, String args) {
		assert(type != null && args != null);
		switch (type) {
			case ADD :
				return parseAdd(args);
			case DELETE : 
				return parseDel(args);
			case UPDATE : 
				return parseUpd(args);
			case QUIT :
				return parseQuit(args);
			case INVALID :
				return makeInvalid();
			case show_temp :
				return parseShow(args);
			case REDO :
				return parseRedo(args);
			case UNDO :
				return parseUndo(args);
			case MARK :
				return parseMark(args);
			case UNMARK :
				return parseUnmark(args);
			case SEARCH :
				return parseSearch(args);
			case FILTER_DATE :
				return parseFilterDate(args);
			case CHANGE_SAVE_LOC:
				return parseChangeSaveLoc(args);
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
		if (args.length() == 0) {
			return makeShow(Command.Type.SHOW_INCOMPLETE);
		}
		switch (args) {
			case "done" :
			case "complete" :
			case "completed" :
				return makeShow(Command.Type.SHOW_COMPLETE);
			default :
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
	private Command parseChangeSaveLoc (String args) {
		assert(args != null);
		Path path;
		if (args.length() != 0) {
			try {
				path = parsePath(args);
				return makeChangeSaveLoc(path);
			} catch (ParseException pe) {
				;
			}
		}

		
		return makeInvalid();
	}
	
	Task.DataType parseFieldKey (String token) throws ParseException {
		assert(token != null);
		switch (token.toLowerCase()) {
			case "name" :
				return Task.DataType.NAME;
				
			case "start" :
			case "from" :
				return Task.DataType.DATE_START;
				
			case "end" :	// Fallthrough
			case "till" :	// Fallthrough
			case "until" :	// Fallthrough
			case "due" :
				return Task.DataType.DATE_END;
				
			default:
				throw new ParseException("", -1);
		}	
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
	
	
	public Command makeAdd (String name, Date start, Date end) {
		Command cmd = new Command(Command.Type.ADD, userRawInput);
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setText(name);
		return cmd;
	}
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
	public Command makeDelete (int taskUID) {
		Command cmd = new Command(Command.Type.DELETE, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public Command makeQuit () {
		Command cmd = new Command(Command.Type.QUIT, userRawInput);
		return cmd;
	}
	public Command makeInvalid () {
		Command cmd = new Command(Command.Type.INVALID, userRawInput);
		return cmd;
	}
	public Command makeShow (Command.Type showtype) {
		Command cmd = new Command(showtype, userRawInput);
		return cmd;		
	}
	public Command makeRedo () {
		Command cmd = new Command(Command.Type.REDO, userRawInput);
		return cmd;
	}
	public Command makeUndo () {
		Command cmd = new Command(Command.Type.UNDO, userRawInput);
		return cmd;
		
	}
	public Command makeMark (int taskUID) {
		Command cmd = new Command(Command.Type.MARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public Command makeUnmark (int taskUID) {
		Command cmd = new Command(Command.Type.UNMARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public Command makeSearch (String keyword) {
		Command cmd = new Command(Command.Type.SEARCH, userRawInput);
		cmd.setText(keyword);
		return cmd;
	}
	public Command makeFilterDate (Date rangeStart, Date rangeEnd) {
		Command cmd = new Command(Command.Type.FILTER_DATE, userRawInput);
		cmd.setStart(rangeStart);
		cmd.setEnd(rangeEnd);
		return cmd;
	}
	public Command makeChangeSaveLoc (Path newPath) {
		Command cmd = new Command(Command.Type.CHANGE_SAVE_LOC, userRawInput);
		cmd.setPath(newPath);
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
	
	private static final <T> boolean arrayContains (T[] arr, T key) {
		assert(arr != null && key != null);
		for (T item : arr) {
			if (key.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		Parser p = new Parser();
		while (true) {
			printCmd(p.parseCommand(sc.nextLine()));
		}
	}

	// Logic test function	// By Ken
	public Command makeType(Command.Type type){
		return new Command(type, "");
	}
}

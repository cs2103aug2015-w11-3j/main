package parser;

//import com.sun.javafx.css.Combinator;
import common.Task;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.*;
import java.util.Scanner;

public class Parser implements ParserInterface {

	private static Parser parserInstance;
	private final DateFormatter DATE_PARSER;
	
	/////////////////////////////////////////////////////////////////
	// Patterns for user command arguments matching (trim results)
	/////////////////////////////////////////////////////////////////

	// for whitespace work
	private static final String REGEX_WHITESPACE = 
			"\\s+";
	private final Pattern P_WHITESPACE;
	
	// <name>
	private static final String REGEX_ADD_FLT = 
			"^(?<name>[^;]+)$";
	private final Pattern P_ADD_FLT;
	
	// <name>; by|due <end>
	private static final String REGEX_ADD_DL = 
			"^(?<name>[^;]+);\\s+(?:by|due)\\s(?<end>.+)$";
	private final Pattern P_ADD_DL;
	
	// <name>; from|start <start> end|to|till|until|due <end>
	private static final String REGEX_ADD_EVT = 
			"^(?<name>[^;]+);\\s+(?:from|start)\\s(?<start>.+)\\s(?:till|to|until|end|due)\\s(?<end>.+)$";
	private final Pattern P_ADD_EVT;	
	
	// <uid>
	private static final String REGEX_DEL = 
			"^(?<uid>\\d++)$";
	private final Pattern P_DEL;
	
	// <field> <uid> <newval>
	private static final String REGEX_UPD = 
			"^(?<uid>\\d+)\\s(?<field>\\w+)\\s(?<newval>.+)$";
	private final Pattern P_UPD;

	/////////////////////////////////////////////////////////////////
	// instance fields
	/////////////////////////////////////////////////////////////////
	
	private String userRawInput;

	/////////////////////////////////////////////////////////////////
	
	private Parser () {
		userRawInput = "no user input received";
		
		DATE_PARSER = new DateFormatter();
		
		P_WHITESPACE = Pattern.compile(REGEX_WHITESPACE);
		P_ADD_FLT = Pattern.compile(REGEX_ADD_FLT);
		P_ADD_DL = Pattern.compile(REGEX_ADD_DL);
		P_ADD_EVT = Pattern.compile(REGEX_ADD_EVT);
		P_DEL = Pattern.compile(REGEX_DEL);
		P_UPD = Pattern.compile(REGEX_UPD);
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
		return parseArgs(cmdType, cmdAndArgs[1]);
	}
	
	private Command.Type getCmdType (String firstToken) {
		assert(firstToken != null);
		switch (firstToken.toLowerCase()) {
		
			case "a" :		// Fallthrough
			case "add" :	// Fallthrough
			case "new" :	// Fallthrough
			case "create" :
				return Command.Type.ADD;
				
			case "d" : 		// Fallthrough
			case "del" : 	// Fallthrough
			case "delete" :	// Fallthrough
			case "rm" :		// Fallthrough
			case "remove" :
				return Command.Type.DELETE;
				
			case "u" :		// Fallthrough
			case "upd" :	// Fallthrough
			case "update" :	// Fallthrough
			case "set" :	// Fallthrough
			case "edit" :
				return Command.Type.UPDATE;
				
			case "q" :		// Fallthrough
			case "quit" :	// Fallthrough
			case "exit" :	
				return Command.Type.QUIT;
				
			case "mark" :
			case "complete" :
				return Command.Type.MARK;
				
			case "unmark" :
			case "reopen" :
				return Command.Type.UNMARK;
				
			case "undo" :
			case "un" :
				return Command.Type.UNDO;
				
			case "redo" :
			case "re" :
				return Command.Type.REDO;
				
			case "show" :
				return Command.Type.show_temp; // temp value, change later
				
			default :
				return Command.Type.INVALID;
		}
	}

	private Command parseArgs (Command.Type type, String args) {
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
		return DATE_PARSER.parseDate(token);
	}
	
	
	public Command makeAdd (String name, Date start, Date end) {
		Command cmd = new Command(Command.Type.ADD, userRawInput);
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setName(name);
		return cmd;
	}
	public Command makeUpdate (int taskUID, Task.DataType fieldType, Object newValue) throws IllegalArgumentException {
		Command cmd = new Command(Command.Type.UPDATE, userRawInput);
		cmd.setTaskField(fieldType);
		cmd.setTaskUID(taskUID);
		switch (fieldType) {
		case NAME : 
			cmd.setName((String) newValue);
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
	public Command makeSearch (String searchKey) {
		Command cmd = new Command(Command.Type.SEARCH, userRawInput);
		cmd.setName(searchKey);
		return cmd;
	}
	public Command makeFilterByDate (Date rangeStart, Date rangeEnd) {
		Command cmd = new Command(Command.Type.FILTER_DATE, userRawInput);
		cmd.setStart(rangeStart);
		cmd.setEnd(rangeEnd);
		return cmd;
	}
	public Command makeChangeSaveLoc (String newPath) {
		Command cmd = new Command(Command.Type.CHANGE_SAVE_LOC, userRawInput);
		cmd.setName(newPath);
		return cmd;
	}
	
	public static void printCmd (Command c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getName());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
	}
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		Parser p = new Parser();
		Date d = new Date(Long.MAX_VALUE);
		DateFormat df = new SimpleDateFormat("hh-mma");
		System.out.println(df.parse("9-20pm"));
//		System.out.println(d);
//		d = new Date();
//		System.out.println(d);
//		assert(false);
//		System.out.println("wot");
		
		/*while (true) {
			if (false) {
				try {
					System.out.println(parseAbsDate(sc.nextLine()));
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			if (true) {
			Command c = p.parseCommand(sc.nextLine());
			System.out.println("type: " + c.getCmdType());
			System.out.println("raw: " + c.getRawUserInput());
			System.out.println("uid: " + c.getTaskUID());
			System.out.println("fieldkey: " + c.getTaskField());
			System.out.println("name: " + c.getName());
			System.out.println("start: " + c.getStart());
			System.out.println("end: "+ c.getEnd());
			} else if (true) {
			Pattern pt = Pattern.compile(sc.nextLine());
			Matcher m = pt.matcher(sc.nextLine());
			System.out.println(m.matches());
			}
		}*/
	}
//	@Override // Logic test function	// By Ken
//	public Command makeSort() {
//		Command cmd = new Command(Command.Type.SHOW_ALL, "");
//		return cmd;
//	}
//	
	// Logic test function	// By Ken
	public Command makeType(Command.Type type){
		return new Command(type, "");
	}
}

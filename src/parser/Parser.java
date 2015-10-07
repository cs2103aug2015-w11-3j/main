package parser;

//import com.sun.javafx.css.Combinator;
import common.Celebi;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.*;
import java.util.Scanner;

public class Parser implements ParserInterface {

	/////////////////////////////////////////////////////////////////
	// Date Formats
	/////////////////////////////////////////////////////////////////
	
	// matching characters . / - _ <space>
	private static final Pattern P_DATE_DELIM = Pattern.compile("[\\Q-_/. \\E]");
	private static final String DATE_DELIM = "*";
	
	private static final DateFormat DF_A = new SimpleDateFormat("hh:mm*a*dd*MM*yyyy");
	private static final DateFormat DF_B = new SimpleDateFormat("HH:mm*dd*MM*yyyy");
	
	private static final DateFormat[] DF_ARRAY = {
				DF_A,
				DF_B
			};
	
	/////////////////////////////////////////////////////////////////
	// Patterns for user command arguments matching (trim results)
	/////////////////////////////////////////////////////////////////

	// for whitespace work
	private static final Pattern P_WHITESPACE = Pattern.compile("\\s+");
	
	// <name> 
	private static final Pattern P_ADD_FLT = Pattern.compile(
			"^(?<name>[^;]+)$"
			);
	// <name>; by|due <end> 
	private static final Pattern P_ADD_DL = Pattern.compile(
			"^(?<name>[^;]+);\\s+(?:by)|(?:due)\\s(?<end>.+)$"
			);
	// <name>; from|start <start> end|to|till|until|due <end> 
	private static final Pattern P_ADD_EVT = Pattern.compile(
			"^(?<name>[^;]+);\\s+(?:from)|(?:start)\\s(?<start>.+)\\s[(?:till)(?:to)(?:until)(?:end)(?:due)]\\s(?<end>.+)$"
			);	
	
	// <uid>
	private static final Pattern P_DEL = Pattern.compile(
			"^(?<uid>\\d++)$"
			);
	
	// <field> <uid> <newval>
	private static final Pattern P_UPD = Pattern.compile(
			"^(?<field>\\w+)\\s(?<uid>\\d+)\\s(?<newval>.+)$"
			);

	/////////////////////////////////////////////////////////////////
	// instance fields
	/////////////////////////////////////////////////////////////////
	
	String userRawInput;

	/////////////////////////////////////////////////////////////////
	
	public Parser () {
		userRawInput = "no user input received";
	}
	
	@Override
	public void init () {
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public Command parseCommand (String rawInput) {
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
		switch (firstToken.toLowerCase()) {
		
			case "a" :		// Fallthrough
			case "add" :	// Fallthrough
			case "new" :	// Fallthrough
			case "create" :
				return Command.Type.Add;
				
			case "d" : 		// Fallthrough
			case "del" : 	// Fallthrough
			case "delete" :	// Fallthrough
			case "rm" :		// Fallthrough
			case "remove" :
				return Command.Type.Delete;
				
			case "u" :		// Fallthrough
			case "upd" :	// Fallthrough
			case "update" :	// Fallthrough
			case "edit" :
				return Command.Type.Update;
				
			case "q" :		// Fallthrough
			case "quit" :	// Fallthrough
			case "exit" :	
				return Command.Type.Quit;
				
			default :
				return Command.Type.Invalid;
		}
	}

	private Command parseArgs (Command.Type type, String args) {
		switch (type) {
			case Add :
				return parseAdd(args);
			case Delete : 
				return parseDel(args);
			case Update : 
				return parseUpd(args);
			case Quit :
				return parseQuit(args);
			case Invalid :
				return makeInvalid();
			default :
				break;
			}
		return null; // should never happen
	}

	private Command parseAdd (String args) {
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
					System.out.println(ie);
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
		Matcher m = P_UPD.matcher(args);
		if (m.matches()) {
			try {
				int uid = Integer.parseInt(m.group("uid"));
				Celebi.DataType field = parseFieldKey(m.group("field"));
				Object newval = parseFieldValue(field, m.group("newval"));
				return makeUpdate(uid, field, newval);
			} catch (Exception e) {
				System.out.println(e); // unparsable tokens
			}
		}
		return makeInvalid();
	}
	private Command parseQuit (String args) {
		return makeQuit();
	}
	
	private static Celebi.DataType parseFieldKey (String token) throws ParseException {
		switch (token.toLowerCase()) {
			case "name" :
				return Celebi.DataType.NAME;
				
			case "start" :
			case "from" :
				return Celebi.DataType.DATE_START;
				
			case "end" :	// Fallthrough
			case "till" :	// Fallthrough
			case "until" :	// Fallthrough
			case "due" :
				return Celebi.DataType.DATE_END;
				
			default:
				throw new ParseException("", -1);
		}	
	}
	private static Object parseFieldValue (Celebi.DataType key, String valStr) throws ParseException, IllegalArgumentException {
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
	private static String parseText (String token) {
		return token.trim();
	}
	private static Date parseDate (String token) throws ParseException {
		token = token.trim().toLowerCase();
		switch (token) {
		
			// current time
			case "now" :
				return new Date();
				
			// +24h
			case "tmr" :		// Fallthrough
			case "tomorrow" :
				return new Date(1000*60*60*24 + (new Date()).getTime());
				
			// +24*7h
			case "next week" :
				return new Date(1000*60*60*24*7 + (new Date()).getTime());
				
			default :
				return parseAbsDate(token);
		}
	}
	private static Date parseAbsDate (String token) throws ParseException {
		// replace date delimiters with common token
		token = P_DATE_DELIM.matcher(token.trim()).replaceAll(DATE_DELIM);
		for (DateFormat df : DF_ARRAY) {
			try {
				Date d = df.parse(token);
				if (d != null) {
					return d;
				}
			} catch (ParseException e) {
				;
			}
		}
		throw new ParseException("", -1);
	}
	
	public Command makeAdd (String name, Date start, Date end) {
		Command cmd = new Command(Command.Type.Add, userRawInput);
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setName(name);
		return cmd;
	}
	public Command makeUpdate (int taskUID, Celebi.DataType fieldType, Object newValue) throws IllegalArgumentException {
		Command cmd = new Command(Command.Type.Update, userRawInput);
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
		Command cmd = new Command(Command.Type.Delete, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	public Command makeQuit () {
		Command cmd = new Command(Command.Type.Quit, userRawInput);
		return cmd;
	}
	public Command makeInvalid () {
		Command cmd = new Command(Command.Type.Invalid, userRawInput);
		return cmd;
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Parser p = new Parser();
		while (true) {
			if (true) {
				try {
					System.out.println(parseAbsDate(sc.nextLine()));
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			if (false) {
			Command c = p.parseCommand(sc.nextLine());
			System.out.println("type: " + c.getCmdType());
			System.out.println("raw: " + c.getRawUserInput());
			System.out.println("uid: " + c.getCelebiUID());
			System.out.println("fieldkey: " + c.getCelebiField());
			System.out.println("name: " + c.getName());
			System.out.println("start: " + c.getStart());
			System.out.println("end: "+ c.getEnd());
			} else if (false) {
			Pattern pt = Pattern.compile(sc.nextLine());
			Matcher m = pt.matcher(sc.nextLine());
			System.out.println(m.matches());
			}
		}
	}
}

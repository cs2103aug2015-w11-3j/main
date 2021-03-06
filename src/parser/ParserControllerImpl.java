//@@author A0131891E
package parser;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.sun.javafx.css.Combinator;
import common.Task;
import common.TasksBag;
import common.Utilities;
import parser.commands.CommandData;
import parser.commands.CommandDataImpl;
import parser.temporal.CelebiDateParser;
import parser.temporal.DateParser;
import ui.view.CelebiViewController.Skin;

public class ParserControllerImpl implements ParserController {
	
	/////////////////////////////////////////////////////////////////
	// Patterns for user command arguments matching (trim results)
	/////////////////////////////////////////////////////////////////

	// for whitespace work
	private final Pattern P_WHITESPACE;
	private static final String REG_WHITESPACE = 
			"\\s+";
	
	// regexes for all cmds for consistency
	private static final String REG_MATCHING_NAME_FIELD = regexContaining(Aliases.FIELD_NAME);
	private static final String REG_MATCHING_START_FIELD = regexContaining(Aliases.FIELD_START_DATE);
	private static final String REG_MATCHING_END_FIELD = regexContaining(Aliases.FIELD_END_DATE);
	
	private static final String REG_MATCHING_SHOW_DEFAULT = regexContaining(Aliases.VIEW_DEFAULT);
	private static final String REG_MATCHING_SHOW_COMPLETED = regexContaining(Aliases.VIEW_COMPLETED);
	private static final String REG_MATCHING_SHOW_INCOMPLETE = regexContaining(Aliases.VIEW_INCOMPLETE);
	
	private static final String REG_MATCHING_THEME_DAY = regexContaining(Aliases.THEME_DAY);
	private static final String REG_MATCHING_THEME_NIGHT = regexContaining(Aliases.THEME_NIGHT);
	
	private static final String REG_MATCHING_CLEAR_VAL = regexContaining(Aliases.CLEAR_VAL);
	
	private static final String GRPNAME_NAME = "name";
	private static final String GRPNAME_START = "start";
	private static final String GRPNAME_END = "end";
	private static final String GRPNAME_UID = "uid";
	
	private static final String REG_VALID_NAME = "[^;]+";
	private static final String REG_UNVALIDATED_DATE = ".+"; // still needs to get parsed by date formatter
	private static final String REG_UID = "-?\\d+"; // supports negative numbers for logic to throw exception
	
	private static final String REG_GRP_NAME = regexNamedGrp(REG_VALID_NAME, GRPNAME_NAME);
	private static final String REG_GRP_START = regexNamedGrp(REG_UNVALIDATED_DATE, GRPNAME_START);
	private static final String REG_GRP_END = regexNamedGrp(REG_UNVALIDATED_DATE, GRPNAME_END);
	private static final String REG_GRP_UID = regexNamedGrp(REG_UID, GRPNAME_UID);
	
	////////////////////////////////////////////////////////////////
	// ADD command parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String REG_NAME_DATE_DELIM = "\\s*;\\s*";
	
	// <name>
	private final Pattern P_ADD_FLT;
	private static final String REG_ADD_FLT = concatArgs(
		'^',
		REG_GRP_NAME,
		'$'
	);
		
	
	// <name>; <start field identifier> <start>
	private final Pattern P_ADD_START;
	private static final String REG_ADD_START = concatArgs(
		'^',
		REG_GRP_NAME,
		REG_NAME_DATE_DELIM,
		REG_MATCHING_START_FIELD,
		REG_WHITESPACE,
		REG_GRP_START,
		'$'
	);

	// <name>; <end field identifier> <end>
	private final Pattern P_ADD_END;
	private static final String REG_ADD_END = concatArgs(
		'^',
		REG_GRP_NAME,
		REG_NAME_DATE_DELIM,
		REG_MATCHING_END_FIELD,
		REG_WHITESPACE,
		REG_GRP_END,
		'$'
	);

	// <name>; <start field identifier> <start> <end field identifier> <end>
	private final Pattern P_ADD_EVT;
	private static final String REG_ADD_EVT = concatArgs(
		'^',
		REG_GRP_NAME,
		REG_NAME_DATE_DELIM,
		REG_MATCHING_START_FIELD,
		REG_WHITESPACE,
		REG_GRP_START,
		REG_WHITESPACE,
		REG_MATCHING_END_FIELD,
		REG_WHITESPACE,
		REG_GRP_END,
		'$'
	);

	////////////////////////////////////////////////////////////////
	// UPD command parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String GRPNAME_NEWVAL = "newval";
	private static final String REG_GRP_NEW_NAME = regexNamedGrp(REG_VALID_NAME, GRPNAME_NEWVAL);
	private static final String REG_GRP_NEW_DATE = regexNamedGrp(REG_UNVALIDATED_DATE, GRPNAME_NEWVAL);
	
	// <field> <uid> <newval>
	private final Pattern P_UPD_NAME;
	private static final String REG_UPD_NAME = concatArgs(
		'^',
		REG_GRP_UID,
		REG_WHITESPACE,
		REG_MATCHING_NAME_FIELD,
		REG_WHITESPACE,
		REG_GRP_NEW_NAME,
		'$'
	);
	private final Pattern P_UPD_START;
	private static final String REG_UPD_START = concatArgs(
		'^',
		REG_GRP_UID,
		REG_WHITESPACE,
		REG_MATCHING_START_FIELD,
		REG_WHITESPACE,
		REG_GRP_NEW_DATE,
		'$'
	);
	private final Pattern P_UPD_END;
	private static final String REG_UPD_END = concatArgs(
		'^',
		REG_GRP_UID,
		REG_WHITESPACE,
		REG_MATCHING_END_FIELD,
		REG_WHITESPACE,
		REG_GRP_NEW_DATE,
		'$'
	);

	////////////////////////////////////////////////////////////////
	// UID arg only command (DEL, MARK, UNMARK, parsing parameters
	////////////////////////////////////////////////////////////////
	
	private static final String REG_UID_ONLY = concatArgs(
		'^',
		REG_GRP_UID,
		'$'
	);

	private final Pattern P_DEL;
	private static final String REG_DEL = REG_UID_ONLY;
	private final Pattern P_MARK;
	private static final String REG_MARK = REG_UID_ONLY;
	private final Pattern P_UNMARK;
	private static final String REG_UNMARK = REG_UID_ONLY;

	
	////////////////////////////////////////////////////////////////
	// SHOW command parsing parameters
	////////////////////////////////////////////////////////////////

	private final Pattern P_SHOW_DEFAULT;
	private static final String REG_SHOW_DEFAULT = concatArgs(
		'^',
		REG_MATCHING_SHOW_DEFAULT,
		'$'
	);
	
	private final Pattern P_SHOW_INCOMPLETE;
	private static final String REG_SHOW_INCOMPLETE = concatArgs(
		'^',
		REG_MATCHING_SHOW_INCOMPLETE,
		'$'
	);
	
	private final Pattern P_SHOW_COMPLETED;
	private static final String REG_SHOW_COMPLETED = concatArgs(
		'^',
		REG_MATCHING_SHOW_COMPLETED,
		'$'
	);
	
	
	////////////////////////////////////////////////////////////////
	// FILTER command parsing parameters
	////////////////////////////////////////////////////////////////

	private static final String GRPNAME_FIL_LO_BOUND = "start";
	private static final String GRPNAME_FIL_HI_BOUND = "end";
	private static final String REG_GRP_FIL_LO_BOUND = 
			regexNamedGrp(REG_UNVALIDATED_DATE, GRPNAME_FIL_LO_BOUND);
	private static final String REG_GRP_FIL_HI_BOUND = 
			regexNamedGrp(REG_UNVALIDATED_DATE, GRPNAME_FIL_HI_BOUND);
	
	// before|bef <key(date,>
	private final Pattern P_FILTER_BEF;
	private static final String REG_FILTER_BEF = concatArgs(
		'^',
		regexContaining(Aliases.FILTER_ARG_BEF),
		REG_WHITESPACE,
		REG_GRP_FIL_HI_BOUND,
		'$'
	);

	// after|aft <key(date,>
	private final Pattern P_FILTER_AFT;
	private static final String REG_FILTER_AFT = concatArgs(
		'^',
		regexContaining(Aliases.FILTER_ARG_AFT),
		REG_WHITESPACE,
		REG_GRP_FIL_LO_BOUND,
		'$'
	);
	
	// between|b/w|btw|from|start <key1(date,> and|to|till|until|end <key2(date,>
	private final Pattern P_FILTER_BTW;
	private static final String REG_FILTER_BTW = concatArgs(
		'^',
		regexContaining(Aliases.FILTER_ARG_BTW_START),
		REG_WHITESPACE,
		REG_GRP_FIL_LO_BOUND,
		REG_WHITESPACE,
		regexContaining(Aliases.FILTER_ARG_BTW_END),
		REG_WHITESPACE,
		REG_GRP_FIL_HI_BOUND,
		'$'
	);
	

	////////////////////////////////////////////////////////////////
	// THEME command parsing parameters
	////////////////////////////////////////////////////////////////

	private final Pattern P_THEME_DAY;
	private static final String REG_THEME_DAY = concatArgs(
		'^',
		REG_MATCHING_THEME_DAY,
		'$'
	);
	private final Pattern P_THEME_NIGHT;
	private static final String REG_THEME_NIGHT = concatArgs(
		'^',
		REG_MATCHING_THEME_NIGHT,
		'$'
	);


	////////////////////////////////////////////////////////////////
	// ALIAS command parsing parameters
	////////////////////////////////////////////////////////////////

	private static final String REG_VALID_CMD = "\\S+";
	private static final String GRPNAME_TARGET_CMD = "target";
	private static final String GRPNAME_ALIAS = "alias";
	private static final String REG_GRP_ALIAS_TARGET = regexNamedGrp(REG_VALID_CMD, GRPNAME_TARGET_CMD);
	private static final String REG_GRP_ALIAS_NEW = regexNamedGrp(REG_VALID_CMD, GRPNAME_ALIAS);
	
	private final Pattern P_ALIAS_NEW;
	private static final String REG_ALIAS_NEW = concatArgs(
		'^',
		REG_GRP_ALIAS_TARGET,
		REG_WHITESPACE,
		REG_GRP_ALIAS_NEW,
		'$'
	);
	private final Pattern P_ALIAS_CLR;
	private static final String REG_ALIAS_CLR = concatArgs(
		'^',
		REG_MATCHING_CLEAR_VAL,
		'$'
	);
	
	
	/////////////////////////////////////////////////////////////////
	// instance fields
	/////////////////////////////////////////////////////////////////
	private String userRawInput;
	private static ParserControllerImpl parserInstance;
	private final CelebiDateParser DATE_FORMATTER;
	private final Aliases ALIASES;

	/////////////////////////////////////////////////////////////////
	
	private ParserControllerImpl() {
		
		userRawInput = "no user input received";
		ALIASES = AliasesImpl.getInstance();
		DATE_FORMATTER = new DateParser();
		
		P_WHITESPACE = Pattern.compile(REG_WHITESPACE);
		
		P_ADD_FLT = Pattern.compile(REG_ADD_FLT, CASE_INSENSITIVE);
		P_ADD_START = Pattern.compile(REG_ADD_START, CASE_INSENSITIVE);
		P_ADD_END = Pattern.compile(REG_ADD_END, CASE_INSENSITIVE);
		P_ADD_EVT = Pattern.compile(REG_ADD_EVT, CASE_INSENSITIVE);
		
		P_UPD_NAME = Pattern.compile(REG_UPD_NAME, CASE_INSENSITIVE);
		P_UPD_START = Pattern.compile(REG_UPD_START, CASE_INSENSITIVE);
		P_UPD_END = Pattern.compile(REG_UPD_END, CASE_INSENSITIVE);
		
		P_DEL = Pattern.compile(REG_DEL, CASE_INSENSITIVE);
		P_MARK = Pattern.compile(REG_MARK, CASE_INSENSITIVE);
		P_UNMARK = Pattern.compile(REG_UNMARK, CASE_INSENSITIVE);
		
		P_SHOW_DEFAULT = Pattern.compile(REG_SHOW_DEFAULT);
		P_SHOW_INCOMPLETE = Pattern.compile(REG_SHOW_INCOMPLETE);
		P_SHOW_COMPLETED = Pattern.compile(REG_SHOW_COMPLETED);
		
		P_FILTER_BEF = Pattern.compile(REG_FILTER_BEF, CASE_INSENSITIVE);
		P_FILTER_AFT = Pattern.compile(REG_FILTER_AFT, CASE_INSENSITIVE);
		P_FILTER_BTW = Pattern.compile(REG_FILTER_BTW, CASE_INSENSITIVE);
		
		P_THEME_DAY = Pattern.compile(REG_THEME_DAY);
		P_THEME_NIGHT = Pattern.compile(REG_THEME_NIGHT);
		
		P_ALIAS_NEW = Pattern.compile(REG_ALIAS_NEW);
		P_ALIAS_CLR = Pattern.compile(REG_ALIAS_CLR);
	}
	// singleton access
	public static ParserControllerImpl getParser() {
		if (parserInstance == null) {
			parserInstance = new ParserControllerImpl();
		}
		return parserInstance;
	}
	
	@Override
	public void init() {
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	private static final String regexContaining(String[] tokens) {
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
	private static final String regexNamedGrp(String regex, String grpName) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(?");
		sb.append('<').append(grpName).append('>');
		sb.append(regex);
		sb.append(')');
		return sb.toString();
}
	private static final String concatArgs(Object... args) {
		final StringBuilder sb = new StringBuilder();
		for (Object item : args) {
			sb.append(item);
		}
		return sb.toString();
	}
	
	@Override
	public CommandDataImpl parseCommandData(String rawInput) {
		assert(rawInput != null);
		// TODO rework to not use String.split
		userRawInput = rawInput;
		// Splits input string at first whitespace substring, trimming trailing whitespace
		String[] cmdAndArgs = P_WHITESPACE.split(rawInput.trim(), 2);
		// if no command args, set the args half of the array to empty string
		if (cmdAndArgs.length != 2) { 
			cmdAndArgs = new String[]{cmdAndArgs[0], ""};
		}
		CommandData.Type cmdType = parseCmdType(cmdAndArgs[0]);
		return passArgs(cmdType, cmdAndArgs[1]);
	}
	
	private CommandData.Type parseCmdType(String token) {
		assert(token != null);
		token = cleanText(token);
		
		final CommandData.Type cmdType = ALIASES.getCmdFromAlias(token);
		return cmdType == null ? CommandData.Type.INVALID : cmdType;
	}

	private CommandDataImpl passArgs(CommandData.Type type, String args) {
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
			case ALIAS :
				return parseAlias(args);
				//break;
			default :
				break;
			}
		assert(false); // should never happen
		return null;
	}

	private CommandDataImpl parseAdd(String args) {
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
	private CommandDataImpl parseDel(String args) {
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
	private CommandDataImpl parseUpd(String args) {
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
				newStart = parseUpdDate(newValue, true);
				return makeUpdateStart(uid, newStart);
			}
	
			m = P_UPD_END.matcher(args);
			if (m.matches()) {
				uid = Integer.parseInt(m.group(GRPNAME_UID));
				newValue = m.group(GRPNAME_NEWVAL);
				newEnd = parseUpdDate(newValue, false);
				return makeUpdateEnd(uid, newEnd);
			}
		} catch (ParseException pe) {
			//System.out.println(pe);
		}
		return makeInvalid();
	}
	private CommandDataImpl parseQuit (String args) {
		assert(args != null);
		return makeQuit();
	}
	private CommandDataImpl parseShow (String args) {
		assert(args != null);
		args = cleanText(args);
		
		Matcher m;
		TasksBag.ViewType view;
		
		m = P_SHOW_DEFAULT.matcher(args);
		if (m.matches()) {
			view = TasksBag.ViewType.DEFAULT;
			return makeShow(view);
		}
		
		m = P_SHOW_INCOMPLETE.matcher(args);
		if (m.matches()) {
			view = TasksBag.ViewType.INCOMPLETE;
			return makeShow(view);
		}
		
		m = P_SHOW_COMPLETED.matcher(args);
		if (m.matches()) {
			view = TasksBag.ViewType.COMPLETED;
			return makeShow(view);
		}
		

		return makeInvalid();
	}
	private CommandDataImpl parseRedo (String args) {
		assert(args != null);
		return makeRedo();
	}
	private CommandDataImpl parseUndo (String args) {
		assert(args != null);
		return makeUndo();
	}
	private CommandDataImpl parseMark (String args) {
		assert(args != null);
		args = args.trim();
		final Matcher m = P_MARK.matcher(args);
		if (m.matches()) {
			final int uid = Integer.parseInt(m.group(GRPNAME_UID));
			return makeMark(uid);
		}
		return makeInvalid();
	}
	private CommandDataImpl parseUnmark (String args) {
		assert(args != null);
		args = args.trim();
		final Matcher m = P_UNMARK.matcher(args);
		if (m.matches()) {
			final int uid = Integer.parseInt(m.group(GRPNAME_UID));
			return makeUnmark(uid);
		}
		return makeInvalid();
	}
	private CommandDataImpl parseSearch (String args) {
		assert(args != null);
		args = args.trim();
		if (args.length() != 0) {
			return makeSearch(args);
		}
		return makeInvalid();
	}
	private CommandDataImpl parseFilterDate (String args) {
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
	private CommandDataImpl parseClear (String args) {
		assert(args != null);
		return makeClear();
	}
	private CommandDataImpl parseMove (String args) {
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
	private CommandDataImpl parseHelp (String args) {
		assert(args != null);
		args = args.trim();
		if (args.length() == 0) { // no args for help cmd
			return makeHelp(null);
		}
		CommandData.Type helpTarget = parseCmdType(args);
		// help args can be parsed into a cmd type
		if (helpTarget != CommandData.Type.INVALID) {
			return makeHelp(helpTarget);
		}
		return makeInvalid();
	}
	private CommandDataImpl parseTheme (String args) {
		assert(args != null);
		args = cleanText(args);
		
		Matcher m;
		
		m = P_THEME_DAY.matcher(args);
		if (m.matches()) {
			return makeTheme(Skin.DAY);
		}
		
		m = P_THEME_NIGHT.matcher(args);
		if (m.matches()) {
			return makeTheme(Skin.NIGHT);
		}

		return makeInvalid();
	}
	private CommandDataImpl parseAlias (String args) {
		assert(args != null);
		args = cleanText(args);
		
		final CommandData.Type target;
		final String alias;
		Matcher m;
		
		m = P_ALIAS_CLR.matcher(args);
		if (m.matches()) {
			return makeAlias(null, null);
		}
		
		m = P_ALIAS_NEW.matcher(args);
		if (m.matches()) {
			target = parseCmdType(cleanText(m.group(GRPNAME_TARGET_CMD)));
			alias = cleanText(m.group(GRPNAME_ALIAS)); // can be any non whitespace string
			if (target != CommandData.Type.INVALID ) {
				return makeAlias(alias, target);
			}
		}
		
		return makeInvalid();
	}
	
	// cleans a string by trimming trailing whitespace and shifting alpha to lowercase
	public static String cleanText (String token) {
		assert(token != null);
		return token.trim().toLowerCase();
	}
	Date parseDate (String token, boolean isStart) throws ParseException {
		assert(token != null);
		return DATE_FORMATTER.parseDate(token, isStart);
	}
	public static Path parsePath (String token) throws ParseException {
		assert(token != null);
		return Paths.get(token.trim());
	}
	// Used only in parseUpdate: allows special datestrings
	// to signify removal of a date field from the task.
	// Allows conversion from event->deadline/startonly->float
	private Date parseUpdDate (String dateStr, boolean isStart) throws ParseException {
		assert(dateStr != null);
		cleanText(dateStr);
		final List<String> clearDateAliases = Arrays.asList(Aliases.CLEAR_VAL);
		if (clearDateAliases.contains(dateStr)) {
			return null;
		}
		return parseDate(dateStr, isStart);
	}
	
	
	public CommandDataImpl makeAdd (String name, Date start, Date end) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.ADD, userRawInput);
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setText(name);
		return cmd;
	}
	
	public CommandDataImpl makeUpdateName (int taskUID, String newName) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.NAME);
		cmd.setTaskUID(taskUID);
		cmd.setText(newName);
		return cmd;	
	}
	
	public CommandDataImpl makeUpdateStart (int taskUID, Date newDate) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.DATE_START);
		cmd.setTaskUID(taskUID);
		cmd.setStart(newDate);
		return cmd;	
	}
	
	public CommandDataImpl makeUpdateEnd (int taskUID, Date newDate) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.UPDATE, userRawInput);
		cmd.setTaskField(Task.DataType.DATE_END);
		cmd.setTaskUID(taskUID);
		cmd.setEnd(newDate);
		return cmd;	
	}
	
	public CommandDataImpl makeDelete (int taskUID) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.DELETE, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	
	public CommandDataImpl makeShow (TasksBag.ViewType view) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.SHOW, userRawInput);
		cmd.setViewType(view);
		return cmd;		
	}
	
	public CommandDataImpl makeRedo () {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.REDO, userRawInput);
		return cmd;
	}
	
	public CommandDataImpl makeUndo () {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.UNDO, userRawInput);
		return cmd;
		
	}
	
	public CommandDataImpl makeMark (int taskUID) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.MARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	
	public CommandDataImpl makeUnmark (int taskUID) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.UNMARK, userRawInput);
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	
	public CommandDataImpl makeSearch (String keywords) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.SEARCH, userRawInput);
		cmd.setText(keywords);
		return cmd;
	}
	
	public CommandDataImpl makeFilterDate (Date rangeStart, Date rangeEnd) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.FILTER_DATE, userRawInput);
		cmd.setStart(rangeStart);
		cmd.setEnd(rangeEnd);
		return cmd;
	}
	
	public CommandDataImpl makeClear () {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.CLEAR_FILTERS, userRawInput);
		return cmd;
	}
	
	public CommandDataImpl makeMove (Path newPath) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.MOVE, userRawInput);
		cmd.setPath(newPath);
		return cmd;
	}
	
	public CommandDataImpl makeHelp (CommandData.Type helpTarget) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.HELP, userRawInput);
		cmd.setSecondaryCmdType(helpTarget);
		return cmd;
	}
	
	public CommandDataImpl makeQuit () {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.QUIT, userRawInput);
		return cmd;
	}
	
	public CommandDataImpl makeTheme (Skin theme) {
        final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.THEME, userRawInput);
	    cmd.setTheme(theme);
	    return cmd;
	}
	
	public CommandDataImpl makeAlias (String alias, CommandData.Type target) {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.ALIAS, userRawInput);
		cmd.setText(alias);
		cmd.setSecondaryCmdType(target);
		return cmd;
	}
	
	public CommandDataImpl makeInvalid () {
		final CommandDataImpl cmd = new CommandDataImpl(CommandData.Type.INVALID, userRawInput);
		return cmd;
	}
	
	public static void printCmd(CommandData c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.print("uid: " + c.getTaskUID());
		System.out.println("   fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getText());
		System.out.print("start: " + c.getStart());
		System.out.println("   end: "+ c.getEnd());
		System.out.println("2nd cmd type: " + c.getSecondaryCmdType());
	}
	
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		ParserControllerImpl p = new ParserControllerImpl();
		while (true) {
//			System.out.println(p.P_ADD_FLT.pattern());
//			System.out.println(p.P_ADD_FLT.matcher(sc.nextLine()).matches());
			printCmd(p.parseCommandData(sc.nextLine()));
		}
	}
}

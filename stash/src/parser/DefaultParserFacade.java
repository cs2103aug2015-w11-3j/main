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

import common.TasksBag;
import common.TasksBag.ViewType;
import common.Utilities;
import parser.commands.Command;
import parser.commands.CommandParams;
import parser.commands.CommandParamsExtractor;
import parser.commands.CommandParamsExtractorImpl;
import parser.tokens.TokenController;
import parser.tokens.DefaultTokenController;
import ui.view.CelebiViewController.Skin;

public class DefaultParserFacade implements ParserFacade {
	
	/////////////////////////////////////////////////////////////////
	// instance fields
	/////////////////////////////////////////////////////////////////

	private static DefaultParserFacade parserInstance;
	
	private final Formatter<Date> DATE_FORMATTER;
	private final TokenController TOKEN_CONTROLLER;
	private final CommandParamsExtractor PARSE_INVOKER;

	/////////////////////////////////////////////////////////////////
	
	private DefaultParserFacade() {
		TOKEN_CONTROLLER = DefaultTokenController.getInstance();
		DATE_PARSER = new DateParserImpl();
		DATE_FORMATTER = new DateFormatterImpl();
		PARSE_INVOKER = new CommandParamsExtractorImpl();
	}
	// singleton access
	public static DefaultParserFacade getInstance() {
		if(parserInstance == null) {
			parserInstance = new DefaultParserFacade();
			parserInstance.TOKEN_CONTROLLER.init();
		}
		return parserInstance;
	}
	
	@Override
	public void init() {
		System.out.println("Parser Init");
		TOKEN_CONTROLLER.init();
		System.out.println("Parser Init complete");
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
		for(Object item : args) {
			sb.append(item);
		}
		return sb.toString();
	}

	//////////////////////////////////////
	// PARSE_CMD subsystem
	//////////////////////////////////////
	
	@Override
	public CommandParams parse(String rawInput) throws ParseException {
		assert(rawInput != null);
		
		// Splits input string at first whitespace substring, trimming trailing whitespace
		String[] cmdAndArgs = P_WHITESPACE.split(rawInput.trim(), 2);
		// if no command args, set the args half of the array to empty string
		if(cmdAndArgs.length != 2) { 
			cmdAndArgs = new String[]{cmdAndArgs[0], ""};
		}
		CommandParams.CmdType cmdType = parseCmdType(cmdAndArgs[0]);
		return passArgs(cmdType, cmdAndArgs[1]);
	}
	
	private CommandParams.CmdType parseCmdType(String token) {
		assert(token != null);
		token = ParserFacade.cleanText(token);
		
		final CommandParams.CmdType cmdType = TOKEN_CONTROLLER.getCmdType(token);
		return cmdType == null ? CommandParams.CmdType.INVALID : cmdType;
	}

	//////////////////////////////////////
	// CREATE_CMD subsystem
	//////////////////////////////////////
	
	@Override
	public CommandParams makeAdd(String userRawInput, String name, Date start, Date end) {
		return Command.makeAdd(name, name, start, end);
	}
	@Override
	public CommandParams makeUpdateName(String userRawInput, int taskUID, String newName) {
		return Command.makeUpdateName(userRawInput, taskUID, newName);
	}
	@Override
	public CommandParams makeUpdateStart(String userRawInput, int taskUID, Date newDate) { 
		return Command.makeUpdateStart(userRawInput, taskUID, newDate); 
	}
	@Override
	public CommandParams makeUpdateEnd(String userRawInput, int taskUID, Date newDate) { 
		return Command.makeUpdateEnd(userRawInput, taskUID, newDate); 
	}
	@Override
	public CommandParams makeDelete(String userRawInput, int taskUID) { 
		return Command.makeDelete(userRawInput, taskUID); 
	}
	@Override
	public CommandParams makeShow(String userRawInput, ViewType view) { 
		return Command.makeShow(userRawInput, view);
	}
	@Override
	public CommandParams makeRedo(String userRawInput) { 
		return Command.makeRedo(userRawInput); 
	}
	@Override
	public CommandParams makeUndo(String userRawInput) { 
		return Command.makeUndo(userRawInput); 
	}
	@Override
	public CommandParams makeMark(String userRawInput, int taskUID) { 
		return Command.makeMark(userRawInput, taskUID); 
	}
	@Override
	public CommandParams makeUnmark(String userRawInput, int taskUID) { 
		return Command.makeUnmark(userRawInput, taskUID); 
	}
	@Override
	public CommandParams makeSearch(String userRawInput, String keywords) { 
		return Command.makeSearch(userRawInput, keywords); 
	}
	@Override
	public CommandParams makeFilterDate(String userRawInput, Date rangeStart, Date rangeEnd) { 
		return Command.makeFilterDate(userRawInput, rangeStart, rangeEnd); 
	}
	@Override
	public CommandParams makeClear(String userRawInput) { 
		return Command.makeClear(userRawInput); 
	}
	@Override
	public CommandParams makeMove(String userRawInput, Path newPath) { 
		return Command.makeMove(userRawInput, newPath); 
	}
	@Override
	public CommandParams makeHelp(String userRawInput) { 
		return Command.makeHelp(userRawInput); 
	}
	@Override
	public CommandParams makeQuit(String userRawInput) { 
		return Command.makeQuit(userRawInput); 
	}
	@Override
	public CommandParams makeTheme(String userRawInput, Skin theme) { 
		return Command.makeTheme(userRawInput, theme); 
	}
	@Override
	public CommandParams makeAlias(String userRawInput, String alias, CommandParams.CmdType target) { 
		return Command.makeAlias(userRawInput, alias, target); 
	}
	@Override
	public CommandParams makeInvalid(String userRawInput) { 
		return Command.makeInvalid(userRawInput); 
	}
	
	//////////////////////////////////////
	// KEYWORD_CHECK subsystem
	//////////////////////////////////////
	
	public boolean isCmdKeyword(String keyword) {
		return TOKEN_CONTROLLER.isCmdKeyword(keyword);
	}
	public boolean isCustomCmdAlias(String keyword) {
		return TOKEN_CONTROLLER.isCustomCmdAlias(keyword);
	}
	public boolean isDefaultCmdKeyword(String keyword) {
		return TOKEN_CONTROLLER.isDefaultCmdKeyword(keyword);
	}
	public boolean isReservedCmdKeyword(String keyword) {
		return TOKEN_CONTROLLER.isReservedCmdKeyword(keyword);
	}

	//////////////////////////////////////
	// FORMAT_HELP subsystem
	//////////////////////////////////////
	
	public String getHelpTooltip(String firstToken) {
		return TOKEN_CONTROLLER.getHelpTooltip(firstToken);
	}

	//////////////////////////////////////
	// FORMAT_DATE subsystem
	//////////////////////////////////////
	
	@Override
	public String format(Date d) {
		return DATE_FORMATTER.format(d);
	}


	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		DefaultParserFacade p = new DefaultParserFacade();
		while(true) {
			ParserFacade.printCmd(p.parse(sc.nextLine()));
		}
	}
	@Override
	public boolean isPossiblyValid(String token) {
		return true; // Checking is expected to equal parsing in cost.
	}
}

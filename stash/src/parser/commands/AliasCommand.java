//@@author A0131891E
package parser.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.VariadicCommand;
import parser.tokens.DefaultTokenController;

/**
 * 2-ary Alias Command
 * @author Leow Yijin
 */
public class AliasCommand extends VariadicCommand implements ConcreteCommand {

	private static String REGEX_LEFT_ARROW_GRP = "(?:<-*)";
	private static String REGEX_RIGHT_ARROW_GRP = "(?:-*>)";
	private static String GRPNAME_TARGET = "target";
	private static String GRPNAME_ALIAS = "alias";
	private static String REGEX_TARGET_GRP = "(?<"+GRPNAME_TARGET+">\\S+)";
	private static String REGEX_ALIAS_GRP = "(?<"+GRPNAME_ALIAS+">\\S+)";

	private static String REGEX_TARGET_LEFT = 
			REGEX_TARGET_GRP + "\\s*" + REGEX_LEFT_ARROW_GRP + "?\\s+" + REGEX_ALIAS_GRP;
	private static String REGEX_TARGET_RIGHT = 
			REGEX_ALIAS_GRP + "\\s*" + REGEX_RIGHT_ARROW_GRP + "\\s*" + REGEX_TARGET_GRP;

	// This pattern will match the cases where the arguments are ordered {target, alias}
	private static final Pattern P_TARGET_LEFT = Pattern.compile(REGEX_TARGET_LEFT);
	// This pattern will match the cases where the arguments are ordered {target, alias}
	private static final Pattern P_TARGET_RIGHT = Pattern.compile(REGEX_TARGET_RIGHT);
	
	public static final CmdType TYPE = CmdType.ALIAS;
    public static final String TOOLTIP_FORMAT_STRING = 
    		"Set Alias for Command: [ %1$s \"target command\" \"new alias\" ]   [ %1$s reset ]";
	public static final String RESERVED_KEYWORD = "alias";
	private static final String[] PRESET_ALIASES = {
			"map",
			"shortcut",
			"point",
			"redirect"
		};

	public static final ParseException FORMAT_EXCEPTION = 
			new ParseException(String.format(WARNING_MSG, RESERVED_KEYWORD), -1);

	// 2 arguments for setting up a mapping
	private static final int NUM_TOKENS_SET = 2;
	private static final int INDEX_SET_TARGET = 0;
	private static final int INDEX_SET_ALIAS = 1;
	// 1 argument for clearing all mappings
	private static final int NUM_TOKENS_RESET = 1;
	
	// 2 subtypes: setting a mapping or clearing all mappings
	private enum SubType {
		SET, RESET
	}
	private String alias;
	private CmdType target;
	private SubType subType;
	
	public AliasCommand(String input) {
		super(input);
	}

	@Override
	public Collection<String> getDefaultKeywords() {
		return new ArrayList<>(Arrays.asList(PRESET_ALIASES));
	}
	@Override
	public String getTooltipStr() {
		return TOOLTIP_FORMAT_STRING;
	}
	@Override
	public String getReservedKeyword() {
		return RESERVED_KEYWORD;
	}
	@Override
	public CmdType getCmdType() {
		return TYPE;
	}
	
	// TOKENISE
	
	@Override
	protected String[] tokenise() throws ParseException {
		// try stricter subcommand format first
		try {
			return tokeniseSet();
		} catch (ParseException e) {
		}
		return tokeniseReset();
	}
	
	// tokenises based on reset aliases subcommand
	private String[] tokeniseReset() throws ParseException {
		String[] tokens = new String[NUM_TOKENS_RESET];
		tokens[0] = ARG_STRING;
		subType = SubType.RESET;
		return tokens;
	}
	// tokenises based on set alias subcommand
	private String[] tokeniseSet() throws ParseException {
		String[] tokens = new String[NUM_TOKENS_SET];
		Matcher m;
		
		m = P_TARGET_LEFT.matcher(ARG_STRING);
		if (!m.matches()) {
			m = P_TARGET_RIGHT.matcher(ARG_STRING);
			if (!m.matches()) {
				throw FORMAT_EXCEPTION;
			}
		}
		
		tokens[INDEX_SET_TARGET] = m.group(GRPNAME_TARGET);
		tokens[INDEX_SET_ALIAS] = m.group(GRPNAME_ALIAS);
		subType = SubType.SET;
		return tokens;
	}
	
	
	// PARSE AND VALIDATE TOKENS
	
	@Override
	protected void parseAndValidateArgs() throws ParseException {
		switch (subType) {
			case RESET :
				parseAndValidateReset();
				break;
			case SET :
				parseAndValidateSet();
				break;
			default:
				assert(false);
				break;
		}
	}
	
	private void parseAndValidateReset() throws ParseException {
		assert(tokens.length == 1);
		String token = tokens[0];
		boolean isValidSpecialResetToken = DefaultTokenController.getInstance().isResetValue(token);
		if (!isValidSpecialResetToken) {
			throw FORMAT_EXCEPTION;
		}
		// contract for alias clear: target and alias set to null
		target = null;
		alias = null;
	}
	private void parseAndValidateSet() throws ParseException {
		assert(tokens.length == 2);
		String token = tokens[INDEX_SET_TARGET];
		
		final CmdType parsedTarget = DefaultTokenController.getInstance().getCmdType(token);
		if (parsedTarget == null) {
			throw FORMAT_EXCEPTION;
		}
		
		target = parsedTarget;
		
		alias = ParserFacade.cleanText(tokens[INDEX_SET_ALIAS]);
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeAlias(INPUT, alias, target);
	}

	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		DefaultTokenController.getInstance().init();
		while (true) {
			try {
			ParserFacade.printCmd(new AliasCommand(in.nextLine()).getParams());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

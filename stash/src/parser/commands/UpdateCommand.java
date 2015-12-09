//@@author A0131891E
package parser.commands;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Task.DataType;
import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.TernaryCommand;
import parser.commands.abstracted.VariadicCommand;
import parser.tokens.DefaultTokenController;

/**
 * 3-ary Update Command
 * @author Leow Yijin
 */
public class UpdateCommand extends TernaryCommand implements ConcreteCommand {

	private static final String REGEX_FIELD_NAME = 
			DefaultTokenController.getInstance().getRegexNameField();
	private static final String REGEX_FIELD_START = 
			DefaultTokenController.getInstance().getRegexStartField();
	private static final String REGEX_FIELD_END = 
			DefaultTokenController.getInstance().getRegexEndField();
	
	private static final Pattern P_FIELD_NAME = Pattern.compile(REGEX_FIELD_NAME);
	private static final Pattern P_FIELD_START = Pattern.compile(REGEX_FIELD_START);
	private static final Pattern P_FIELD_END = Pattern.compile(REGEX_FIELD_END);
	
	public static final CmdType TYPE = CmdType.UPDATE;
    public static final String TOOLTIP_FORMAT_STRING = 
    		"Edit Task: [ %1$s \"task ID\" name|start|end \"new value\" ] OR [ %1$s name|start|end \"task ID\" \"new value\" ]";
	public static final String RESERVED_KEYWORD = "edit";
	private static final String[] PRESET_ALIASES = {
			"upd",
			"update",
			"set"
		};

	public static final ParseException FORMAT_EXCEPTION = 
			new ParseException(String.format(WARNING_MSG, RESERVED_KEYWORD), -1);

	private static final int INDEX_NEWVAL = 2;
	
	private DataType field;
	private Date newDate;
	private String newString;
	private int taskUID;
	
	public UpdateCommand(String input) {
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
	
	// TOKENISE (TernaryCommand's tokenise is sufficient)
	
	// PARSE AND VALIDATE TOKENS
	
	@Override
	protected void parseAndValidateArgs() throws ParseException {
		if (tokens.length != 3) { // superclass tokeniser could not split into 3
			throw FORMAT_EXCEPTION;
		}

		try { // Try to parse as <id> <field> <newval>
			tryFieldAndID(1, 0);
		} catch (ParseException e) {
		}
		// Now try to parse as <field> <id> <newval>
		tryFieldAndID(0, 1);
		
		parseNewValue();
	}
	
	// order of args: first arg = guess index of field, next arg = guess index of UID
	private void tryFieldAndID(int fieldGuess, int uidGuess) throws ParseException {
		try {
			taskUID = Integer.parseInt(tokens[uidGuess]);
			field = DefaultTokenController.getInstance().getFieldType(tokens[fieldGuess]);
			
			if (field == null) { // id parsed, but field cannot match
				throw FORMAT_EXCEPTION;
			}
		} catch (NumberFormatException e) {
			throw FORMAT_EXCEPTION;
		}
	}
	
	private void parseNewValue() {
		switch (field) {
			case NAME :
				
		}
	}
	
	@Override
	protected CommandParams prepareCmdParams() {
		return makeUp(INPUT, alias, target);
	}

	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		DefaultTokenController.getInstance().init();
		while (true) {
			try {
			ParserFacade.printCmd(new UpdateCommand(in.nextLine()).getParams());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

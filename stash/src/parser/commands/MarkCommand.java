//@@author A0131891E
package parser.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.UnaryIdCommand;
import parser.tokens.DefaultTokenController;

/**
 * 1-ary Mark Command
 * @author Leow Yijin
 */
public class MarkCommand extends UnaryIdCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.MARK;
    public static final String TOOLTIP_FORMAT_STRING = "Marking as Completed: [ %1$s \"task ID number\" ]";
	public static final String RESERVED_KEYWORD = "mark";
	private static final String[] PRESET_ALIASES = {
			"done",
			"finish",
			"complete"
		};
	
	public static final ParseException FORMAT_EXCEPTION = 
			new ParseException(String.format(WARNING_MSG, RESERVED_KEYWORD), -1);

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
	
	public MarkCommand(String input) {
		super(input);
	}
	
	@Override
	protected void parseAndValidateArgs() throws ParseException {
		super.parseArgs(FORMAT_EXCEPTION);
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeMark(INPUT, taskUID);
	}

	public static void main(String[] args) throws Exception {
		ParserFacade.printCmd(new MarkCommand("mark 59").getParams());
	}

}

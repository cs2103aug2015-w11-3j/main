//@@author A0131891E
package parser.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.NullaryCommand;

/**
 * 0-ary Quit command.
 * @author Leow Yijin
 */
public class QuitCommand extends NullaryCommand implements ConcreteCommand {
	
	public static final CmdType TYPE = CmdType.QUIT;
    public static final String TOOLTIP_FORMAT_STRING = "Quit Celebi :( [ %1$s ]";
    public static final String RESERVED_KEYWORD = "exit";
	
	private static final String[] PRESET_ALIASES = {
			"q",	
			"quit"
		};

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
	
	public QuitCommand(String input) {
		super(input);
	}
	
	@Override
	protected CommandParams prepareCmdParams() {
		return makeQuit(INPUT);
	}

	public static void main(String[] args) throws Exception {
		ParserFacade.printCmd(new QuitCommand("q").getParams());
	}
}

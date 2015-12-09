//@@author A0131891E
package parser.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.NullaryCommand;

/**
 * 0-ary Help command.
 * @author Leow Yijin
 */
public class HelpCommand extends NullaryCommand implements ConcreteCommand {
	
	public static final CmdType TYPE = CmdType.HELP;
	public static final String TOOLTIP_FORMAT_STRING = "Get Help! [ %1$s ] ";
	public static final String RESERVED_KEYWORD = "help";
	private static final String[] PRESET_ALIASES = {
			"?",
			"-h"
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
	
	public HelpCommand(String input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeHelp(INPUT);
	}

	
	public static void main(String[] args) {
	}
}

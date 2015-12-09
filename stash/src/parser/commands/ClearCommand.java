//@@author A0131891E
package parser.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.NullaryCommand;

/**
 * 0-ary Clear command.
 * @author Leow Yijin
 */
public class ClearCommand extends NullaryCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.CLEAR_FILTERS;
    public static final String TOOLTIP_FORMAT_STRING = "Clear Current Search/Filters: [ %1$s ]";
	public static final String RESERVED_KEYWORD = "clear";
	private static final String[] PRESET_ALIASES = {
			"clr",
			"cls",
			"reset"
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
	
	public ClearCommand(String input) {
		super(input);
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeClear(INPUT);
	}
	
	public static void main(String[] args) {
	}
}

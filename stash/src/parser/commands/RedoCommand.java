//@@author A0131891E
package parser.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.NullaryCommand;

/**
 * 0-ary Redo command.
 * @author Leow Yijin
 */
public class RedoCommand extends NullaryCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.REDO;
	public static final String TOOLTIP_FORMAT_STRING = "Redo Previously Undone Action: [ %1$s ]";
	public static final String RESERVED_KEYWORD = "redo";
	private static final String[] PRESET_ALIASES = {
			"re" 
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
	
	public RedoCommand(String input) {
		super(input);
	}

	
	@Override
	protected CommandParams prepareCmdParams() {
		return makeRedo(INPUT);
	}

	public static void main(String[] args) {
	}
}

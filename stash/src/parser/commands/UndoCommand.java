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
 * 0-ary Undo command.
 * @author Leow Yijin
 */
public class UndoCommand extends NullaryCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.UNDO;
	public static final String TOOLTIP_FORMAT_STRING = "Undo Previous Changes To Tasks: [ %1$s ]";
	public static final String RESERVED_KEYWORD = "undo";
	private static final String[] PRESET_ALIASES = {
			"u",
			"un"
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
	
	public UndoCommand(String input) {
		super(input);
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeUndo(INPUT);
	}

	public static void main(String[] args) {
	}
}

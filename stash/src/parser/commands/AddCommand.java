package parser.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.VariadicCommand;

/**
 * Variadic Add Command.
 * 
 * Dispatches input to correct N-ary add subcommands
 * based on input string format.
 * 
 * @author Leow Yijin
 */
public class AddCommand extends VariadicCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.ADD;
    public static final String TOOLTIP_FORMAT_STRING = "Searching in Names: [ %1$s \"words to find in task names\" ]";
	public static final String RESERVED_KEYWORD = "add";
	protected static final String[] PRESET_ALIASES = {
			"a",
			"new",
			"create"
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
	
	public AddCommand(String input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parseAndValidateArgs() throws ParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected CommandParams prepareCmdParams() {
		// TODO Auto-generated method stub
		return null;
	}

}

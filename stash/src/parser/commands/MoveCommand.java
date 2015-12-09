//@@author A0131891E
package parser.commands;

import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.UnaryCommand;

/**
 * 1-ary Move Command
 * @author Leow Yijin
 */
public class MoveCommand extends UnaryCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.MOVE;
    public static final String TOOLTIP_FORMAT_STRING = "Change Save File Location: [ %1$s \"new save file path\" ]";
	public static final String RESERVED_KEYWORD = "move";
	private static final String[] PRESET_ALIASES = {
			"mv",
			"changeloc"
		};
	
	public static final ParseException FORMAT_EXCEPTION = 
			new ParseException(String.format(WARNING_MSG, RESERVED_KEYWORD), -1);
	
	private Path newPath;

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
	
	public MoveCommand(String input) {
		super(input);
	}

	@Override
	protected void parseAndValidateArgs() throws ParseException {
		super.parseArgs(FORMAT_EXCEPTION);
		String token = tokens[0];
		try {
			newPath = ParserFacade.parsePath(token);
		} catch (ParseException e) {
			e.printStackTrace();
			throw FORMAT_EXCEPTION;
		}
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeMove(INPUT, newPath);
	}

	public static void main(String[] args) throws Exception {
		ParserFacade.printCmd(new MoveCommand("move bin/potato/").getParams());
	}
}

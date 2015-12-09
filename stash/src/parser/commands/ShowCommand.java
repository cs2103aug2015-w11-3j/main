//@@author A0131891E
package parser.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import common.TasksBag.ViewType;
import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.UnaryCommand;
import parser.tokens.DefaultTokenController;

/**
 * 1-ary Show Command
 * @author Leow Yijin
 */
public class ShowCommand extends UnaryCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.SHOW;
    public static final String TOOLTIP_FORMAT_STRING = 
    		"Changing View Tab: [ %1$s default ]   [ %1$s done ]   [ %1$s unfinished ]";
	public static final String RESERVED_KEYWORD = "show";
	private static final String[] PRESET_ALIASES = {
			"view",
			"display"
		};
	
	public static final ParseException FORMAT_EXCEPTION = 
			new ParseException(String.format(WARNING_MSG, RESERVED_KEYWORD), -1);
	
	private ViewType viewType;

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
	
	public ShowCommand(String input) {
		super(input);
	}

	@Override
	protected void parseAndValidateArgs() throws ParseException {
		super.parseArgs(FORMAT_EXCEPTION);
		String token = tokens[0];
		token = ParserFacade.cleanText(token);
		viewType = DefaultTokenController.getInstance().getViewType(token);
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeShow(INPUT, viewType);
	}

	public static void main(String[] args) throws Exception {
		ParserFacade.printCmd(new ShowCommand("show undone").getParams());
	}

}

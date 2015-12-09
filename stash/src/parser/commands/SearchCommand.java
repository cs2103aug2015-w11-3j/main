//@@author A0131891E
package parser.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.UnaryTextCommand;

/**
 * 1-ary Search Command
 * @author Leow Yijin
 */
public class SearchCommand extends UnaryTextCommand implements ConcreteCommand {

	public static final CmdType TYPE = CmdType.SEARCH;
    public static final String TOOLTIP_FORMAT_STRING = "Searching in Names: [ %1$s \"words to find in task names\" ]";
	public static final String RESERVED_KEYWORD = "search";
	private static final String[] PRESET_ALIASES = {
			"s",
			"find"
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
	
	public SearchCommand(String input) {
		super(input);
	}

	@Override
	protected void parseAndValidateArgs() throws ParseException {
		super.parseArgs(FORMAT_EXCEPTION);
	}

	@Override
	protected CommandParams prepareCmdParams() {
		return makeSearch(INPUT, text);
	}

	public static void main(String[] args) throws Exception {
		ParserFacade.printCmd(new SearchCommand("s dasdads.,vsmmo,  59").getParams());
	}

}

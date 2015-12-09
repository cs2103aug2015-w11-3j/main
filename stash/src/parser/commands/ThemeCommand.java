//@@author A0131891E
package parser.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import parser.ParserFacade;
import parser.commands.CommandParams.CmdType;
import parser.commands.abstracted.ConcreteCommand;
import parser.commands.abstracted.UnaryCommand;
import parser.tokens.DefaultTokenController;
import ui.view.CelebiViewController.Skin;

/**
 * 1-ary Theme Command
 * @author Leow Yijin
 */
public class ThemeCommand extends UnaryCommand implements ConcreteCommand {
	
	public static final CmdType TYPE = CmdType.THEME;

    public static final String TOOLTIP_FORMAT_STRING = "Change Colour Theme: [ %1$s day|night ]";
	public static final String RESERVED_KEYWORD = "theme";
	private static final String[] PRESET_ALIASES = {
			"skin",
			"color",
			"colour"
		};

	public static final ParseException FORMAT_EXCEPTION = 
			new ParseException(String.format(WARNING_MSG, RESERVED_KEYWORD), -1);
	
	private Skin newSkin;

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
	
	public ThemeCommand(String input) {
		super(input);
	}

	@Override
	protected void parseAndValidateArgs() throws ParseException {
		parseArgs(FORMAT_EXCEPTION);
		
		String token = tokens[0];
		token = ParserFacade.cleanText(token);
		
		newSkin = DefaultTokenController.getInstance().getSkin(token);
		if (newSkin == null) {
			throw FORMAT_EXCEPTION;
		}
	}


	@Override
	protected CommandParams prepareCmdParams() {
		return makeTheme(INPUT, newSkin);
	}

	public static void main(String[] args) throws Exception {
		ParserFacade.printCmd(new ThemeCommand("skin ay").getParams());
	}

}

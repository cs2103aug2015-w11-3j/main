//@@author A0131891E
package parser.tokens;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import common.Configuration;
import common.Task.DataType;
import common.TasksBag.ViewType;
import parser.ParserFacade;
import parser.commands.*;
import parser.commands.abstracted.ConcreteCommand;
import ui.view.CelebiViewController.Skin;


/**
 * Implements operations defined in TokenController interface.
 * 
 * Uses Maps for keyword mapping and checking.
 * Uses the common.config.CustomAliases class methods to change user alias mappings.
 * 
 * Also a singleton @see getInstance
 */
public class DefaultTokenController implements TokenController {

	public static final String CMD_CLASS_RESERVED_FIELD_NAME = "RESERVED_KEYWORD";
	public static final String CMD_CLASS_CMDTYPE_FIELD_NAME = "TYPE";
	public static final String CMD_CLASS_TOOLTIP_TEXT_FIELD_NAME = "TOOLTIP_FORMAT_STRING";
	public static final String CMD_CLASS_GET_DEF_KEYS_METHOD_NAME = "getDefaultKeywords";
	
	// singleton instance
	private static DefaultTokenController instance;

	private static ConcreteCommand[] ALL_COMMANDS = {
		// 0-ary
		new QuitCommand("PLACEHOLDER"),
		new HelpCommand("PLACEHOLDER"),
		new UndoCommand("PLACEHOLDER"),
		new RedoCommand("PLACEHOLDER"),
		new ClearCommand("PLACEHOLDER"),
		// 1-ary
		new ThemeCommand("PLACEHOLDER"),
		new DeleteCommand("PLACEHOLDER"),
		new MarkCommand("PLACEHOLDER"),
		new UnmarkCommand("PLACEHOLDER"),
		new ShowCommand("PLACEHOLDER"),
		new SearchCommand("PLACEHOLDER"),
		new MoveCommand("PLACEHOLDER"),
		// 2-ary
		new AliasCommand("PLACEHOLDER"),
		// 3-ary
		new UpdateCommand("PLACEHOLDER"),
		// variadic
		new AddCommand("PLACEHOLDER"),
		new FilterCommand("PLACEHOLDER")
	};
	
	// Stores the default keyword->CmdType mappings.
	private final Map<String, CmdType> DEFAULT_CMD_KEYWORDS;
	// Stores all reserved keyword->CmdType mappings.
	private final Map<String, CmdType> RESERVED_CMD_KEYWORDS;
	// Stores all CmdType-> help tooltip format string mappings.
	private final Map<CmdType, String> HELP_FORMAT_STRINGS;
	
	// Other keyword->identifier maps.
	private final Map<String, Skin> THEME_KEYWORDS;
	private final Map<String, ViewType> VIEW_KEYWORDS;
	
	private boolean initialised;
	
	private DefaultTokenController() {
		
		RESERVED_CMD_KEYWORDS = new LinkedHashMap<>();
		DEFAULT_CMD_KEYWORDS = new LinkedHashMap<>();
		THEME_KEYWORDS = new LinkedHashMap<>();
		VIEW_KEYWORDS = new LinkedHashMap<>();
		HELP_FORMAT_STRINGS = new LinkedHashMap<>();
		initialised = false;
	}

	public static TokenController getInstance() {
		if (instance == null) {
			instance = new DefaultTokenController();
		}
		instance.init();
		return instance;
	}

	public void init() {
		
		if (initialised) {
			return; 
		}
		
		Collection<String> keys;
		String reserved, helpStr;
		CmdType target;
		
		for(ConcreteCommand c : ALL_COMMANDS) {
			
			target = c.getCmdType();
			reserved = c.getReservedKeyword();
			
			// init reserved cmd key map
			RESERVED_CMD_KEYWORDS.put(reserved, target); 
			
			// init default cmd key map
			keys = c.getDefaultKeywords();
			mapAliasesToValue(DEFAULT_CMD_KEYWORDS, target, keys); 
			
			// init help tooltip map
			helpStr = c.getTooltipStr();
			HELP_FORMAT_STRINGS.put(target, helpStr);
			
		}
		
		mapSkinKeywords(); // init skin map
		mapViewKeywords(); // init view map
		initialised = true;
	}
	
	// initialises Skin keyword token map
	private void mapSkinKeywords() {
		mapAliasesToValue(THEME_KEYWORDS, Skin.DAY, Arrays.asList(THEME_DAY));
		mapAliasesToValue(THEME_KEYWORDS, Skin.NIGHT, Arrays.asList(THEME_NIGHT));
	}
	// initialises View keyword token map
	private void mapViewKeywords() {
		mapAliasesToValue(VIEW_KEYWORDS, ViewType.DEFAULT, Arrays.asList(VIEW_DEFAULT));
		mapAliasesToValue(VIEW_KEYWORDS, ViewType.COMPLETED, Arrays.asList(VIEW_COMPLETED));
		mapAliasesToValue(VIEW_KEYWORDS, ViewType.INCOMPLETE, Arrays.asList(VIEW_INCOMPLETE));
	}
	
	/**
	 * Maps every key in keys to value, inside map.
	 * If any duplicate is detected in the map, assertion kills program.
	 * Since this is used only for alias mapping, any detected duplicates
	 * imply an error on the dev's part in specifying the aliases above.
	 * @param map : mappings are stored here.
	 * @param value : to be mapped by all aliases
	 * @param aliases : to be mapped to value
	 */
	private static final <V> void mapAliasesToValue(Map<String, V> map, V value, Collection<String> aliases) {
		assert(map != null && aliases != null && value != null);
		for(String alias : aliases) {
		    V var = map.put(alias, value);
			assert var == null : "duplicate key binding " + alias ; // no duplicates allowed
		}
	}

	
	///////////////////////////////////////////////////////////
	// Superclass/interface method implementations
	///////////////////////////////////////////////////////////
	
	@Override
	public boolean isCmdKeyword(String alias) {
		return isReservedCmdKeyword(alias) || isCustomCmdAlias(alias) || isDefaultCmdKeyword(alias);
	}
	@Override
	public boolean isCustomCmdAlias(String alias) {
		assert(validAliasFormat(alias));
		alias = ParserFacade.cleanText(alias);
		return Configuration.getInstance().isUserAlias(alias);
	}
	@Override
	public boolean isDefaultCmdKeyword(String alias) {
		assert(validAliasFormat(alias));
		alias = ParserFacade.cleanText(alias);
		return DEFAULT_CMD_KEYWORDS.containsKey(alias);
	}
	@Override
	public boolean isReservedCmdKeyword(String alias) {
		assert(validAliasFormat(alias));
		alias = ParserFacade.cleanText(alias);
		return RESERVED_CMD_KEYWORDS.containsKey(alias);
	}
	
	@Override
	public CmdType getCmdType(String key) {
		assert(validAliasFormat(key));
		key = ParserFacade.cleanText(key);
		
		CmdType rtnCmd;
		
		// check reserved keywords first.
		rtnCmd = RESERVED_CMD_KEYWORDS.get(key);
		if (rtnCmd != null) {
			assert(rtnCmd != CmdType.INVALID);
			return rtnCmd;
		}
		
		// check custom user aliases next.
		final String cmdName = Configuration.getInstance().getUserAliasTargetName(key);
		try { 
			if(cmdName != null) {
				rtnCmd = Enum.valueOf(CmdType.class, cmdName);
				assert(rtnCmd != CmdType.INVALID);
				return rtnCmd;
			}
		} catch(IllegalArgumentException iae) {
		}
		
		// check default aliases last.
		rtnCmd = DEFAULT_CMD_KEYWORDS.get(key);
		assert(rtnCmd != CmdType.INVALID);
		return rtnCmd;
	}
	
	@Override
	public void setCustomAlias(String alias, CmdType target) throws IOException {
		assert(validAliasFormat(alias) && target != null && target != CmdType.INVALID);
		alias = ParserFacade.cleanText(alias);
		Configuration.getInstance().setUserAlias(alias, target.name());
	}
	
	@Override
	public void clearCustomAliases() throws IOException {
		Configuration.getInstance().clearUserAliases();
	}
	
	@Override
	public String getHelpTooltip(String firstToken) {
		assert(firstToken != null);
		final String cleanToken = ParserFacade.cleanText(firstToken);
		assert(cleanToken.split("\\s+").length == 1);
		
		final CmdType cmdType = getCmdType(cleanToken);
		final String formatStr = HELP_FORMAT_STRINGS.get(cmdType);
		if (cmdType == null || formatStr == null) {
			return null;
		}
		return String.format(formatStr, firstToken);
	}
	@Override
	public Skin getSkin(String key) {
		return THEME_KEYWORDS.get(key);
	}
	@Override
	public ViewType getViewType(String key) {
		key = ParserFacade.cleanText(key);
		return VIEW_KEYWORDS.get(key);
	}
	
	// for parsing task field identifiers
	
	@Override
	public DataType getFieldType(String token) {
		if (isNameFieldId(token)) {
			return DataType.NAME;
		}
		if (isStartFieldId(token)) {
			return DataType.DATE_START;
		}
		if (isEndFieldId(token)) {
			return DataType.DATE_END;
		}
		return null;
	}
	
	@Override
	public String getRegexNameField() {
		return regexContaining(FIELD_NAME);
	}
	@Override
	public String getRegexStartField() {
		return regexContaining(FIELD_START_DATE);
	}
	@Override
	public String getRegexEndField() {
		return regexContaining(FIELD_END_DATE);
	}
	
	@Override
	public boolean isNameFieldId(String token) {
		token = ParserFacade.cleanText(token);
		return Arrays.asList(FIELD_NAME).contains(token);
	}
	@Override
	public boolean isStartFieldId(String token) {
		token = ParserFacade.cleanText(token);
		return Arrays.asList(FIELD_START_DATE).contains(token);
	}
	@Override
	public boolean isEndFieldId(String token) {
		token = ParserFacade.cleanText(token);
		return Arrays.asList(FIELD_END_DATE).contains(token);
	}
	
	// for parsing special null/reset value
	@Override
	public boolean isResetValue(String token) {
		token = ParserFacade.cleanText(token);
		return Arrays.asList(RESET_VAL).contains(token);
	}
	
	// for parsing filter command special keywords
	@Override
	public String getRegexFilterBefore() {
		return regexContaining(FILTER_ARG_BEF);
	}
	@Override
	public String getRegexFilterAfter() {
		return regexContaining(FILTER_ARG_AFT);
	}
	@Override
	public String getRegexFilterBtwFront() {
		return regexContaining(FILTER_ARG_BTW_FRONT);
	}
	@Override
	public String getRegexFilterBtwDelim() {
		return regexContaining(FILTER_ARG_BTW_DELIM);
	}
	
	// REGEX GENERATOR: generates a regex string to match any String element in the tokens argument
	private static final String regexContaining(String[] tokens) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(?:");
		for (String tok : tokens) {
			sb.append("\\Q").append(tok).append("\\E"); // necessary escaping
			sb.append('|');
		}
		if (tokens.length > 0) { 
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(')');
		return sb.toString();
	}
	private static boolean validAliasFormat(String testee) {
		// not null and no whitespace
		return testee != null && testee.matches("\\S*+");
	}
	
	public static void main(String[] args) {
		DefaultTokenController.getInstance();
	}
}

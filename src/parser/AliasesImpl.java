//@@author A0131891E
package parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import common.Configuration;


public class AliasesImpl extends Aliases {

	private static AliasesImpl instance = null;
	public static Aliases getInstance () {
	    
		if (instance == null) {
		
		    System.out.println("iasdasdnit"); 
		    instance = new AliasesImpl();
		}
		return instance;
	}
	
	// Stores the default alias->command.type mapping
	private final Map<String, Command.Type> DEFAULT_CMD_ALIASES;
	private final Set<String> RESERVED_CMD_ALIASES;
	
	private AliasesImpl () {
		DEFAULT_CMD_ALIASES = new LinkedHashMap<>();
		mapCmdTypeAliases();
		RESERVED_CMD_ALIASES = new LinkedHashSet<>(Arrays.asList(CMD_RESERVED));
	}
	
	private void mapCmdTypeAliases () {
		mapAliases(CMD_ADD, Command.Type.ADD);
		mapAliases(CMD_UPD, Command.Type.UPDATE);
		mapAliases(CMD_DEL, Command.Type.DELETE);
		
		mapAliases(CMD_MARK, Command.Type.MARK);
		mapAliases(CMD_UNMARK, Command.Type.UNMARK);
		
		mapAliases(CMD_UNDO, Command.Type.UNDO);
		mapAliases(CMD_REDO, Command.Type.REDO);
		
		mapAliases(CMD_SHOW, Command.Type.SHOW);
		
		mapAliases(CMD_SEARCH, Command.Type.SEARCH);
		mapAliases(CMD_FILTER, Command.Type.FILTER_DATE);
		mapAliases(CMD_CLEAR, Command.Type.CLEAR_FILTERS);
		
		mapAliases(CMD_MOVE, Command.Type.MOVE);
		mapAliases(CMD_ALIAS, Command.Type.ALIAS);
		
		mapAliases(CMD_HELP, Command.Type.HELP);
		mapAliases(CMD_THEME, Command.Type.THEME);
		
		mapAliases(CMD_QUIT, Command.Type.QUIT);
	}
	
	private void mapAliases (String[] aliases, Command.Type cmdType) {
		mapAliasesToValue(DEFAULT_CMD_ALIASES, cmdType, Arrays.asList(aliases));
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
	private static final <V> void mapAliasesToValue (Map<String, V> map, V value, Collection<String> aliases) {
		assert(map != null && aliases != null && value != null);
		for (String alias : aliases) {
		    V var = map.put(alias, value);
			assert var == null : "duplicate key binding " + alias ; // no duplicates allowed
		}
	}

	// abstract class / interface methods
	
	@Override
	public boolean isCustomAlias (String alias) {
		assert(validAliasFormat(alias));
		alias = Parser.cleanText(alias);
		return Configuration.getInstance().isUserAlias(alias);
	}
	@Override
	public boolean isDefaultAlias (String alias) {
		assert(validAliasFormat(alias));
		alias = Parser.cleanText(alias);
		return DEFAULT_CMD_ALIASES.containsKey(alias);
	}
	@Override
	public boolean isReservedCmdAlias (String alias) {
		assert(validAliasFormat(alias));
		alias = Parser.cleanText(alias);
		return RESERVED_CMD_ALIASES.contains(alias);
	}
	
	@Override
	public Command.Type getCmdFromAlias (String alias) {
		assert(validAliasFormat(alias));
		alias = Parser.cleanText(alias);
		
		final String cmdName = Configuration.getInstance().getUserAliasTargetName(alias);
		Command.Type rtnCmd;
		
		// check custom user aliases first
		try { 
			// includes additional redundant check to make sure reserved aliases aren't overwritten
			if (cmdName != null && !RESERVED_CMD_ALIASES.contains(alias)) {
				rtnCmd = Enum.valueOf(Command.Type.class, cmdName);
				assert(rtnCmd != Command.Type.INVALID);
				return rtnCmd;
			}
		} catch (IllegalArgumentException iae) {
			;
		}
		// check default aliases now.
		rtnCmd = DEFAULT_CMD_ALIASES.get(alias);
		return rtnCmd;
	}
	
	@Override
	public void setCustomAlias (String alias, Command.Type target) throws IOException {
		assert(validAliasFormat(alias) && target != null && target != Command.Type.INVALID);
		alias = Parser.cleanText(alias);
		Configuration.getInstance().setUserAlias(alias, target.name());
	}
	@Override
	public void clearCustomAliases () throws IOException {
		Configuration.getInstance().clearUserAliases();
	}
	
	private static boolean validAliasFormat (String testee) {
		// not null and no whitespace
		return testee != null && testee.matches("\\S*+");
	}
}

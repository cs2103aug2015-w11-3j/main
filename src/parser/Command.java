//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;

import common.Task;
import common.TasksBag;
import static ui.view.CelebiViewController.Skin;

public abstract class Command {

	public static enum Type {
		ADD, DELETE, UPDATE, 
		SHOW,
		SEARCH, FILTER_DATE, CLEAR_FILTERS,
		UNDO, REDO,
		MARK, UNMARK,
		QUIT, 
		INVALID,
		MOVE, HELP, ALIAS,
		THEME
	}
	
	private final String _userInput;
	private final Type _cmdType;
	
	Command (Type type, String rawInput) {
		_userInput = rawInput;
		_cmdType = type;
	}
	
	///////////////////////////////////////////////////
	// Invalid Command handling
	///////////////////////////////////////////////////
	
	/** INVALID
	 * 
	 * Provides user's raw input string for if input string cannot be parsed into recognised command type
	 * @return String
	 */
	public String getRawUserInput () {
		return _userInput;
	}
	
	
	///////////////////////////////////////////////////
	// Identifiers
	///////////////////////////////////////////////////
	
	/** ALL
	 * Identifies the type of command for further processing
	 * @return Command.Type (enum), null if NA
	 */
	public Type getCmdType () {
		return _cmdType;
	}
	
	/** DELETE, UPDATE, MARK, UNMARK
	 * Identifies a specific Celebi task object by index on UI for further processing.
	 * Uses the index as shown on the UI to the user, Logic must perform UID->real ID mapping.
	 * @return int, -1 if not applicable
	 */
	public abstract int getTaskUID ();
	abstract void setTaskUID (int uid);
	
	/** UPDATE
	 * Identifies a specific field type within the Celebi object for field-level processing.
	 * @return Task.DataType (enum), null if NA
	 */
	public abstract Task.DataType getTaskField ();
	abstract void setTaskField (Task.DataType field);
	
	/** HELP, ALIAS
	 * Identifies a command type for command type specific operations
	 * Will be null if the user is requesting the general help (to list all cmds)
	 * @return Command.Type (enum), or null if general help requested
	 */
	public abstract Type getSecondaryCmdType ();
	abstract void setSecondaryCmdType (Type type);
	
	/** SHOW
	 * Identifies which of the 3 view tabs the user wishes to switch to.
	 * @return TasksBag.ViewType for setting view
	 */
	public abstract TasksBag.ViewType getViewType ();
	abstract void setViewType (TasksBag.ViewType tab);
	
	/** THEME
	 * Identifies which theme the user wishes to switch to
	 * @return CelebiViewController.Skin
	 */
	public abstract Skin getTheme ();
	abstract void setTheme (Skin theme);
	
	///////////////////////////////////////////////////
	// User defined data getters
	//
	// NULL values will be returned if those fields are not applicable.
	// Mutable values will be cloned; internal fields are always safe from mutation.
	///////////////////////////////////////////////////
	
	/**
	 * 
	 * @return String data
	 */
	public abstract String getText ();
	abstract void setText (String text);
	
	// also represents start of date range for filtering by date
	public abstract Date getStart ();
	abstract void setStart (Date start);
	
	// also represents end of date range for filtering by date
	public abstract Date getEnd ();
	abstract void setEnd (Date end);
	
	// used for changing save location
	public abstract Path getPath();
	abstract void setPath (Path p);
	
	// For search {{ use getText }}
	//public String getSearchKeyword ();
}

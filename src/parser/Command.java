//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;

import common.Task;
import common.TasksBag;
import static ui.view.CelebiViewController.Skin;

/**
 * Base abstract class for the command datatype object.
 * Will be returned to Logic as result of parseCommand(String)
 * 
 * Only full user input string and command type are stored here (immutable)
 * Other fields have to be implemented by subclasses.
 * Their setters and getters have to be implemented as well.
 *
 * This allows developers to easily swap in/out any setting/getting
 * logic like validation, protection (clone object when getting) etc...
 * 
 * Subclasses also have the choice of storing their fields differently
 * Eg: Start and end dates can be implemented as a calendar,
 * Text as a suffix tree etc.
 * As long as it is converted back into String/Date/whatever as per
 * the getter contracts here.
 */
public abstract class Command {

	// Uniquely identifies which Action to create in Logic
	public static enum Type {
		ADD, DELETE, UPDATE, 
		SHOW,
		SEARCH, FILTER_DATE, CLEAR_FILTERS,
		UNDO, REDO,
		MARK, UNMARK,
		QUIT, 
		MOVE, HELP, 
		ALIAS, THEME,
		INVALID
	}
	
	private final String _userInput;
	private final Type _cmdType;
	
	Command (Type type, String rawInput) {
		_userInput = rawInput;
		_cmdType = type;
	}
	
	
	/** INVALID
	 * Provides user's raw input string for if input string cannot be parsed into recognised command type
	 * @return String
	 */
	public final String getRawUserInput () {
		return _userInput;
	}
	
	
	///////////////////////////////////////////////////
	// Identifiers (bounded discrete values)
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
	
	
	///////////////////////////////////////////////////////////////////
	// User defined data values (unbounded values)
	//
	// NULL values will be returned if those fields are not applicable.
	// Advisable for implementations to protect inner fields from mutation.
	///////////////////////////////////////////////////////////////////
	
	/** ADD, UPDATE, SEARCH, ALIAS
	 * Each of the above potentially need a string to work with
	 * @return String names or search strings or new alias
	 */
	public abstract String getText ();
	abstract void setText (String text);
	
	/** ADD, UPDATE, FILTER
	 * Each of the above potentially need a start date to work with	
	 * @return Date for starting in filter range or task
	 */
	public abstract Date getStart ();
	abstract void setStart (Date start);
	
	/** ADD, UPDATE, FILTER
	 * Each of the above potentially need an end date to work with	
	 * @return Date for ending in filter range or task
	 */
	public abstract Date getEnd ();
	abstract void setEnd (Date end);
	
	/** MOVE
	 * @return Path to shift task file to
	 */
	public abstract Path getPath ();
	abstract void setPath (Path p);
	
}

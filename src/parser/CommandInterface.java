//@@author A0131891E
package parser;

import common.Task;
import common.TasksBag;
import parser.Command.Type;
import ui.view.CelebiViewController;

import java.nio.file.Path;
import java.util.Date;

public interface CommandInterface {

	///////////////////////////////////////////////////
	// Invalid Command handling
	///////////////////////////////////////////////////
	
	/** INVALID
	 * 
	 * Provides user's raw input string for if input string cannot be parsed into recognised command type
	 * @return String
	 */
	public String getRawUserInput ();
	
	///////////////////////////////////////////////////
	// Identifiers
	///////////////////////////////////////////////////
	
	/** ALL
	 * Identifies the type of command for further processing
	 * @return Command.Type (enum), null if NA
	 */
	public Command.Type getCmdType ();
	/** DELETE, UPDATE, MARK, UNMARK
	 * Identifies a specific Celebi task object by index on UI for further processing.
	 * Uses the index as shown on the UI to the user, Logic must perform UID->real ID mapping.
	 * @return int, -1 if not applicable
	 */
	public int getTaskUID ();
	/** UPDATE
	 * Identifies a specific field type within the Celebi object for field-level processing.
	 * @return Task.DataType (enum), null if NA
	 */
	public Task.DataType getTaskField ();
	/** HELP
	 * Identifies the command type the user is requesting help for.
	 * Will be null if the user is requesting the general help (to list all cmds)
	 * @return Command.Type (enum), or null if general help requested
	 */
	public Type getHelpCmdType ();
	
	public TasksBag.ViewType getViewType ();
	
	public CelebiViewController.Skin getTheme ();
	
	///////////////////////////////////////////////////
	// Celebi field value getters
	// Provides user-defined values corresponding to Celebi fields
	//
	// NULL values will be returned if those fields are not applicable.
	// Mutable values will be cloned; internal fields are always safe from mutation.
	///////////////////////////////////////////////////
	
	// represents name or substring to search for
	public String getText ();
	// also represents start of date range for filtering by date
	public Date getStart ();
	// also represents end of date range for filtering by date
	public Date getEnd ();
	// used for chaning save location
	public Path getPath();
	
	// For search {{ use getText }}
	//public String getSearchKeyword ();
}

package parser;

import common.Task;
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
	 * 
	 * Identifies the type of command for further processing
	 * @return Command.Type (enum), null if NA
	 */
	public Command.Type getCmdType ();
	
	/** DELETE, UPDATE
	 * 
	 * Identifies a specific Celebi task object by index on UI for further processing.
	 * Uses the index as shown on the UI to the user, Logic must perform UID->real ID mapping.
	 * @return Integer, -1 if not applicable
	 */
	public int getTaskUID ();
	
	/** UPDATE
	 * 
	 * Identifies a specific field type within the Celebi object for field-level processing.
	 * @return Celebi.DataType (enum), null if NA
	 */
	public Task.DataType getTaskField ();
	
	///////////////////////////////////////////////////
	// Celebi field value getters
	// Provides user-defined values corresponding to Celebi fields
	//
	// NULL values will be returned if those fields are not applicable.
	// Mutable values will be cloned; internal fields are always safe from mutation.
	///////////////////////////////////////////////////
	public String getName ();
	public Date getStart ();
	public Date getEnd ();
	
	// For search
	public String getSearchKeyword ();
}

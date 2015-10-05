package parser;

import common.Celebi;
import java.util.Date;

public interface CommandInterface {

	///////////////////////////////////////////////////
	// Invalid Command handling
	///////////////////////////////////////////////////
	
	/** INVALID
	 * 
	 * Provides user's raw input string for if input string cannot be parsed into recognised command type
	 * 
	 * @return String
	 */
	public String getRawUserInput ();
	
	///////////////////////////////////////////////////
	// Identifiers
	///////////////////////////////////////////////////
	
	/** ALL
	 * 
	 * Identifies the type of command for further processing
	 * @return Command.Type (enum)
	 */
	public Command.Type getCmdType ();
	
	/** DELETE, UPDATE
	 * 
	 * Identifies a specific Celebi task object by index on UI for further processing.
	 * Uses the index as shown on the UI to the user, Logic must perform UID->real ID mapping.
	 * 
	 * @return Integer
	 */
	public int getCelebiUID ();
	
	/** UPDATE
	 * 
	 * Identifies a specific field type within the Celebi object for field-level processing.
	 * 
	 * @return Celebi.DataType (enum)
	 */
	public Celebi.DataType getCelebiField ();
	
	///////////////////////////////////////////////////
	// Celebi field value getters
	// Provides user-defined values corresponding to Celebi fields
	///////////////////////////////////////////////////
	
	public String getName ();
	public Date getStart ();
	public Date getEnd ();
	
}

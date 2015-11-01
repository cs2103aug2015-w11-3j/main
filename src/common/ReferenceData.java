//@@author A0131891E
package common;

import parser.Parser;
import static common.Utilities.*;
/**
 * Global canonical reference for all immutable data that need to be shared among multiple components.
 * The purpose is to reduce unnecessary coupling between components that need to reference each other's data.
 * All data here is defined by developers beforehand (equivalent to C's #define).
 * Since the data is immutable, it is thread safe.
 * 
 * FOR USERS:
 * When your class has to use data from this class, call the getter ONCE, storing the result for personal use. 
 * This is to avoid the overhead of unnecessary deep copying from repeatedly calling the getters.
 * 
 * FOR DEVS:
 * All non-primitive data should be retrieved via the getter methods to prevent mutation. 
 * All getters should return a deep clone to preserve immutability.
 */
public final class ReferenceData {
	// Disables constructor
	private ReferenceData() {
	}
	
	// Currently used by: UI (for cmd token syntax highlighting)
	// A deep copy of Parser's TOKENS is archived here. 
	// Using a deep copy means that any runtime changes to Parser's TOKENS
	// will not affect the canonical copy here
	private static final String[][] CMD_TOKENS = Parser.getAllCmdTokens();
	public static final String[][] getValidCmdTokens () {
		return str2dArrayClone(CMD_TOKENS);
	}
}

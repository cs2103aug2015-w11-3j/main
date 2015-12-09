//@@A0131891E
package parser;

import java.text.ParseException;

public interface Parser<T> {
	T parse(String token) throws ParseException;
	// Provide option for cheaper lenient check to save resources.
	// Does not guarantee parseability.
	boolean isPossiblyValid(String token); 
}

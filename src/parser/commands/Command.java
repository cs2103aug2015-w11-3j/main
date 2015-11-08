//@@author A0131891E
package parser.commands;

public abstract class Command implements CommandDataParser {

	final String INPUT;
	CommandData commandData;
	
	public Command(String input) {
		INPUT = input;
	}

}

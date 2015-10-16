package parser;

import java.util.Date;
import common.Task;

public interface ParserInterface {
	public void init();
	public Command parseCommand(String s);
	
	// Manually create commands for logic testing.

	public Command makeAdd (String name, Date start, Date end);
	public Command makeUpdate (int taskUID, Task.DataType fieldType, Object newValue);
	public Command makeDelete (int taskUID);
	public Command makeSort();
	public Command makeQuit ();
	public Command makeInvalid ();
	public Command makeType(Command.Type type);
}

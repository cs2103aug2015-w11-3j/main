package parser;

import java.util.Date;
import common.Celebi;

public interface ParserInterface {
	public void init();
	public Command parseCommand(String s);
	
	// Manually create 

	public Command makeAdd (String name, Date start, Date end);
	public Command makeUpdate (int taskUID, Celebi.DataType fieldType, Object newValue);
	public Command makeDelete (int taskUID);
	public Command makeQuit ();
	public Command makeInvalid ();
	
}

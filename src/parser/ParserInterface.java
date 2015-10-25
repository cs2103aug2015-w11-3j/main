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
	//public Command makeSort();
	public Command makeQuit ();
	public Command makeInvalid ();
	public Command makeType(Command.Type type);
	
	
	// to debug command
	public static void printCmd (Command c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getName());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
	}
}

//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;

import static ui.view.CelebiViewController.Skin;
import common.TasksBag;

public interface ParserInterface {
	
	// Used in actual program execution flow
	public void init();
	public Command parseCommand(String s);
	
	// Manually create commands for logic testing.
	public Command makeAdd (String name, Date start, Date end);
	public Command makeUpdateName (int taskUID, String newName);
	public Command makeUpdateStart (int taskUID, Date newDate);
	public Command makeUpdateEnd (int taskUID, Date newDate);
	public Command makeDelete (int taskUID);
	public Command makeQuit ();
	public Command makeInvalid ();
	public Command makeShow (TasksBag.ViewType viewType);
	public Command makeRedo ();
	public Command makeUndo ();
	public Command makeMark (int taskUID);
	public Command makeUnmark (int taskUID);
	public Command makeSearch (String searchKey);
	public Command makeFilterDate (Date rangeStart, Date rangeEnd);
	public Command makeMove (Path newPath);
	public Command makeHelp (Command.Type helpTarget);
	public Command makeTheme (Skin theme);
	
	// to debug command
	public static void printCmd (Command c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getText());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
	}
}

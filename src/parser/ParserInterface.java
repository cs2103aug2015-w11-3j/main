//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;

import static ui.view.CelebiViewController.Skin;
import common.TasksBag;

public interface ParserInterface {
	
	// Used in actual program execution flow
	public void init();
	public CommandImpl parseCommand(String s);
	
	// Manually create commands for logic testing.
	public CommandImpl makeAdd (String name, Date start, Date end);
	public CommandImpl makeUpdateName (int taskUID, String newName);
	public CommandImpl makeUpdateStart (int taskUID, Date newDate);
	public CommandImpl makeUpdateEnd (int taskUID, Date newDate);
	public CommandImpl makeDelete (int taskUID);
	public CommandImpl makeQuit ();
	public CommandImpl makeInvalid ();
	public CommandImpl makeShow (TasksBag.ViewType viewType);
	public CommandImpl makeRedo ();
	public CommandImpl makeUndo ();
	public CommandImpl makeMark (int taskUID);
	public CommandImpl makeUnmark (int taskUID);
	public CommandImpl makeSearch (String searchKey);
	public CommandImpl makeFilterDate (Date rangeStart, Date rangeEnd);
	public CommandImpl makeClear ();
	public CommandImpl makeMove (Path newPath);
	public CommandImpl makeHelp (Command.Type helpTarget);
	public CommandImpl makeTheme (Skin theme);
	public CommandImpl makeAlias (String alias, Command.Type target);
	
	// to debug command
	public static void printCmd (CommandImpl c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getText());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
	}
}

//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;
import parser.commands.CommandDataParser;

import static ui.view.CelebiViewController.Skin;
import common.TasksBag;
import parser.commands.CommandData;

public interface ParserController extends CommandDataParser {
	
	// Used in actual program execution flow
	public void init();
	
	// Manually create commands for logic testing.
	public CommandData makeAdd (String name, Date start, Date end);
	public CommandData makeUpdateName (int taskUID, String newName);
	public CommandData makeUpdateStart (int taskUID, Date newDate);
	public CommandData makeUpdateEnd (int taskUID, Date newDate);
	public CommandData makeDelete (int taskUID);
	public CommandData makeQuit ();
	public CommandData makeInvalid ();
	public CommandData makeShow (TasksBag.ViewType viewType);
	public CommandData makeRedo ();
	public CommandData makeUndo ();
	public CommandData makeMark (int taskUID);
	public CommandData makeUnmark (int taskUID);
	public CommandData makeSearch (String searchKey);
	public CommandData makeFilterDate (Date rangeStart, Date rangeEnd);
	public CommandData makeClear ();
	public CommandData makeMove (Path newPath);
	public CommandData makeHelp (CommandData.Type helpTarget);
	public CommandData makeTheme (Skin theme);
	public CommandData makeAlias (String alias, CommandData.Type target);
	
	// to debug command
	public static void printCmd (CommandData c) {
		System.out.println("type: " + c.getCmdType());
		System.out.println("raw: " + c.getRawUserInput());
		System.out.println("uid: " + c.getTaskUID());
		System.out.println("fieldkey: " + c.getTaskField());
		System.out.println("name: " + c.getText());
		System.out.println("start: " + c.getStart());
		System.out.println("end: "+ c.getEnd());
	}
}

package parser;

//import com.sun.javafx.css.Combinator;
import common.Celebi;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.*;

public class Parser implements ParserInterface {

	// Patterns for user command matching for substring after 
	private static final Pattern P_ADD = Pattern.compile(
			"(?<>)");
	private static final Pattern P_DEL = Pattern.compile(
			"()");
	private static final Pattern P_UPD = Pattern.compile(
			"()");
	
	public Command makeAdd (String name, Date start, Date end) {
		Command cmd = new Command(Command.Type.Add, "test methods no raw user input");
		cmd.setEnd(end);
		cmd.setStart(start);
		cmd.setName(name);
		return cmd;
	}
	
	public Command makeUpdate (int taskUID, Celebi.DataType fieldType, Object newValue) {
		Command cmd = new Command(Command.Type.Update, "test methods no raw user input");
		cmd.setTaskField(fieldType);
		cmd.setTaskUID(taskUID);
		switch (fieldType) {
		case NAME : 
			cmd.setName((String) newValue);
			break;
		case DATE_START :
			cmd.setStart((Date) newValue);
			break;
		case DATE_END :
			cmd.setEnd((Date) newValue);
			break;
		default :
			throw new IllegalArgumentException("Allowed fields are name, start, and end dates.");
			// break;
		}
		return cmd;	
	}

	public Command makeDelete (int taskUID) {
		Command cmd = new Command(Command.Type.Delete, "test methods no raw user input");
		cmd.setTaskUID(taskUID);
		return cmd;
	}
	
	public Command makeQuit () {
		Command cmd = new Command(Command.Type.Quit, "test methods no raw user input");
		return cmd;
	}
	
	public Command makeInvalid () {
		Command cmd = new Command(Command.Type.Invalid, "test methods no raw user input");
		return cmd;
	}
	
	@Override
	public void init () {
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public Command parseCommand (String rawInput) {
		//String[] cmdAndDet
		Command.Type type = getCmdType(rawInput);
		return null;
	}
	
	private Command.Type getCmdType (String firstToken) {
		switch (firstToken.toLowerCase()) {
		
			case "a" :		// Fallthrough
			case "add" :	// Fallthrough
			case "new" :
				return Command.Type.Add;
				
			case "d" : 		// Fallthrough
			case "del" : 	// Fallthrough
			case "delete" :	// Fallthrough
			case "rm" :		// Fallthrough
			case "remove" :
				return Command.Type.Delete;
				
			case "u" :		// Fallthrough
			case "upd" :	// Fallthrough
			case "update" :	// Fallthrough
			case "edit" :
				return Command.Type.Update;
				
			case "q" :		// Fallthrough
			case "quit" :	// Fallthrough
			case "exit" :	
				return Command.Type.Quit;
				
			default :
				return Command.Type.Invalid;
		}
	}

//	private Celebi.DataType getField (String fieldToken) {
//		
//	}
}

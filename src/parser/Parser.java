package parser;

import com.sun.javafx.css.Combinator;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.regex.*;

public class Parser implements ParserInterface {

	// private static final 
	
	@Override
	public void init () {
		// TODO Auto-generated method stub
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public Command parseCommand (String rawInput) {
		Command.Type type = parseCmdType(rawInput);
		return null;
	}
	
	private Command.Type parseCmdType (String rawInput) {
		String firstToken = rawInput.substring(0, rawInput.indexOf(' ')).toLowerCase();
		switch (firstToken) {
		
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

}

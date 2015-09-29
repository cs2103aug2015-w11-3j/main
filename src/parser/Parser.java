package parser;

import com.sun.javafx.css.Combinator;

public class Parser implements ParserInterface {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public Command parseCommand(String s) {
		Command c;// = new Command(Command.Type.Add);
		
		switch(s){
			case "add":
				c = new Command(Command.Type.Add);
				break;
				
			case "quit":
				c = new Command(Command.Type.Quit);
				System.out.println("parser received quit");
				break;
				
			default:
				c = new Command(Command.Type.Invalid);
				break;
		}
		return c;
	}

}

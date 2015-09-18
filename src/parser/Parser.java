package parser;

public class Parser implements ParserInterface {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public ParsedCommand parseCommand(String s) {
		ParsedCommand c = new ParsedCommand();
		if (s.equals("quit")) {
			c.setCmd(ParsedCommand.Command.Quit);
			System.out.println("parser received quit");
		}
		return c;
	}

}

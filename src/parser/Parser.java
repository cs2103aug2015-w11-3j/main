package parser;

public class Parser implements ParserInterface {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public Command parseCommand(String s) {
//		Command c = new Command();
//		if (s.equals("quit")) {
//			c.setCmd(Command.Type.Quit);
//			System.out.println("parser received quit");
//		}
//		return c;
		return null;
	}

}

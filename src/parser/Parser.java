package parser;

import common.Celebi;
import common.Celebi.Command;

public class Parser implements ParserInterface {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("Parser Init");
		System.out.println("Parser Init complete");
	}

	@Override
	public Celebi parseCommand(String s) {
		Celebi c = new Celebi();
		if (s.equals("quit")) {
			c.setCmd(Command.Quit);
			System.out.println("parser received quit");
		}
		return c;
	}

	@Override
	public Celebi updateCelebi(Celebi c) {
		// TODO Auto-generated method stub
		return null;
	}

}

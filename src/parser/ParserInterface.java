package parser;

import common.Celebi;

public interface ParserInterface {
	public void init();
	public Celebi parseCommand(String s);
	public Celebi updateCelebi(Celebi c);	// Might be in logic DISCUSS
}

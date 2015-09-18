package parser;

public interface ParserInterface {
	public void init();
	public ParsedCommand parseCommand(String s);
}

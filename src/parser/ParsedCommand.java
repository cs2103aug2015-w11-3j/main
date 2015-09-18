package parser;

public class ParsedCommand {

	public enum Command {
		Add, Delete, Update, Sort, Search, Quit, None
	}

	private Command cmd;

	public ParsedCommand(){
		cmd = Command.None;
	}
	public Command getCmd() {
		return cmd;
	}

	public void setCmd(Command c) {
		cmd = c;
	}

}

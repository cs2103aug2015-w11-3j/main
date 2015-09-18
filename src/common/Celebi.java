package common;

/*
 * Our task object.
 * Stores data
 * 
 */
public class Celebi {
	public enum Command {
		Add, Delete, Update, Sort, Search, Quit, None
	}

	private Command cmd;

	public Celebi(){
		cmd = Command.None;
	}
	public Command getCmd() {
		return cmd;
	}

	public void setCmd(Command c) {
		cmd = c;
	}
}

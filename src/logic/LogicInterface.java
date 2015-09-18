package logic;

import common.CelebiBag;
import parser.ParsedCommand;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public ParsedCommand.Command executeCommand(String cmd);

	public CelebiBag getCelebiBag();

}

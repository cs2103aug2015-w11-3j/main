package logic;

import common.CelebiBag;
import parser.Command;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public Command executeCommand(String cmd) throws IntegrityCommandException;

	public CelebiBag getCelebiBag();

}

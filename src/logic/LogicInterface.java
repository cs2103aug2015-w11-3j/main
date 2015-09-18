package logic;

import common.Celebi;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public Celebi.Command executeCommand(String cmd);

}

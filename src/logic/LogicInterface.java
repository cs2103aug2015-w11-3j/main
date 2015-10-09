package logic;

import common.TasksBag;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public Feedback executeCommand(String cmd) throws IntegrityCommandException;

	public TasksBag getCelebiBag();

}

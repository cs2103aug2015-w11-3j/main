package logic;

import common.CelebiBag;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public Feedback executeCommand(String cmd) throws IntegrityCommandException;

	public CelebiBag getCelebiBag();

}

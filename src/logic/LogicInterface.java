package logic;

import common.Celebi;
import common.CelebiBag;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public Celebi.Command executeCommand(String cmd);

	public CelebiBag getCelebiBag();

}

package logic;

import common.TasksBag;
import logic.exceptions.LogicException;

public interface LogicInterface {

	public void init();

	public boolean initData(String s); // Returns true if successfully init data

	public Feedback executeCommand(String cmd) throws LogicException;

	public TasksBag getTaskBag();

}

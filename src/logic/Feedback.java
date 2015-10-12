package logic;

import common.TasksBag;
import parser.Command;

/*
 * Wrapper class to contain Command type and 
 * CelebiBag for UI
 */
public class Feedback {
	private final TasksBag cBag;
	private final Command cCommand;
	
	public Feedback(Command comd, TasksBag bag){
		cBag = bag;
		cCommand = comd;
	}

	/*
	 * No setter, should never be set individually
	 */
	public TasksBag getcBag() {
		return cBag;
	}

	public Command getCommand() {
		return cCommand;
	}
	
	
	
}

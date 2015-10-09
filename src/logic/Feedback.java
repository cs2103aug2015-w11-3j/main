package logic;

import common.TasksBag;
import parser.Command;

/*
 * Wrapper class to contain Command type and 
 * CelebiBag for UI
 */
public class Feedback {
	TasksBag cBag;
	Command cCommand;
	
	/*
	 * No setter, should never be set individually
	 */
	public TasksBag getcBag() {
		return cBag;
	}

	public Command getCommand() {
		return cCommand;
	}
	
	
	public Feedback(Command comd, TasksBag bag){
		
		cBag = bag;
		cCommand = comd;
	}
}

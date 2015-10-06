package logic;

import common.CelebiBag;
import parser.Command;

/*
 * Wrapper class to contain Command type and 
 * CelebiBag for UI
 */
public class Feedback {
	CelebiBag cBag;
	Command cCommand;
	
	/*
	 * No setter, should never be set individually
	 */
	public CelebiBag getcBag() {
		return cBag;
	}

	public Command getCommand() {
		return cCommand;
	}
	
	
	public Feedback(Command comd, CelebiBag bag){
		
		cBag = bag;
		cCommand = comd;
	}
}

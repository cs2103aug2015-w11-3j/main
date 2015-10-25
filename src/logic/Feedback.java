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
	private String cMsg;
	
	public Feedback(Command comd, TasksBag bag){
		cBag = bag;
		cCommand = comd;
	}
	public Feedback(Command comd, TasksBag bag, String msg){
		this(comd, bag);
		cMsg = msg;
	}
	/*
	 * No setter, should never be set individually
	 */
	/**
	 * Provides the current sorted state of bag for UI
	 * @return sorted state of bag
	 */
	public TasksBag getcBag() {
		return cBag.getFiltered();
	}

	public Command getCommand() {
		return cCommand;
	}
	
	public String getMsg(){
		return cMsg;
	}
	
	public void setMsg(String msg){
		cMsg = msg;
	}
	
}

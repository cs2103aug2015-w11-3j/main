package logic;

import common.TasksBag;
import parser.Command;

public class AddAction implements UndoableAction{

	Command cCommand;
	TasksBag cBag;
	
	public AddAction(Command command, TasksBag bag){
		cCommand = command;
		cBag = bag;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

}

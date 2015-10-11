package logic;

import java.util.Date;

import common.Task;
import common.TasksBag;
import parser.Command;
import storage.Storage;
import storage.StorageInterface;

public class AddAction implements UndoableAction{

	Command cCommand;
	TasksBag cBag;
	StorageInterface cStore;
	Task cCreatedTask;
	
	public AddAction(Command command, TasksBag bag, StorageInterface stor){
		cCommand = command;
		cBag = bag;
		cStore = stor;
	}
	
	@Override
	public Feedback execute() {
		assert cCommand.getCmdType() == Command.Type.Add : cCommand.getCmdType();
		
		String name = cCommand.getName();
		Date startDate = cCommand.getStart();
		Date endDate = cCommand.getEnd();
		cCreatedTask = new Task(name, startDate, endDate);

		boolean addStatus = cStore.save(cCreatedTask);
		if(addStatus){	// Added successfully
			cBag.addTask(cCreatedTask);
		}else{
			// Throw?
		}
		Feedback fb = new Feedback(cCommand, cBag);
		return fb;
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		cStore.delete(cCreatedTask);
		System.out.println(cBag.removeTask(cCreatedTask));
	}

}

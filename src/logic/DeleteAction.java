package logic;

import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import parser.Command;
import storage.StorageInterface;

public class DeleteAction implements UndoableAction {
	
	Command cCommand;
	TasksBag cBag;
	StorageInterface cStore;
	Task cDeletedTask;
	boolean isSuccessful;
	
	public DeleteAction(Command command, TasksBag bag, StorageInterface stor){
		cCommand = command;
		cBag = bag;
		cStore = stor;
		isSuccessful = false;
	}
	
	@Override
	public Feedback execute() throws IntegrityCommandException {
		
		int UID = cCommand.getTaskUID();
		assert UID > 0 : UID;
		
		if(UID >= cBag.size()){
			throw new IntegrityCommandException("Given index out of bound");
		}
		
		cDeletedTask = cBag.getTask(UID);
		isSuccessful = cStore.delete(cDeletedTask);
		
		if(isSuccessful){
			cBag.removeTask(UID);
		}else{
			// Throw error?
		}
		
		Feedback fb = new Feedback(cCommand, cBag);
		return fb;
	}
	@Override
	public void undo() {
		// TODO Auto-generated method stub
		cBag.addTask(cDeletedTask);
		cStore.save(cDeletedTask);
	}
	@Override
	public void redo() {
		if(isSuccessful){
			cBag.removeTask(cDeletedTask);
			cStore.delete(cDeletedTask);
		}
	}
}

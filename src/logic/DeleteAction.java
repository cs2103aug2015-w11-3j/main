package logic;

import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import parser.Command;
import storage.StorageInterface;

public class DeleteAction implements UndoableAction {

	private Command cCommand;
	private TasksBag cBag;
	private StorageInterface cStore;
	private Task cDeletedTask;
	private boolean isSuccessful;
	private int cPosition;

	/**
	 * Returns a Delete Action object with the current status of the system
	 *
	 * @param command 	Command of the action
	 * @param bag		Current bag status
	 * @param stor		Storage pointer
	 */
	public DeleteAction(Command command, TasksBag bag, StorageInterface stor) {
		cCommand = command;
		cBag = bag;
		cStore = stor;
		isSuccessful = false;
	}

	/**
	 * Attempts to execute the action
	 * Returns Feedback of the action
	 * 
	 * @throws IntegrityCommandException	If deleting out of bound 
	 */
	@Override
	public Feedback execute() throws IntegrityCommandException {

		int UID = cCommand.getTaskUID();
		assert UID >= 0 : UID;

		if (UID >= cBag.size()) {
			throw new IntegrityCommandException("Given index out of bound");
		}

		cDeletedTask = cBag.getTask(UID);
		isSuccessful = cStore.delete(cDeletedTask);

		if (isSuccessful) {
			cPosition = cBag.removeTask(cDeletedTask);

		} else {
			// Throw error?
		}

		Feedback fb = new Feedback(cCommand, cBag);
		return fb;
	}

	/**
	 * Insert task back into bag at removed position
	 * Saves task back into storage
	 */
	@Override
	public void undo() {
		// TODO Auto-generated method stub
		cBag.addTask(cPosition, cDeletedTask);
		cStore.save(cDeletedTask);
	}

	/**
	 * Remove task object from bag
	 * Deletes task at storage
	 */
	@Override
	public void redo() {
		if (isSuccessful) {
			cBag.removeTask(cDeletedTask);
			cStore.delete(cDeletedTask);
		}
	}
}

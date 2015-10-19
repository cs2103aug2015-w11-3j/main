package logic;

import java.util.logging.Logger;

import common.Log;
import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import parser.Command;
import storage.StorageInterface;

public class DeleteAction implements UndoableAction {

	private static final String USR_MSG_DELETE_OOB = "Provided index not on list.";
	private static final String USR_MSG_DELETE_OK = "Removed!";
	private Command cCommand;
	private TasksBag cCurBag, cIntBag;
	private StorageInterface cStore;
	private Task cDeletedTask;
	private boolean isSuccessful;
	private int cPosition;		// Position of internal bag
	Logger log;
	/**
	 * Returns a Delete Action object in the internal bag.
	 * References the curBag to get the actual internalbag task
	 *
	 * @param command 	Command of the action
	 * @param curBag	Current bag status
	 * 		  internalBag Internal bag 
	 * @param stor		Storage pointer
	 */
	public DeleteAction(Command command, TasksBag internalBag, StorageInterface stor) {
		cCommand = command;
		cCurBag = internalBag.getSorted();
		cIntBag = internalBag;
		cStore = stor;
		isSuccessful = false;
		log = Logger.getLogger("DeleteAction");
	}

	/**
	 * Attempts to execute the action
	 * Returns Feedback of the action
	 * 
	 * @throws IntegrityCommandException	If deleting out of bound 
	 */
	@Override
	public Feedback execute() throws IntegrityCommandException {
		assert cCurBag != null;
		assert cIntBag != null;
		assert cStore != null;
		
		int UID = cCommand.getTaskUID();
		
		if( UID <= 0){
			throw new IntegrityCommandException(USR_MSG_DELETE_OOB);
		}

		if (UID > cCurBag.size()) {
			log.warning("Exceeded size" + UID + " " + cCurBag.size());
			throw new IntegrityCommandException(USR_MSG_DELETE_OOB);
		}

		cDeletedTask = cCurBag.getTask(UID - 1);
		isSuccessful = cStore.delete(cDeletedTask);

		if (isSuccessful) {
			cPosition = cIntBag.removeTask(cDeletedTask);
		} else {
			// Throw error?
		}

		Feedback fb = new Feedback(cCommand, cIntBag, USR_MSG_DELETE_OK);
		return fb;
	}

	/**
	 * Insert task back into internal bag at removed position
	 * Saves task back into storage
	 */
	@Override
	public void undo() {
		cIntBag.addTask(cPosition, cDeletedTask);
		cStore.save(cDeletedTask);
	}

	/**
	 * Remove task object from bag
	 * Deletes task at storage
	 */
	@Override
	public void redo() throws IntegrityCommandException{
		if (isSuccessful) {
			execute();
		}
	}
}

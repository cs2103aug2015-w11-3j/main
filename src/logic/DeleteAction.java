package logic;

import java.util.logging.Logger;

import common.Log;
import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class DeleteAction implements UndoableAction {

    private static final String USR_MSG_DELETE_OOB = "Provided index not on list.";
    private static final String USR_MSG_DELETE_OK = "Removed %1$s!";
    private static final String USR_MSG_DELETE_UNDO = "Undoing delete %1$s";

    private Command cCommand;
    private TasksBag cCurBag;
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask; // The task to be modified
    private boolean isSuccessful;
    private int cPosition; // Position of internal bag
    Logger log;

    /**
     * Returns a Delete Action object in the internal bag. References the curBag
     * to get the actual internalbag task
     *
     * @param command
     *            Command of the action
     * @param curBag
     *            Current bag status internalBag Internal bag
     * @param stor
     *            Storage pointer
     * @throws IntegrityCommandException 
     *            When provided with an index that will access OOB values
     */
    public DeleteAction(Command command, TasksBag internalBag, StorageInterface stor) throws IntegrityCommandException{
        assert internalBag != null;
        assert stor != null;
        assert command != null;
        
        cCommand = command;
        cCurBag = internalBag.getFlitered();
        cIntBag = internalBag;
        cStore = stor;
        isSuccessful = false;
        log = Logger.getLogger("DeleteAction");
        
        // Find the offending command and lock it at init time
        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IntegrityCommandException(USR_MSG_DELETE_OOB);
        }

        if (UID > cCurBag.size()) {
            throw new IntegrityCommandException(USR_MSG_DELETE_OOB);
        }

        // UID - 1 to get array index
        UID -= 1;

        cWhichTask = cCurBag.getTask(UID);
    }

    /**
     * Attempts to execute the action Returns Feedback of the action
     * 
     * @throws IntegrityCommandException
     *             If deleting out of bound
     */
    @Override
    public Feedback execute() throws LogicException {
        String usrMsg;
        
        isSuccessful = cStore.delete(cWhichTask);

        if (isSuccessful) {
            // Used when adding back into task bag
            cPosition = cIntBag.removeTask(cWhichTask);
        } else {
            throw new LogicException("Storage failed to delete task");
        }
        usrMsg = String.format(USR_MSG_DELETE_OK, cWhichTask.getName());
        Feedback fb = new Feedback(cCommand, cIntBag, usrMsg);
        
        return fb;
    }

    /**
     * Insert task back into internal bag at removed position Saves task back
     * into storage
     */
    @Override
    public Feedback undo() {
        String usrMsg;
        usrMsg = String.format(USR_MSG_DELETE_UNDO, cWhichTask.getName());
        
        cIntBag.addTask(cPosition, cWhichTask);
        cStore.save(cWhichTask);
        return new Feedback(cCommand, cIntBag, usrMsg);
    }

    /**
     * Remove task object from bag Deletes task at storage
     */
    @Override
    public Feedback redo() throws LogicException {
        return execute();
    }
}

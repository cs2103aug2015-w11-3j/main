//@@author A0125546E
package logic;

import java.util.logging.Logger;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.IllegalAccessCommandException;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

/***
 * Note that due to execute being called again when redo-ing. The task to be
 * deleted has to be decided at INIT/Constructor time. Not at execution time.
 */
public class DeleteAction implements UndoableAction {

    private static final String USR_MSG_DELETE_OOB = "Provided index not on list.";
    private static final String USR_MSG_DELETE_OK = "Removed %1$s!";
    private static final String USR_MSG_DELETE_UNDO = "Undoing delete %1$s";

    private Command cCommand;
    private TasksBag cCurBag;
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask; // The task to be modified
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
     *             When provided with an index that will access OOB values
     * @throws IllegalAccessCommandException 
     */
    public DeleteAction(Command command, TasksBag internalBag, StorageInterface stor) throws IllegalAccessCommandException {
        assert internalBag != null;
        assert stor != null;
        assert command != null;

        cCommand = command;
        cCurBag = internalBag.getFiltered();
        cIntBag = internalBag;
        cStore = stor;
        log = Logger.getLogger("DeleteAction");

        // Find the offending command and lock it at init time
        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IllegalAccessCommandException(USR_MSG_DELETE_OOB);
        }

        if (UID > cCurBag.size()) {
            throw new IllegalAccessCommandException(USR_MSG_DELETE_OOB);
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
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        CommandFeedback fb;

        cPosition = cIntBag.removeTask(cWhichTask);
        cStore.delete(cWhichTask);

        formattedString = Utilities.formatString(USR_MSG_DELETE_OK, cWhichTask.getName());
        fb = new CommandFeedback(cCommand, cIntBag, formattedString);

        return fb;
    }

    /**
     * Insert task back into internal bag at removed position Saves task back
     * into storage
     */
    @Override
    public CommandFeedback undo() {
        String formattedString;
        formattedString = Utilities.formatString(USR_MSG_DELETE_UNDO, cWhichTask.getName());

        cIntBag.addTask(cPosition, cWhichTask);
        cStore.save(cWhichTask);

        return new CommandFeedback(cCommand, cIntBag, formattedString);
    }

    /**
     * Remove task object from bag Deletes task at storage
     */
    @Override
    public CommandFeedback redo() throws LogicException {
        return execute();
    }
}

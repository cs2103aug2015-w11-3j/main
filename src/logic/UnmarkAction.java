//@@author A0125546E
package logic;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.AlreadyUnmarkedException;
import logic.exceptions.IllegalAccessCommandException;
import logic.exceptions.LogicException;
import parser.commands.CommandData;
import storage.StorageInterface;

public class UnmarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private static final String USR_MSG_UNMARK_OK = "Unmarked %1$s!";
    private static final String USR_MSG_UNMARK_FAIL = "Already unmarked %1$s!";
    private static final String USR_MSG_UNMARK_UNDO = "Undo unmarked %1$s!";
    
    private static final String USR_MSG_UNMARK_WARNING_STORE_FAIL = "Fail to update the storage file!";

    private CommandData cCommand;
    private TasksBag cCurBag;
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;
    private boolean cUpdateSuccess;

    public UnmarkAction(CommandData command, TasksBag internalBag, StorageInterface stor) throws IllegalAccessCommandException {
        cCommand = command;
        cCurBag = internalBag.getFilteredView();
        cIntBag = internalBag;
        cStore = stor;

        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IllegalAccessCommandException(USR_MSG_INDEX_ERR);
        }

        if (UID > cCurBag.size()) {
            throw new IllegalAccessCommandException(USR_MSG_INDEX_ERR);
        }

        // UID - 1 to get array index
        UID -= 1;

        cWhichTask = cCurBag.getTask(UID);
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        String warningString = "";

        // Should not unmark again if it is already unmarked.
        // Does not go into undo queue if already unmarked.
        if (cWhichTask.isCompleted() == false) {
            formattedString = Utilities.formatString(USR_MSG_UNMARK_FAIL, cWhichTask.getName());
            throw new AlreadyUnmarkedException(formattedString);
        } else {
            cWhichTask.setComplete(false);
            cUpdateSuccess = cStore.save(cWhichTask);
        }
        
        if (!cUpdateSuccess) {
        	warningString = USR_MSG_UNMARK_WARNING_STORE_FAIL;
        }

        formattedString = Utilities.formatString(USR_MSG_UNMARK_OK, cWhichTask.getName());
        CommandFeedback fb = new CommandFeedback(cCommand, cIntBag, formattedString, warningString);

        return fb;
    }

    @Override
    public CommandFeedback undo() {
        assert cWhichTask != null;

        String warningString = "";
        
        cWhichTask.setComplete(true);
        cUpdateSuccess = cStore.save(cWhichTask);
        
        if (!cUpdateSuccess) {
        	warningString = USR_MSG_UNMARK_WARNING_STORE_FAIL;
        }

        String formattedString = Utilities.formatString(USR_MSG_UNMARK_UNDO, cWhichTask.getName());
        return new CommandFeedback(cCommand, cIntBag, formattedString, warningString);
    }

    @Override
    public CommandFeedback redo() throws LogicException {
        return execute();
    }
}

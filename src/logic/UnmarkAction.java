//@@author A0125546E
package logic;

import java.util.logging.Logger;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.AlreadyUnmarkedException;
import logic.exceptions.IllegalAccessCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;


public class UnmarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private static final String USR_MSG_UNMARK_OK = "Unmarked %1$s!";
    private static final String USR_MSG_UNMARK_FAIL = "Already unmarked %1$s!";
    private static final String USR_MSG_UNMARK_UNDO = "Undo unmarked %1$s!";

    private Command cCommand;
    private TasksBag cCurBag;
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;

    private Logger log;

    public UnmarkAction(Command command, TasksBag internalBag, StorageInterface stor) throws IllegalAccessCommandException {
        cCommand = command;
        cCurBag = internalBag.getFiltered();
        cIntBag = internalBag;
        cStore = stor;
        log = Logger.getLogger("UnmarkAction");

        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IllegalAccessCommandException(USR_MSG_INDEX_ERR);
        }

        if (UID > cCurBag.size()) {
            log.warning("Exceeded size" + UID + " " + cCurBag.size());
            throw new IllegalAccessCommandException(USR_MSG_INDEX_ERR);
        }

        // UID - 1 to get array index
        UID -= 1;

        cWhichTask = cCurBag.getTask(UID);
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        
        // Should not unmark again if it is already unmarked.
        // Does not go into undo queue if already unmarked.
        if (cWhichTask.isCompleted() == false) {
            formattedString = Utilities.formatString(USR_MSG_UNMARK_FAIL, cWhichTask.getName());
            throw new AlreadyUnmarkedException(formattedString);
        } else {
            cWhichTask.setComplete(false);
            cStore.save(cWhichTask);
        }
        
        formattedString = Utilities.formatString(USR_MSG_UNMARK_OK, cWhichTask.getName());
        CommandFeedback fb = new CommandFeedback(cCommand, cIntBag, formattedString);

        return fb;
    }

    @Override
    public CommandFeedback undo() {
        assert cWhichTask != null;

        cWhichTask.setComplete(true);
        cStore.save(cWhichTask);
        
        String formattedString = Utilities.formatString(USR_MSG_UNMARK_UNDO, cWhichTask.getName());
        return new CommandFeedback(cCommand, cIntBag, formattedString);
    }

    @Override
    public CommandFeedback redo() throws LogicException {
        return execute();
    }
}

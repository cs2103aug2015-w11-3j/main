package logic;

import java.util.logging.Logger;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.IntegrityCommandException;
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

    public UnmarkAction(Command command, TasksBag internalBag, StorageInterface stor) throws IntegrityCommandException {
        cCommand = command;
        cCurBag = internalBag.getFiltered();
        cIntBag = internalBag;
        cStore = stor;
        log = Logger.getLogger("UnmarkAction");

        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IntegrityCommandException(USR_MSG_INDEX_ERR);
        }

        if (UID > cCurBag.size()) {
            log.warning("Exceeded size" + UID + " " + cCurBag.size());
            throw new IntegrityCommandException(USR_MSG_INDEX_ERR);
        }

        // UID - 1 to get array index
        UID -= 1;

        cWhichTask = cCurBag.getTask(UID);
    }

    @Override
    public Feedback execute() throws LogicException {
        String formattedString;
        
        // Should not unmark again if it is already unmarked.
        // Does not go into undo queue if already unmarked.
        if (cWhichTask.isComplete() == false) {
            formattedString = Utilities.formatString(USR_MSG_UNMARK_FAIL, cWhichTask);
            throw new AlreadyUnmarkedException(formattedString);
        } else {
            cWhichTask.setComplete(false);
            cStore.save(cWhichTask);
        }
        
        formattedString = Utilities.formatString(USR_MSG_UNMARK_OK, cWhichTask);
        Feedback fb = new Feedback(cCommand, cIntBag, formattedString);

        return fb;
    }

    @Override
    public Feedback undo() {
        assert cWhichTask != null;

        cWhichTask.setComplete(true);
        cStore.save(cWhichTask);
        String formattedString = Utilities.formatString(USR_MSG_UNMARK_UNDO, cWhichTask);
        return new Feedback(cCommand, cIntBag, formattedString);
    }

    @Override
    public Feedback redo() throws LogicException {
        return execute();
    }
}

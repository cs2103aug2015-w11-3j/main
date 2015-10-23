package logic;

import java.util.logging.Logger;

import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class UnmarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private static final String USR_MSG_UNMARK_OK = "Unmarked!";
    private static final String USR_MSG_UNMARK_FAIL = "Already unmarked!";
    private static final String USR_MSG_UNMARK_UNDO = "Undo unmarked";

    private Command cCommand;
    private TasksBag cCurBag;
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;

    private Logger log;

    public UnmarkAction(Command command, TasksBag internalBag, StorageInterface stor) throws IntegrityCommandException {
        cCommand = command;
        cCurBag = internalBag.getFlitered();
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

        // Should not unmark again if it is already unmarked.
        // Does not go into undo queue if already unmarked.
        if (cWhichTask.isComplete() == false) {
            throw new AlreadyUnmarkedException(USR_MSG_UNMARK_FAIL);
        } else {
            cWhichTask.setComplete(false);
            cStore.save(cWhichTask);
        }

        Feedback fb = new Feedback(cCommand, cIntBag, USR_MSG_UNMARK_OK);

        return fb;
    }

    @Override
    public Feedback undo() {
        assert cWhichTask != null;

        cWhichTask.setComplete(true);
        cStore.save(cWhichTask);
        return new Feedback(cCommand, cIntBag, USR_MSG_UNMARK_UNDO);
    }

    @Override
    public Feedback redo() throws LogicException {
        return execute();
    }

}
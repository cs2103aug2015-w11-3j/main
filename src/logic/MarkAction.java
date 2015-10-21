package logic;

import java.util.logging.Logger;

import common.Task;
import common.TasksBag;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class MarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private static final String USR_MSG_MARK_OK = "Marked %1$s!";
    private static final String USR_MSG_MARK_FAIL = "Already marked %1$s!";
    private static final String USR_MSG_MARK_UNDO = "Undo mark %1$s";
    
    private Command cCommand;
    private TasksBag cCurBag;    
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cModifiedTask;
    
    private Logger log;
    
    public MarkAction(Command command, TasksBag internalBag, StorageInterface stor) {
        cCommand = command;
        cCurBag = internalBag.getFlitered();
        cIntBag = internalBag;
        cStore = stor;
        log = Logger.getLogger("MarkAction");
    }

    @Override
    public Feedback execute() throws LogicException {
        assert cCommand.getCmdType() == Command.Type.MARK : cCommand.getCmdType();

        int UID = cCommand.getTaskUID();

        if (UID <= 0) {
            throw new IntegrityCommandException(USR_MSG_INDEX_ERR);
        }

        if (UID > cCurBag.size()) {
            log.warning("Exceeded size" + UID + " " + cCurBag.size());
            throw new IntegrityCommandException(USR_MSG_INDEX_ERR);
        }

        // UID - 1 to get  array index
        UID -= 1;
        
        cModifiedTask = cIntBag.getFlitered().getTask(UID);
        assert cModifiedTask != null;
        
        // Should not mark again if it is already marked.
        // Does not go into undo queue if already marked.
        if(cModifiedTask.isComplete()){ 
            throw new AlreadyMarkedException(USR_MSG_MARK_FAIL);
        } else { 
            cModifiedTask.setComplete(true);
            cStore.save(cModifiedTask);
        }

        Feedback fb = new Feedback(cCommand, cIntBag, USR_MSG_MARK_OK);

        return fb;
    }

    @Override
    public Feedback undo() {
        assert cModifiedTask != null;

        cModifiedTask.setComplete(false);
        cStore.save(cModifiedTask);
        return new Feedback(cCommand, cCurBag, USR_MSG_MARK_UNDO);
    }

    @Override
    public Feedback redo() throws LogicException {
        return execute();
    }

}

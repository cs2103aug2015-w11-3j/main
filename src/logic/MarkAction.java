//@@author A0125546E
package logic;

import java.util.logging.Logger;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.AlreadyMarkedException;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

public class MarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private static final String USR_MSG_MARK_OK = "Marked %1$s!";
    private static final String USR_MSG_MARK_FAIL = "Already marked %1$s!";
    private static final String USR_MSG_MARK_UNDO = "Undo mark %1$s!";
    
    private Command cCommand;
    private TasksBag cCurBag;    
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;
    
    private Logger log;
    
    public MarkAction(Command command, TasksBag internalBag, StorageInterface stor) throws IntegrityCommandException {
        
        cCommand = command;
        cCurBag = internalBag.getFiltered();
        cIntBag = internalBag;
        cStore = stor;
        log = Logger.getLogger("MarkAction");
        
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
        
        cWhichTask = cCurBag.getTask(UID);
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        // Should not mark again if it is already marked.
        // Does not go into undo queue if already marked.
        if(cWhichTask.isComplete()){ 
            formattedString = Utilities.formatString(USR_MSG_MARK_FAIL, cWhichTask.getName());
            throw new AlreadyMarkedException(formattedString);
        } else { 
            cWhichTask.setComplete(true);
            cStore.save(cWhichTask);
        }
        
        formattedString =  Utilities.formatString(USR_MSG_MARK_OK, cWhichTask.getName());
        CommandFeedback fb = new CommandFeedback(cCommand, cIntBag, formattedString);

        return fb;
    }

    @Override
    public CommandFeedback undo() {
        assert cWhichTask != null;

        cWhichTask.setComplete(false);
        cStore.save(cWhichTask);
        
        String formattedString =  Utilities.formatString(USR_MSG_MARK_UNDO, cWhichTask.getName());
        return new CommandFeedback(cCommand, cIntBag, formattedString);
    }

    @Override
    public CommandFeedback redo() throws LogicException {
        return execute();
    }
}

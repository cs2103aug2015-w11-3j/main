//@@author A0125546E
package logic;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.AlreadyMarkedException;
import logic.exceptions.IllegalAccessCommandException;
import logic.exceptions.LogicException;
import parser.commands.CommandData;
import storage.StorageInterface;

public class MarkAction implements UndoableAction {

    private static final String USR_MSG_INDEX_ERR = "Provided index not on list.";
    private static final String USR_MSG_MARK_OK = "Marked %1$s!";
    private static final String USR_MSG_MARK_FAIL = "Already marked %1$s!";
    private static final String USR_MSG_MARK_UNDO = "Undo mark %1$s!";
    
    private static final String USR_MSG_MARK_WARNING_STORE_FAIL = "Fail to update the storage file!";
    
    private CommandData cCommand;
    private TasksBag cCurBag;    
    private TasksBag cIntBag;
    private StorageInterface cStore;
    private Task cWhichTask;
    private boolean cUpdateSuccess;
    
    public MarkAction(CommandData command, TasksBag internalBag, StorageInterface stor) throws IllegalAccessCommandException {
        
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

        // UID - 1 to get  array index
        UID -= 1;
        
        cWhichTask = cCurBag.getTask(UID);
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        String warningString = "";
        // Should not mark again if it is already marked.
        // Does not go into undo queue if already marked.
        if(cWhichTask.isCompleted()){ 
            formattedString = Utilities.formatString(USR_MSG_MARK_FAIL, cWhichTask.getName());
            throw new AlreadyMarkedException(formattedString);
        } else { 
            cWhichTask.setComplete(true);
            cUpdateSuccess = cStore.save(cWhichTask);
            
            if (!cUpdateSuccess) {
            	warningString = USR_MSG_MARK_WARNING_STORE_FAIL;
            }
        }
        
        formattedString =  Utilities.formatString(USR_MSG_MARK_OK, cWhichTask.getName());
        CommandFeedback fb = new CommandFeedback(cCommand, cIntBag, formattedString, warningString);

        return fb;
    }

    @Override
    public CommandFeedback undo() {
        assert cWhichTask != null;
        
        String warningString = "";

        cWhichTask.setComplete(false);
        cUpdateSuccess = cStore.save(cWhichTask);
        
        if (!cUpdateSuccess) {
        	warningString = USR_MSG_MARK_WARNING_STORE_FAIL;
        }
        
        String formattedString =  Utilities.formatString(USR_MSG_MARK_UNDO, cWhichTask.getName());
        return new CommandFeedback(cCommand, cIntBag, formattedString, warningString);
    }

    @Override
    public CommandFeedback redo() throws LogicException {
        return execute();
    }
}

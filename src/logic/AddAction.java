//@@author A0125546E
package logic;

import java.util.Date;
import java.util.Observable;

import common.Task;
import common.TasksBag;
import common.Utilities;
import javafx.collections.ObservableList;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

/**
 * Adds tasks into internal bag Undo removes the task
 */
public class AddAction implements UndoableAction {

    private static final String USR_MSG_ADD_OK = "Added %1$s!";
    private static final String USR_MSG_ADD_UNDO = "Undo adding %1$s!";
    private static final String USR_MSG_ADD_DATE_ERROR = "Failed to add! Start date is after end date!";
    private static final String USR_MSG_ADD_CLASH_WARNING_SINGLE = "Task clashes with %1$s!";
    private static final String USR_MSG_ADD_CLASH_WARNING_MANY = "Task clashes with %1$s and %2$s more!";

    private Command cCommand;
    private TasksBag cBag;
    private StorageInterface cStore;
    private Task cWhichTask;

    public AddAction(Command command, TasksBag bag, StorageInterface stor) throws IntegrityCommandException {
        cCommand = command;
        cBag = bag;
        cStore = stor;

        String name = cCommand.getText();
        Date startDate = cCommand.getStart();
        Date endDate = cCommand.getEnd();

        boolean isValidDate = Utilities.verifyDate(startDate, endDate);
        if (isValidDate) {
            cWhichTask = new Task(name, startDate, endDate);
        } else {
            throw new IntegrityCommandException(USR_MSG_ADD_DATE_ERROR);
        }
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        String warningString;
        CommandFeedback fb;

        cBag.addTask(cWhichTask);
        cStore.save(cWhichTask);

        warningString = processWarningMsg();
        
        formattedString = Utilities.formatString(USR_MSG_ADD_OK, cWhichTask.getName());
        fb = new CommandFeedback(cCommand, cBag, formattedString, warningString);

        return fb;
    }

    private String processWarningMsg() {
        String warningString;
        ObservableList<Task> clashList = cBag.findClashesWithIncomplete(cWhichTask);
        
        if(clashList == null || clashList.size() == 0){
            return "";
        }
        
        Task firstTask = clashList.get(0);
        if(clashList.size() > 1){
            int noOfOtherClashes = clashList.size() - 1;
            warningString = Utilities.formatString(USR_MSG_ADD_CLASH_WARNING_MANY, firstTask.getName(), noOfOtherClashes);
        } else {            
            warningString = Utilities.formatString(USR_MSG_ADD_CLASH_WARNING_SINGLE, firstTask.getName());
        }
        return warningString;
    }

    @Override
    public CommandFeedback undo() {
        assert cWhichTask != null;

        cStore.delete(cWhichTask);
        cBag.removeTask(cWhichTask);
        String formatted = Utilities.formatString(USR_MSG_ADD_UNDO, cWhichTask.getName());
        return new CommandFeedback(cCommand, cBag, formatted);
    }

    @Override
    public CommandFeedback redo() throws LogicException {
        return execute();
    }
}

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
        ObservableList<Task> clashList = cBag.findClashesWithIncomplete(cWhichTask);
        if(clashList != null){
            System.out.println("askjdasd");
            System.out.println(clashList.size());
        }
        
    }

    @Override
    public CommandFeedback execute() throws LogicException {
        String formattedString;
        CommandFeedback fb;

        cBag.addTask(cWhichTask);
        cStore.save(cWhichTask);

        formattedString = Utilities.formatString(USR_MSG_ADD_OK, cWhichTask.getName());
        fb = new CommandFeedback(cCommand, cBag, formattedString);

        return fb;
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

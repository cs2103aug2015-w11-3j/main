//@@author A0125546E
package logic;

import java.util.Date;
import common.Task;
import common.TasksBag;
import common.Utilities;
import javafx.collections.ObservableList;
import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;
import parser.CommandImpl;
import storage.StorageInterface;

/**
 * Adds tasks into internal bag Undo removes the task
 */
public class AddAction implements UndoableAction {

    private static final String USR_MSG_ADD_OK = "Added %1$s!";
    private static final String USR_MSG_ADD_UNDO = "Undo adding %1$s!";
    private static final String USR_MSG_ADD_ERROR_DATE = "Failed to add! Start date is after end date!";
    private static final String USR_MSG_ADD_ERROR_LONG_NAME = "Failed to add! Your task name is too long! Keep it "
            + "to less than 50 characters.";
    private static final String USR_MSG_ADD_WARNING_CLASH_SINGLE = "Your task clashes with %1$s!";
    private static final String USR_MSG_ADD_WARNING_CLASH_MANY = "Your task clashes with %1$s and %2$s more!";
    private static final String USR_MSG_ADD_WARNING_OVERDUE = "Your task is already over due!";
    private static final int NAME_LIMIT = 50; // Hard limit for user's max char

    private CommandImpl cCommand;
    private TasksBag cBag;
    private StorageInterface cStore;
    private Task cWhichTask;

    public AddAction(CommandImpl command, TasksBag bag, StorageInterface stor) throws IntegrityCommandException {
        cCommand = command;
        cBag = bag;
        cStore = stor;

        String name = cCommand.getText();
        Date startDate = cCommand.getStart();
        Date endDate = cCommand.getEnd();

        validateTaskName(name);
        validateDate(startDate, endDate);
        
        cWhichTask = new Task(name, startDate, endDate);
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
        // Over due has a higher priority than clashes
        if (cWhichTask.isOverDue()) {
            return USR_MSG_ADD_WARNING_OVERDUE;
        }

        String warningString;
        ObservableList<Task> clashList = cBag.findClashesWithIncomplete(cWhichTask);

        if (clashList == null || clashList.size() == 0) {
            return "";
        }

        Task firstTask = clashList.get(0);
        if (clashList.size() > 1) {
            int noOfOtherClashes = clashList.size() - 1;
            warningString = Utilities.formatString(USR_MSG_ADD_WARNING_CLASH_MANY, firstTask.getName(),
                    noOfOtherClashes);
        } else {
            warningString = Utilities.formatString(USR_MSG_ADD_WARNING_CLASH_SINGLE, firstTask.getName());
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

    private void validateDate(Date startDate, Date endDate) throws IntegrityCommandException {
        boolean isValidDate = Utilities.verifyDate(startDate, endDate);
        
        if (isValidDate == false) {
            throw new IntegrityCommandException(USR_MSG_ADD_ERROR_DATE);
        }
    }

    private void validateTaskName(String name) throws IntegrityCommandException {
        if (name.length() > NAME_LIMIT) {
            throw new IntegrityCommandException(USR_MSG_ADD_ERROR_LONG_NAME);
        }
    }
}

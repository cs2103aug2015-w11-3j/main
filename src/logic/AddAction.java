//@@author A0125546E
package logic;

import java.util.Date;

import common.Task;
import common.TasksBag;
import common.Utilities;
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

    }

    @Override
    public Feedback execute() throws LogicException {
        String formattedString;
        Feedback fb;

        cBag.addTask(cWhichTask);
        cStore.save(cWhichTask);

        formattedString = Utilities.formatString(USR_MSG_ADD_OK, cWhichTask.getName());
        fb = new Feedback(cCommand, cBag, formattedString);

        return fb;
    }

    @Override
    public Feedback undo() {
        assert cWhichTask != null;

        cStore.delete(cWhichTask);
        cBag.removeTask(cWhichTask);
        String formatted = Utilities.formatString(USR_MSG_ADD_UNDO, cWhichTask.getName());
        return new Feedback(cCommand, cBag, formatted);
    }

    @Override
    public Feedback redo() throws LogicException {
        return execute();
    }
}

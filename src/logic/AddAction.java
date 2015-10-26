package logic;

import java.util.Date;

import common.Task;
import common.TasksBag;
import common.Utilities;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

/**
 * 
 * @author MonoChrome Adds tasks into internal bag Undo removes the task
 */
public class AddAction implements UndoableAction {

    private static final String USR_MSG_ADD_ERROR = "Failed to store to storage";
    private static final String USR_MSG_ADD_OK = "Added %1$s!";
    private static final String USR_MSG_ADD_UNDO = "Undo adding %1$s!";

    private Command cCommand;
    private TasksBag cBag;
    private StorageInterface cStore;
    private Task cWhichTask;

    public AddAction(Command command, TasksBag bag, StorageInterface stor) {
        cCommand = command;
        cBag = bag;
        cStore = stor;

        String name = cCommand.getName();
        Date startDate = cCommand.getStart();
        Date endDate = cCommand.getEnd();

        cWhichTask = new Task(name, startDate, endDate);
    }

    @Override
    public Feedback execute() throws LogicException {
        String formattedString;
        Feedback fb;

        boolean addStatus = cStore.save(cWhichTask);

        if (addStatus) {
            cBag.addTask(cWhichTask);

            formattedString = Utilities.formatString(USR_MSG_ADD_OK, cWhichTask.getName());
            fb = new Feedback(cCommand, cBag, formattedString);

            return fb;
        } else {
            formattedString = Utilities.formatString(USR_MSG_ADD_ERROR, cWhichTask.getName());

            throw new LogicException(formattedString);
        }
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

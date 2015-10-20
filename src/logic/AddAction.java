package logic;

import java.util.Date;

import common.Task;
import common.TasksBag;
import logic.exceptions.LogicException;
import parser.Command;
import storage.StorageInterface;

/**
 * 
 * @author MonoChrome Adds tasks into internal bag Undo removes the task
 */
public class AddAction implements UndoableAction {

    private static final String USR_MSG_ADD_ERROR = "Failed to store to storage";
    private static final String USR_MSG_ADD_OK = "Added!";
    
    private Command cCommand;
    private TasksBag cBag;
    private StorageInterface cStore;
    private Task cCreatedTask;

    public AddAction(Command command, TasksBag bag, StorageInterface stor) {
        cCommand = command;
        cBag = bag;
        cStore = stor;
    }

    @Override
    public Feedback execute() throws LogicException {
        assert cCommand.getCmdType() == Command.Type.Add : cCommand.getCmdType();

        Feedback fb;

        String name = cCommand.getName();
        Date startDate = cCommand.getStart();
        Date endDate = cCommand.getEnd();

        cCreatedTask = new Task(name, startDate, endDate);

        boolean addStatus = cStore.save(cCreatedTask);

        if (addStatus) {
            cBag.addTask(cCreatedTask);
            fb = new Feedback(cCommand, cBag, USR_MSG_ADD_OK);
        } else {
            throw new LogicException(USR_MSG_ADD_ERROR);
        }

        return fb;
    }

    @Override
    public void undo() {
        cStore.delete(cCreatedTask);
        cBag.removeTask(cCreatedTask);
    }

    @Override
    public void redo() throws LogicException {
        execute();
    }

}
